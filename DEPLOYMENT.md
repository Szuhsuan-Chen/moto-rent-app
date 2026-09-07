# 部署架構與 CI/CD 說明

## 整體架構

```
GitHub push/PR
      │
      ▼
┌─────────────┐   push 到 main 且成功後觸發
│  CI workflow │ ──────────────────────────┐
│ (test + build)│                            │
└─────────────┘                            ▼
                                    ┌──────────────┐
                                    │  CD workflow  │
                                    │ 1. 用 OIDC 換 AWS 臨時憑證
                                    │ 2. docker build + push image → ECR
                                    │ 3. SSH 連進 EC2
                                    │ 4. docker compose pull + up -d
                                    └──────────────┘
                                            │
                                            ▼
                              ┌─────────────────────────┐
                              │          EC2             │
                              │ ┌────────┐  ┌──────────┐ │
                              │ │ nginx  │→│  backend │ │
                              │ │(80,前端)│  │  (8080)  │ │
                              │ └────────┘  └────┬─────┘ │
                              │                   ▼       │
                              │              ┌────────┐  │
                              │              │  MySQL │  │
                              │              └────────┘  │
                              └─────────────────────────┘
```

CI 和 CD 分開兩個 workflow：CI 對「所有 push / PR」跑測試把關，CD 只在 CI 針對 `main` 分支跑成功後才觸發（`workflow_run` trigger），確保不會把沒過測試的程式碼部署上線。

## 一次性的 AWS 手動設置

以下這些是「基礎設施」，只需要建立一次，之後 pipeline 每次跑不會重建它們。

### 1. ECR（存放 Docker image 的地方）

```bash
aws ecr create-repository --repository-name moto-rent-backend
aws ecr create-repository --repository-name moto-rent-frontend
```

### 2. GitHub OIDC 讓 Actions 免存 AWS 長期金鑰

建立 IAM Identity Provider（如果帳號裡還沒有）：

```bash
aws iam create-open-id-connect-provider \
  --url https://token.actions.githubusercontent.com \
  --client-id-list sts.amazonaws.com \
  --thumbprint-list 6938fd4d98bab03faadb97b34396831e3780aea1
```

建立一個 IAM Role，信任政策（trust policy)只允許這個 repo 的 GitHub Actions 來扮演，把 `<AWS_ACCOUNT_ID>` 換成你的帳號 ID：

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::<AWS_ACCOUNT_ID>:oidc-provider/token.actions.githubusercontent.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
        },
        "StringLike": {
          "token.actions.githubusercontent.com:sub": "repo:Szuhsuan-Chen/moto-rent-app:ref:refs/heads/main"
        }
      }
    }
  ]
}
```

這個 Role 只需要能推 image 到 ECR，掛 `AmazonEC2ContainerRegistryPowerUser` 這個 AWS managed policy 就夠。

**為什麼用 OIDC 而不是把 AWS access key 存進 GitHub Secrets？** 這是業界現在的標準做法——長期存在的 access key 一旦外洩沒有時效性，OIDC 換到的是幾十分鐘就過期的臨時憑證，而且信任政策把範圍鎖死在「這個 repo 的 main 分支」，別的 repo 或分支換不到。這點在面試被問「怎麼做 AWS 認證」時是很好的加分回答。

### 3. EC2

- 開一台 EC2(t3.micro 等 free tier 額度內即可,Amazon Linux 2023 或 Ubuntu)
- Security Group:允許 22(SSH,建議限定你自己的 IP)、80(HTTP,對外)
- 掛一個 IAM Instance Profile,附上 `AmazonEC2ContainerRegistryReadOnly`,讓 EC2 上的 `docker login` 能拉 image 而不用另外存憑證
- SSH 進去後安裝 Docker + docker compose plugin,並把 repo clone 一份:
  ```bash
  git clone https://github.com/Szuhsuan-Chen/moto-rent-app.git ~/moto-rent-app
  cd ~/moto-rent-app
  cp .env.example .env   # 填入正式環境的 DB_USER / DB_PASSWORD / DB_NAME / MYSQL_ROOT_PASSWORD
  ```
  之後 CD workflow 每次都只是 `git checkout` 到新的 commit、`docker compose pull` 拉新 image、`up -d` 重啟,不會重新 clone。

## GitHub Secrets 清單

到 repo 的 Settings → Secrets and variables → Actions 設定:

| Secret | 說明 |
|---|---|
| `AWS_DEPLOY_ROLE_ARN` | 上面建立的 IAM Role ARN |
| `AWS_REGION` | 例如 `ap-northeast-1` |
| `EC2_HOST` | EC2 的公開 IP 或網域 |
| `EC2_USER` | SSH 登入帳號(Ubuntu 預設 `ubuntu`,Amazon Linux 預設 `ec2-user`) |
| `EC2_SSH_KEY` | EC2 key pair 的私鑰內容(`.pem` 檔案內容整份貼上) |

## 這次連帶修正的東西

- 前端原本把後端網址寫死成 `http://localhost:5001`——這種寫法只有在瀏覽器和後端跑在同一台機器才恰好能動,部署到 EC2 上外部使用者根本連不到。改成前端一律呼叫相對路徑 `/api/...`,本機 `npm run dev` 靠 [vite.config.js](frontend/vite.config.js) 的 dev proxy 轉發,正式環境靠 [nginx.conf](frontend/nginx.conf) reverse proxy 轉發,前端程式碼完全不用知道後端實際網址,也順便避開瀏覽器端的 CORS 問題。
- `frontend/Dockerfile`(開發用,跑 `npm run dev`)保留不動,另外新增 [frontend/Dockerfile.prod](frontend/Dockerfile.prod) 做 production build(multi-stage:Vite build → nginx serve 靜態檔),兩者用途分開。

## 本機開發

`docker-compose.yml`(開發用)完全沒受影響,一樣是 `docker compose up`。`docker-compose.prod.yml` 只在 EC2 上、由 CD workflow 使用。

# 摩托車租賃應用程式

這是一個使用 React + Spring Boot + MyBatis + MySQL + Docker 構建的摩托車租賃應用程式。

## 技術棧

- **前端**: React 19 + Vite + Bootstrap 5
- **後端**: Spring Boot 3 (Java 21) + MyBatis
- **資料庫**: MySQL 8.0
- **容器化**: Docker + Docker Compose

## 項目結構

```
moto-rent-app/
├── docker-compose.yml          # Docker Compose 配置
├── .env.example                 # 環境變數範本
├── frontend/                    # React 前端應用
│   ├── Dockerfile
│   ├── package.json
│   └── src/
├── backend/                     # Spring Boot 後端 API
│   ├── Dockerfile
│   ├── pom.xml
│   ├── openapi.yaml             # OpenAPI 3.0 規範
│   ├── API_DOCUMENTATION.md     # API 文件
│   └── src/main/java/com/motorent/
│       ├── controller/          # REST controllers
│       ├── service/              # 業務邏輯
│       ├── repository/           # MyBatis mapper 介面
│       ├── entity/ dto/          # 實體與資料傳輸物件
│       └── resources/mapper/     # MyBatis XML mapper
├── db/
│   └── init.sql                 # 資料庫初始化腳本
└── docs/
    └── SQL_QUERIES.md
```

## 快速開始（Docker，推薦）

### 前置條件

- 安裝 [Docker Desktop](https://www.docker.com/get-started)（已內建 Docker Compose v2）

確認安裝：
```bash
docker --version
docker compose version
```

### 運行應用程式

1. **進入專案目錄**:
   ```bash
   cd moto-rent-app
   ```

2. **設置環境變數**:
   ```bash
   # 複製環境變數範本檔案
   cp .env.example .env

   # 編輯 .env 檔案，填入你的資料庫密碼
   # DB_PASSWORD=your_secure_password_here
   # MYSQL_ROOT_PASSWORD=your_secure_password_here
   ```
   注意：`DB_HOST` 必須維持 `db`（Docker 內部服務名稱），不要改成 `localhost`。

3. **使用 Docker Compose 建置並啟動所有服務**:
   ```bash
   docker compose up --build
   ```
   首次執行需下載 image 並編譯後端，約需 3–5 分鐘。啟動順序由 `depends_on` 控制：**MySQL → Backend → Frontend**。

4. **等待所有服務啟動完成**，然後開啟瀏覽器訪問:

   | 服務 | 網址 |
   |------|------|
   | 前端應用 | http://localhost:3000 |
   | 後端 API | http://localhost:5001 |
   | MySQL 資料庫 | localhost:3307 |

### 停止應用程式

```bash
docker compose down
```

如果想要清除所有資料（包括資料庫）:
```bash
docker compose down -v
```

## 本地開發模式（不使用 Docker）

需要本機已安裝 Node.js 18+、JDK 21、Maven，以及一個可連線的 MySQL 實例。

### 前端

```bash
cd frontend
npm install
npm run dev
```
預設會在 http://localhost:5173 啟動 Vite 開發伺服器。

### 後端

```bash
cd backend

# 設定環境變數（需先有本機 MySQL 且已建立資料庫）
export DB_HOST=localhost
export DB_USER=root
export DB_PASSWORD=your_password
export DB_NAME=mydb

mvn spring-boot:run
```
後端預設監聽 8080 埠（`backend/src/main/resources/application.yml`）。

### 資料庫

MySQL 需先手動執行 `db/init.sql` 建立資料表與範例資料（Docker 模式下會在容器第一次啟動時自動執行）。

## API 文檔

本專案提供完整的 RESTful API，詳細規範以 OpenAPI 3.0 撰寫。

- **OpenAPI 規範檔案**: [backend/openapi.yaml](backend/openapi.yaml)
- **API 文檔**: [backend/API_DOCUMENTATION.md](backend/API_DOCUMENTATION.md)

### Swagger UI（推薦，可直接測試 API）

後端已整合 springdoc-openapi，啟動後端服務（Docker 或本地）即可開啟 Swagger UI，直接在網頁上瀏覽並測試所有 API：

```
http://localhost:5001/swagger-ui/index.html
```

> 若以 `mvn spring-boot:run` 本地啟動（埠 8080），則改用 `http://localhost:8080/swagger-ui/index.html`。

可使用以下工具查看與測試 API：
- **Swagger UI**（見上方）- 內建於後端服務，免安裝即可互動測試
- [Swagger Editor](https://editor.swagger.io/) - 貼上 `openapi.yaml` 內容即可查看互動式文檔
- [Postman](https://www.postman.com/) - 匯入 `openapi.yaml` 檔案以自動生成 API 測試集合

### 主要 API 端點

#### 健康檢查
- `GET /api/health` - 檢查 API 服務和資料庫連接狀態

#### 摩托車相關
- `GET /api/motorcycles` - 獲取摩托車列表（支援 `branch`, `date`, `start_time`, `duration`, `price_category`, `moto_type`, `brand` 等篩選參數）
- `GET /api/motorcycles/{motorcycle_id}` - 獲取特定摩托車詳細資訊及所有時段價格

#### 租賃相關
- `POST /api/rentals` - 建立新的租賃預約
- `GET /api/rentals` - 獲取租賃記錄列表（支援狀態、分店、電話號碼篩選）
- `GET /api/rentals/{rental_id}` - 獲取特定租賃記錄詳情
- `PATCH /api/rentals/{rental_id}` - 更新租賃狀態
- `DELETE /api/rentals/{rental_id}` - 刪除租賃記錄

#### 基本資料
- `GET /api/brands` - 獲取所有摩托車品牌
- `GET /api/types` - 獲取所有摩托車類型
- `GET /api/branches` - 獲取所有分店資訊
- `GET /api/price-categories` - 獲取價格分類資訊

### 範例 API 調用

```bash
# 檢查 API 健康狀態
curl http://localhost:5001/api/health

# 獲取所有摩托車
curl http://localhost:5001/api/motorcycles

# 根據分店和日期篩選摩托車
curl "http://localhost:5001/api/motorcycles?branch=taipei&date=2025-12-15&duration=24h"

# 建立租賃預約
curl -X POST http://localhost:5001/api/rentals \
  -H "Content-Type: application/json" \
  -d '{
    "motorcycle_id": 1,
    "customer_name": "王小明",
    "customer_phone": "0912345678",
    "customer_email": "wang@email.com",
    "branch": "taipei",
    "rental_date": "2025-12-15",
    "start_time": "14:00",
    "duration": "24h",
    "notes": "請準備大型安全帽"
  }'
```

> 注意：以上為透過 Docker Compose 啟動時的後端 host 端口（5001）。若以 `mvn spring-boot:run` 本地啟動，後端埠為 8080。

## 資料庫配置

資料庫配置透過環境變數檔案 (`.env`) 管理：

- **資料庫名稱**: mydb
- **用戶名**: root
- **密碼**: 在 `.env` 檔案中設置
- **Host 端口**（Docker）: 3307（容器內部仍為 3306）

**重要安全提示**:
- 請勿將 `.env` 檔案提交到 Git
- `.env.example` 提供了環境變數的範本，每位開發者應複製後設置自己的密碼

MySQL 資料庫會在第一次啟動時自動執行 `db/init.sql` 腳本來創建表格和插入範例資料。

## 故障排除

1. **容器啟動失敗** — 確保 Docker 正在運行；檢查端口 3000、5001、3307 是否被占用。
2. **後端一直重啟或連線失敗** — 後端會等 MySQL 健康檢查通過才啟動，初次啟動可能需要等待 30–60 秒，屬正常現象。可用 `docker compose logs db` 查看資料庫日誌。
3. **前端無法獲取資料** — 確認後端已啟動（`curl http://localhost:5001/api/health`），並檢查瀏覽器開發者工具的 Network 標籤。

### 查看日誌

```bash
# 查看所有服務的日誌
docker compose logs -f

# 查看特定服務的日誌
docker compose logs -f frontend
docker compose logs -f backend
docker compose logs -f db
```

## 功能特色

- 響應式網頁設計
- 摩托車資料展示、品牌與類型篩選
- 完整的租賃預約 RESTful API
- Docker 容器化部署，MySQL 資料持久化

## 貢獻

歡迎提交 Pull Request 或開啟 Issue 來改進這個項目！

## 授權

MIT License

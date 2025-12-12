# 摩托車租賃應用程式

這是一個使用 React + Flask + MySQL + Docker 構建的摩托車租賃應用程式。

## 技術棧

- **前端**: React 18 + Vite + Bootstrap 5
- **後端**: Python Flask + SQLAlchemy
- **資料庫**: MySQL 8.0
- **容器化**: Docker + Docker Compose

## 項目結構

```
moto-rent-app/
├── docker-compose.yml          # Docker Compose 配置
├── frontend/                   # React 前端應用
│   ├── Dockerfile
│   ├── package.json
│   ├── src/
│   │   ├── App.jsx
│   │   └── components/
└── backend/                    # Flask 後端 API
│   ├── Dockerfile
│   ├── app.py
│   └── requirements.txt
└── db/                        # 資料庫初始化腳本
    └── init.sql
```

## 快速開始

### 前置條件

- 安裝 [Docker](https://www.docker.com/get-started)
- 安裝 [Docker Compose](https://docs.docker.com/compose/install/)

### 運行應用程式

1. **克隆存儲庫並進入目錄**:
   ```bash
   cd moto-rent-app
   ```

2. **設置環境變數**:
   ```bash
   # 複製環境變數範本檔案
   cp .env.example .env
   
   # 編輯 .env 檔案，填入您的資料庫密碼
   # DB_PASSWORD=your_secure_password_here
   # MYSQL_ROOT_PASSWORD=your_secure_password_here
   ```

3. **使用 Docker Compose 啟動所有服務**:
   ```bash
   docker-compose up --build
   ```

4. **等待所有服務啟動完成**，然後開啟瀏覽器訪問:
   - 前端應用: http://localhost:3000
   - 後端 API: http://localhost:5000
   - MySQL 資料庫: localhost:3306

### 停止應用程式

```bash
docker-compose down
```

如果想要清除所有數據（包括資料庫）:
```bash
docker-compose down -v
```

## API 端點

本專案提供完整的 RESTful API，詳細的 API 文檔以 OpenAPI 3.0 規範撰寫。

### API 文檔

- **OpenAPI 規範檔案**: `backend/openapi.yaml`
- **API 文檔**: `backend/API_DOCUMENTATION.md`

您可以使用以下工具查看和測試 API：
- [Swagger Editor](https://editor.swagger.io/) - 將 `openapi.yaml` 內容貼上即可查看互動式文檔
- [Swagger UI](https://swagger.io/tools/swagger-ui/) - 本地部署 API 文檔瀏覽器
- [Postman](https://www.postman.com/) - 匯入 `openapi.yaml` 檔案以自動生成 API 測試集合
- VS Code 擴充套件: OpenAPI (Swagger) Editor

#### 使用 Postman 測試 API
1. 開啟 Postman
2. 點擊 **Import** 按鈕
3. 選擇 `backend/openapi.yaml` 檔案
4. Postman 會自動生成所有 API 端點的請求範例
5. 確保後端服務運行在 `http://localhost:5000` 後即可開始測試

### 主要 API 端點

#### 健康檢查
- `GET /api/health` - 檢查 API 服務和資料庫連接狀態

#### 摩托車相關
- `GET /api/motorcycles` - 獲取摩托車列表（支援多種篩選參數）
  - 參數: `branch`, `date`, `start_time`, `duration`, `price_category`, `moto_type`, `brand`
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
curl http://localhost:5000/api/health

# 獲取所有摩托車
curl http://localhost:5000/api/motorcycles

# 根據分店和日期篩選摩托車
curl "http://localhost:5000/api/motorcycles?branch=taipei&date=2025-12-15&duration=24h"

# 獲取特定摩托車詳情
curl http://localhost:5000/api/motorcycles/1

# 建立租賃預約
curl -X POST http://localhost:5000/api/rentals \
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

# 獲取所有品牌
curl http://localhost:5000/api/brands
```

## 資料庫配置

資料庫配置透過環境變數檔案 (`.env`) 管理：

- **資料庫名稱**: mydb
- **用戶名**: root
- **密碼**: 在 `.env` 檔案中設置
- **端口**: 3306

**重要安全提示**: 
- ⚠️ 請勿將 `.env` 檔案提交到 Git
- ✅ `.env.example` 提供了環境變數的範本
- ✅ 每位開發者應該複製 `.env.example` 並設置自己的密碼

MySQL 資料庫會在第一次啟動時自動執行 `db/init.sql` 腳本來創建表格和插入範例資料。

## 開發模式

如果你想要在開發模式下運行應用程式：

### 前端開發

```bash
cd frontend
npm install
npm run dev
```

### 後端開發

```bash
cd backend
pip install -r requirements.txt
python app.py
```

### 資料庫連接

開發模式下確保 MySQL 正在運行，並從 `.env` 檔案載入環境變數，或手動設置:

```bash
# Windows PowerShell
$env:DB_HOST="localhost"
$env:DB_USER="root"
$env:DB_PASSWORD="your_password"
$env:DB_NAME="mydb"

# Linux/macOS
export DB_HOST=localhost
export DB_USER=root
export DB_PASSWORD=your_password
export DB_NAME=mydb
```

## 功能特色

- ✅ 響應式網頁設計
- ✅ 摩托車資料展示
- ✅ 品牌和類型篩選
- ✅ RESTful API
- ✅ Docker 容器化部署
- ✅ MySQL 資料持久化
- ✅ 錯誤處理和備援機制

## 故障排除

### 常見問題

1. **容器啟動失敗**
   - 確保 Docker 正在運行
   - 檢查端口 3000, 5000, 3306 是否被占用

2. **資料庫連接失敗**
   - 等待 MySQL 容器完全啟動（通常需要 30-60 秒）
   - 檢查 `docker-compose logs db` 查看資料庫日誌

3. **前端無法獲取資料**
   - 確保後端服務正在運行
   - 檢查瀏覽器開發者工具的網路標籤

### 查看日誌

```bash
# 查看所有服務的日誌
docker-compose logs

# 查看特定服務的日誌
docker-compose logs frontend
docker-compose logs backend
docker-compose logs db
```

## 貢獻

歡迎提交 Pull Request 或開啟 Issue 來改進這個項目！

## 授權

MIT License
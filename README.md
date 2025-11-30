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

### 摩托車相關 API

- `GET /api/motorcycles` - 獲取所有摩托車
- `GET /api/motorcycles/<id>` - 獲取特定摩托車
- `GET /api/motorcycles/brand/<brand>` - 根據品牌篩選
- `GET /api/motorcycles/type/<type>` - 根據類型篩選

### 範例 API 調用

```bash
# 獲取所有摩托車
curl http://localhost:5000/api/motorcycles

# 獲取特定品牌的摩托車
curl http://localhost:5000/api/motorcycles/brand/KAWASAKI

# 獲取特定類型的摩托車
curl http://localhost:5000/api/motorcycles/type/跑車
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
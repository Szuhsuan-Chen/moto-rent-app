# 使用 Node.js 18 官方基礎鏡像
FROM node:18-alpine

# 設定工作目錄
WORKDIR /app

# 複製 package.json 和 package-lock.json (如果存在)
COPY package*.json ./

# 安裝依賴套件
RUN npm install

# 複製所有原始碼
COPY . .

# 暴露開發服務器端口
EXPOSE 3000

# 啟動開發服務器
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
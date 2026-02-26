# 使用 Python 3.11 官方基礎鏡像
FROM python:3.11-slim

# 設定工作目錄
WORKDIR /app

# 複製需求文件
COPY requirements.txt .

# 安裝 Python 依賴套件
RUN pip install --no-cache-dir -r requirements.txt

# 複製應用程式代碼
COPY . .

# 暴露端口
EXPOSE 5000

# 設定環境變數
ENV FLASK_APP=app.py
ENV FLASK_ENV=production

# 啟動應用程式
ENTRYPOINT ["python"]
CMD ["app.py"]
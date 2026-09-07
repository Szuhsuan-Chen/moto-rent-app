import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    host: '0.0.0.0',
    proxy: {
      // 開發時把 /api 轉發給後端，跟 production 的 nginx reverse proxy 邏輯一致
      // 裸 npm run dev 用預設值；docker-compose 內跑則靠 VITE_PROXY_TARGET 指到 backend container
      '/api': {
        target: process.env.VITE_PROXY_TARGET || 'http://localhost:5001',
        changeOrigin: true
      }
    }
  }
})

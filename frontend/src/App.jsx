import { useState, useEffect } from 'react'
import './App.css'

import Navbar from './components/Navbar'
import Hero from './components/Hero'
import Footer from './components/Footer'
import BikeCard from './components/BikeCard'

const toCamelCase = (str) => str.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase())
const transformKeys = (obj) => {
  if (Array.isArray(obj)) return obj.map(transformKeys)
  if (obj !== null && typeof obj === 'object')
    return Object.fromEntries(Object.entries(obj).map(([k, v]) => [toCamelCase(k), transformKeys(v)]))
  return obj
}

function App() {
  const [bikesData, setBikesData] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [currentFilters, setCurrentFilters] = useState({})

  // 從後端 API 獲取摩托車資料
  const fetchMotorcycles = async (filters = {}) => {
    try {
      setLoading(true)
      setError(null)
      
      // branch/date/start_time/duration 要嘛全給、要嘛全不給：
      // 全給才是「查可用性」，走 /motorcycles/availability；否則是單純瀏覽目錄，走 /motorcycles
      const hasFullSearch = filters.branch && filters.date && filters.startTime && filters.duration
      const endpoint = hasFullSearch ? '/api/motorcycles/availability' : '/api/motorcycles'

      const params = new URLSearchParams()
      if (hasFullSearch) {
        params.append('branch', filters.branch)
        params.append('date', filters.date)
        params.append('start_time', filters.startTime)
        params.append('duration', filters.duration)
      }
      if (filters.priceCategory) params.append('price_category', filters.priceCategory)

      const url = `http://localhost:5001${endpoint}${params.toString() ? '?' + params.toString() : ''}`
      const response = await fetch(url)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const result = await response.json()
      // Backend returns { count, data }, extract the data array
      // transformKeys: 在 API 邊界把 snake_case JSON 轉成 JS 慣用的 camelCase
      setBikesData(transformKeys(result.data || []))
      setCurrentFilters(filters)
    } catch (error) {
      console.error('獲取摩托車資料時發生錯誤:', error)
      setError(error.message)
        
        // 如果 API 失敗，使用預設資料作為備份
        const fallbackData = [
          {
            id: 1,
            image: "/KAWASAKI_NINJA_400.png",
            title: "KAWASAKI NINJA 400",
            brand: "KAWASAKI",
            price: "2400",
            motoType: "跑車",
            engineDisplacement: 140,
            maxHorsepower: 45,
            maxTorque: 38,
            engineType: "水冷四行程單汽缸 SOHC 4V",
            fuelTankCapacity: 14.0,
            seatHeight: 785,
            weight: 168
          },
          {
            id: 2,
            image: "/YAMAHA_YZF_R3.png",
            title: "YAMAHA YZF-R3",
            brand: "YAMAHA",
            price: "2200",
            motoType: "跑車",
            engineDisplacement: 321,
            maxHorsepower: 42,
            maxTorque: 29.6,
            engineType: "水冷四行程並列雙汽缸 DOHC 4V",
            fuelTankCapacity: 14.0,
            seatHeight: 780,
            weight: 169
          }
        ]
        setBikesData(fallbackData)
      } finally {
        setLoading(false)
      }
    }

  // 初始載入所有資料
  useEffect(() => {
    fetchMotorcycles()
  }, [])

  // 處理查詢按鈕點擊
  const handleSearch = (filters) => {
    fetchMotorcycles(filters)
  }

  // 重置到初始狀態
  const handleReset = () => {
    fetchMotorcycles()
  }

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">載入中...</span>
        </div>
      </div>
    )
  }

  return (
    // In React, a component can only return a single root element
    <>
      {/* 導覽列 Navbar */}
      <Navbar/>

      {/* Hero 區塊 */}
      <Hero onSearch={handleSearch}/>

      {/* 內容區塊（車輛卡片之後會放這） */}
      <div className="container py-5">
        {/* 顯示篩選資訊 */}
        {Object.keys(currentFilters).some(key => currentFilters[key]) && (
          <div className="alert alert-info mb-4" role="alert">
            <strong>篩選結果：</strong>
            {currentFilters.branch && ` 分店: ${currentFilters.branch}`}
            {currentFilters.date && ` | 日期: ${currentFilters.date}`}
            {currentFilters.startTime && ` | 時間: ${currentFilters.startTime}`}
            {currentFilters.duration && ` | 時長: ${currentFilters.duration}`}
            {currentFilters.priceCategory && ` | 車型: ${currentFilters.priceCategory}`}
            <span className="ms-3">共找到 {bikesData.length} 台車</span>
          </div>
        )}
        
        {/* 顯示錯誤訊息（如果有的話） */}
        {error && (
          <div className="alert alert-warning mb-4" role="alert">
            <strong>注意：</strong> 無法連接到後端服務器 ({error})，正在顯示備份資料。
          </div>
        )}
        
        <div className="row">
          {/* 動態產生車輛卡片 */}
          {bikesData.map(bike => (
            <BikeCard
              key={bike.id}
              id={bike.id}
              image={bike.image}
              title={bike.title}
              brand={bike.brand}
              price={bike.price}
              motoType={bike.motoType}
              engineDisplacement={bike.engineDisplacement}
              maxHorsepower={bike.maxHorsepower}
              maxTorque={bike.maxTorque}
              engineType={bike.engineType}
              fuelTankCapacity={bike.fuelTankCapacity}
              seatHeight={bike.seatHeight}
              weight={bike.weight}
              availability={bike.availability}
              filterData={{
                branch: currentFilters.branch || '',
                date: currentFilters.date || '',
                startTime: currentFilters.startTime || '',
                duration: currentFilters.duration || ''
              }}
              onReset={handleReset}
            />
          ))}
        </div>
        
        {/* 如果沒有資料則顯示訊息 */}
        {bikesData.length === 0 && !loading && (
          <div className="text-center py-5">
            <h4>目前沒有可用的摩托車資料</h4>
            <p className="text-muted">請稍後再試或聯繫系統管理員。</p>
          </div>
        )}
      </div>

      {/* footer */}
      <Footer/>
    </>
  )
}

export default App

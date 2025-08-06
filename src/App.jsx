import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'

import Navbar from './components/Navbar'
import Hero from './components/Hero'
import Searchbar from './components/Searchbar'

function App() {
  return (
    // In React, a component can only return a single root element
    <>
      {/* 導覽列 Navbar */}
      <Navbar/>

      {/* Hero 區塊 */}
      <Hero/>

      {/* 搜尋列 Searchbar */}
      <Searchbar/>

      {/* 內容區塊（車輛卡片之後會放這） */}
      <div className="container py-5">
        {/* <h2 className="mb-4">🔥 熱門車款</h2> */}
        <h2 className="mb-4">🔥 XXXX</h2>
        <div className="row">
          {/* 每張車輛卡片 */}
          <div className="col-md-4 mb-4">
            <div className="card">
              <img src="https://via.placeholder.com/400x250" className="card-img-top" alt="bike" />
              <div className="card-body">
                <h5 className="card-title">Honda 140</h5>
                <p className="card-text">引擎 140cc / 時速 80km/h</p>
                <p className="card-text fw-bold">$56 / 每小時</p>
                <a href="#" className="btn btn-primary">Rent Now</a>
              </div>
            </div>
          </div>
          {/* 更多車卡可以用 map 動態產生 */}
        </div>
      </div>
    </>
  )
}

export default App

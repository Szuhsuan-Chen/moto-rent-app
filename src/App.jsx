import { useState } from 'react'
import './App.css'

import Navbar from './components/Navbar'
import Hero from './components/Hero'
import Footer from './components/Footer'
import BikeCard from './components/BikeCard'

function App() {
  // 模擬後端傳來的車輛資料
  const bikesData = [
    {
      id: 1,
      image: "/KAWASAKI_NINJA_400.png",
      title: "KAWASAKI NINJA 400",
      brand: "KAWASAKI",
      price: "2400",
      motoType: "跑車",
      engineDisplacement: "140cc",
      maxHorsepower: "45hp",
      maxTorque: "38Nm",
      engineType: "水冷四行程單汽缸 SOHC 4V",
      fuelTankCapacity: "14L",
      seatHeight: "785mm",
      weight: "168kg"
    },
    {
      id: 2,
      image: "/YAMAHA_YZF_R3.png",
      title: "YAMAHA YZF-R3",
      brand: "YAMAHA",
      price: "2200",
      motoType: "跑車",
      engineDisplacement: "321cc",
      maxHorsepower: "42hp",
      maxTorque: "29.6Nm",
      engineType: "水冷四行程並列雙汽缸 DOHC 4V",
      fuelTankCapacity: "14L",
      seatHeight: "780mm",
      weight: "169kg"
    },
    {
      id: 3,
      image: "/HONDA_CB650R.png",
      title: "HONDA CB650R",
      brand: "HONDA",
      price: "3200",
      motoType: "街車",
      engineDisplacement: "649cc",
      maxHorsepower: "95hp",
      maxTorque: "64Nm",
      engineType: "水冷四行程並列四汽缸 DOHC 4V",
      fuelTankCapacity: "15.4L",
      seatHeight: "810mm",
      weight: "200kg"
    },
    {
      id: 4,
      image: "/DUCATI_MONSTER_821.png",
      title: "DUCATI MONSTER 821",
      brand: "DUCATI",
      price: "4500",
      motoType: "街車",
      engineDisplacement: "821cc",
      maxHorsepower: "109hp",
      maxTorque: "86Nm",
      engineType: "水冷四行程L型雙汽缸 Desmodromic 4V",
      fuelTankCapacity: "17.5L",
      seatHeight: "785mm",
      weight: "205kg"
    },
    {
      id: 5,
      image: "/BMW_S1000RR.png",
      title: "BMW S1000RR",
      brand: "BMW",
      price: "6800",
      motoType: "超跑",
      engineDisplacement: "999cc",
      maxHorsepower: "207hp",
      maxTorque: "113Nm",
      engineType: "水冷四行程並列四汽缸 DOHC 4V",
      fuelTankCapacity: "16.5L",
      seatHeight: "824mm",
      weight: "197kg"
    },
    {
      id: 6,
      image: "/HARLEY_DAVIDSON_SPORTSTER.png",
      title: "HARLEY-DAVIDSON SPORTSTER",
      brand: "HARLEY-DAVIDSON",
      price: "3800",
      motoType: "美式巡航",
      engineDisplacement: "883cc",
      maxHorsepower: "50hp",
      maxTorque: "68Nm",
      engineType: "氣冷四行程V型雙汽缸 OHV 2V",
      fuelTankCapacity: "12.5L",
      seatHeight: "760mm",
      weight: "256kg"
    }
  ];

  return (
    // In React, a component can only return a single root element
    <>
      {/* 導覽列 Navbar */}
      <Navbar/>

      {/* Hero 區塊 */}
      <Hero/>

      {/* 內容區塊（車輛卡片之後會放這） */}
      <div className="container py-5">
        <h2 className="mb-4">🔥 熱門車款</h2>
        <div className="row">
          {/* 動態產生車輛卡片 */}
          {bikesData.map(bike => (
            <BikeCard 
              key={bike.id}
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
            />
          ))}
        </div>
      </div>

      {/* footer */}
      <Footer/>
    </>
  )
}

export default App

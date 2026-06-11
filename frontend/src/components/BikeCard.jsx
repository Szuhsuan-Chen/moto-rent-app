import React, { useState } from 'react';
import './BikeCard.css';
import BookingSuccessModal from './BookingSuccessModal';

function BikeCard({ 
  id,
  image, 
  title, 
  brand, 
  price, 
  motoType, 
  engineDisplacement, 
  maxHorsepower, 
  maxTorque, 
  engineType, 
  fuelTankCapacity, 
  seatHeight, 
  weight,
  filterData,
  onReset
}) {
  const [showModal, setShowModal] = useState(false);
  const [rentalInfo, setRentalInfo] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleCloseModal = () => {
    setShowModal(false);
    if (onReset) {
      onReset();
    }
  };

  const handleRentNow = async () => {
    // 檢查是否有必要的篩選資料
    if (!filterData.branch || !filterData.date || !filterData.startTime || !filterData.duration) {
      alert('請先選擇租借分店、日期、時間和時長');
      return;
    }

    setIsLoading(true);

    try {
      const response = await fetch('http://localhost:5001/api/rentals', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          motorcycle_id: id,
          customer_name: '顧客', // 簡化版本，實際應該有表單輸入
          customer_phone: '0900000000', // 簡化版本，實際應該有表單輸入
          branch: filterData.branch,
          rental_date: filterData.date,
          start_time: filterData.startTime,
          duration: filterData.duration,
        }),
      });

      const result = await response.json();

      if (!response.ok) {
        throw new Error(result.error || '預約失敗');
      }

      // 預約成功，顯示成功彈窗
      setRentalInfo(result.data);
      setShowModal(true);
    } catch (error) {
      console.error('預約失敗:', error);
      alert(`預約失敗: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <div className="col-md-4 mb-4">
        <div className="card">
          <img src={image} className="card-img-top" alt={title} />
          <div className="card-body">
              <h5 className="card-title">{title}</h5>
              <div className="row text-muted small">
                  <div className="col-6">
                      <p className="card-text">品牌 {brand}</p>
                      <p className="card-text">車身型式 {motoType}</p>
                      <p className="card-text">最大馬力 {maxHorsepower}</p>
                      <p className="card-text">最大扭力 {maxTorque}</p>
                      <p className='card-text'>引擎型式 {engineType}</p>
                  </div>
                  <div className="col-6 text-end">
                      <p className="card-text">引擎 {engineDisplacement}</p>
                      <p className="card-text">油箱容量 {fuelTankCapacity}</p>
                      <p className="card-text">座高 {seatHeight}</p>
                      <p className="card-text">重量 {weight}</p>
                  </div>
              </div>
              <div className='row mt-5'>
                  <div className='col-6'>
                      <p className="card-text fw-bold">${price}</p>
                  </div>
                  <div className='col-6'>
                      <button 
                        className="btn btn-primary w-100" 
                        onClick={handleRentNow}
                        disabled={isLoading}
                      >
                        {isLoading ? '處理中...' : 'Rent Now'}
                      </button>
                  </div>
              </div>
          </div>
        </div>
      </div>

      <BookingSuccessModal 
        isOpen={showModal}
        onClose={handleCloseModal}
        rentalInfo={rentalInfo}
      />
    </>
  );
}

export default BikeCard;

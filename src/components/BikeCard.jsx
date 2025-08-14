import React from 'react';
import './BikeCard.css';

function BikeCard({ image, title, brand, price, motoType, engineDisplacement, maxHorsepower, maxTorque, engineType, fuelTankCapacity, seatHeight, weight }) {
  return (
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
                    <button className="btn btn-primary w-100">Rent Now</button>
                </div>
            </div>
        </div>
      </div>
    </div>
  );
}

export default BikeCard;

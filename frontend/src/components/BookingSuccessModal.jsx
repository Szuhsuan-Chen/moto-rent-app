import React from 'react';
import './BookingSuccessModal.css';

function BookingSuccessModal({ isOpen, onClose, rentalInfo }) {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h2>✓ 預約成功</h2>
        {rentalInfo && (
          <div>
            <p>您的預約已成功建立！</p>
            <p className="text-muted">訂單編號：#{rentalInfo.rental_id}</p>
          </div>
        )}
        <button className="modal-close-btn" onClick={onClose}>
          關閉
        </button>
      </div>
    </div>
  );
}

export default BookingSuccessModal;

import React from 'react'
import './Navbar.css';
import logoImageBlack from '/MotoRentR_logo_black.png';

function Navbar() {
  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light px-4 container-fluid">
      {/* 品牌 Logo */}
      <a className="navbar-brand" href="#">
        <img className='logo-image' src={logoImageBlack}></img>
      </a>

      {/* 漢堡選單按鈕 */}
      <button
        className="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarNav"
        aria-controls="navbarNav"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span className="navbar-toggler-icon"></span>
      </button>

      {/* 可折疊連結區 */}
      <div className="collapse navbar-collapse" id="navbarNav">
        <ul className="navbar-nav ms-auto">
          <li className="nav-item">
            <a className="nav-link" href="#">Hosting</a>
          </li>
          <li className="nav-item">
            <a className="nav-link" href="#">Contact</a>
          </li>
          <li className="nav-item">
            <a className="nav-link" href="#">Account</a>
          </li>
        </ul>
      </div>
    </nav>
  )
}

export default Navbar

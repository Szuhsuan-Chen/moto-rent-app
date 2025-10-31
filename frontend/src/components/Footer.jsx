import React from 'react';
import './Footer.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faPhone, faEnvelope } from '@fortawesome/free-solid-svg-icons'
import { faFacebookF, faXTwitter, faInstagram, faThreads } from '@fortawesome/free-brands-svg-icons'
import logoImageWhite from '../assets/MotoRentR_logo_white.png';

function Footer(){
    return (
        <footer className="bg-dark text-light pt-5 pb-3">
            <div className="container">
                <div className="row">
                {/* 公司資訊 */}
                <div className="col-md-6 mb-4">
                    <img className='logo-image' src={logoImageWhite}/>
                    <p>
                    我們出租的不是車，而是通往冒險的鑰匙。<br/>
                    MotoRentR，陪你開啟每一次未知的路。
                    </p>
                    <p><FontAwesomeIcon icon={faPhone}/>02-1234-5678</p>
                    <p><FontAwesomeIcon icon={faEnvelope}/> contact@motorentr.com</p>
                </div>

                {/* 分店資訊 */}
                <div className="col-md-3 mb-4">
                    <h5 className="fw-bold">分店資訊</h5>
                    <ul className="list-unstyled">
                    <li>台北旗艦店</li>
                    <li>台中概念店</li>
                    <li>台南體驗店</li>
                    </ul>
                </div>

                {/* 社群連結 */}
                <div className="col-md-3 mb-4">
                    <h5 className="fw-bold">追蹤我們</h5>
                    <div className='social-icons'>
                        <a href="#" className="text-light fs-5"><FontAwesomeIcon icon={faFacebookF}/></a>
                        <a href="#" className="text-light fs-5"><FontAwesomeIcon icon={faXTwitter}/></a>
                        <a href="#" className="text-light fs-5"><FontAwesomeIcon icon={faInstagram}/></a>
                        <a href="#" className="text-light fs-5"><FontAwesomeIcon icon={faThreads}/></a>
                    </div>
                </div>
                </div>

                <div className="text-center border-top border-secondary pt-3 mt-3">
                <small>© {new Date().getFullYear()} MotoRent. All rights reserved.</small>
                </div>
            </div>
        </footer>
    )
}

export default Footer
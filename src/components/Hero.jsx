import React from "react"
import './Hero.css';
import heroBackground from '../assets/hero-bg.svg';

function Hero(){
    return(
        <div className="hero-bg text-white p-5 text-center">
            <h1 className="slogan">Rent A Bike, Rent Your Freedom</h1>
            {/* <p className="lead">騎著你夢想的重機，開始冒險之旅吧！</p> */}
        </div>
    )
}

export default Hero
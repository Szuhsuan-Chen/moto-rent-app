import React from "react";
import './Filterbar.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';

function Filterbar() {
  return (
    <div className="filter-bar">
      {/* 租借分店 */}
      <select name="branch" defaultValue="">
        <option value="" disabled selected hidden>租借分店</option>
        <option value="taipei">台北旗艦店</option>
        <option value="taichung">台中概念店</option>
        <option value="tainan">台南體驗店</option>
      </select>

      {/* 選擇日期 */}
      <input type="date" name="date" min="2025-09-01" max="2025-11-30" />

      {/* 開始時間 */}
      <select name="startTime" defaultValue="">
        <option value="" disabled selected hidden>開始時間</option>
        {[
          "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
          "13:00", "13:30", "14:00", "14:30", "15:00", "15:30",
          "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
          "19:00", "19:30", "20:00", "20:30", "21:00", "21:30",
          "22:00"
        ].map(time => (
          <option key={time} value={time}>{time}</option>
        ))}
      </select>

      {/* 租借時長方案 */}
      <select name="duration" defaultValue="">
        <option value="" disabled selected hidden>租借時長方案</option>
        <option value="5h">5H</option>
        <option value="10h">10H</option>
        <option value="24h">24H</option>
        <option value="48h">48H</option>
      </select>

      {/* 車型類別（價格） */}
      <select name="carType" defaultValue="">
        <option value="" disabled selected hidden>車型類別（價格）</option>
        <option value="type-ss">TYPE-SS</option>
        <option value="type-s">TYPE-S</option>
        <option value="type-a">TYPE-A</option>
        <option value="type-b">TYPE-B</option>
        <option value="type-c">TYPE-C</option>
        <option value="type-minibike">TYPE-MiniBike</option>
      </select>

      {/* 篩選按鈕 */}
      <button className="filter-btn" type="button">
        Search <FontAwesomeIcon icon={faMagnifyingGlass} />
      </button>
    </div>
  );
};

export default Filterbar;

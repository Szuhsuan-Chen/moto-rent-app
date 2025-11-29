-- 摩托車租賃應用程式資料庫初始化腳本

-- 建立資料庫（如果不存在）
CREATE DATABASE IF NOT EXISTS mydb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用資料庫
USE mydb;

-- 建立摩托車表格
CREATE TABLE IF NOT EXISTS motorcycles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    image VARCHAR(255) NOT NULL,
    title VARCHAR(100) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    price_category VARCHAR(20) NOT NULL COMMENT '車型類別（type-ss, type-s, type-a, type-b, type-c, type-minibike）',
    moto_type VARCHAR(50) NOT NULL COMMENT '摩托車類型（sport, naked, superbike, cruiser等）',
    engine_displacement VARCHAR(20) NOT NULL,
    max_horsepower VARCHAR(20) NOT NULL,
    max_torque VARCHAR(20) NOT NULL,
    engine_type VARCHAR(200) NOT NULL,
    fuel_tank_capacity VARCHAR(20) NOT NULL,
    seat_height VARCHAR(20) NOT NULL,
    weight VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 當插入新資料時，會自動填入當下的時間
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 當資料被修改時，這個欄位會自動更新為最新時間
) ENGINE=InnoDB;

-- 插入範例資料
INSERT INTO motorcycles (
    image, title, brand, price_category, moto_type, engine_displacement, 
    max_horsepower, max_torque, engine_type, fuel_tank_capacity, 
    seat_height, weight
) VALUES 
    ('/KAWASAKI_NINJA_400.png', 'KAWASAKI NINJA 400', 'KAWASAKI', 'type-b', 'sport', '140cc', '45hp', '38Nm', '水冷四行程單汽缸 SOHC 4V', '14L', '785mm', '168kg'),
    ('/YAMAHA_YZF_R3.png', 'YAMAHA YZF-R3', 'YAMAHA', 'type-c', 'sport', '321cc', '42hp', '29.6Nm', '水冷四行程並列雙汽缸 DOHC 4V', '14L', '780mm', '169kg'),
    ('/HONDA_CB650R.png', 'HONDA CB650R', 'HONDA', 'type-a', 'naked', '649cc', '95hp', '64Nm', '水冷四行程並列四汽缸 DOHC 4V', '15.4L', '810mm', '200kg'),
    ('/DUCATI_MONSTER_821.png', 'DUCATI MONSTER 821', 'DUCATI', 'type-s', 'naked', '821cc', '109hp', '86Nm', '水冷四行程L型雙汽缸 Desmodromic 4V', '17.5L', '785mm', '205kg'),
    ('/BMW_S1000RR.png', 'BMW S1000RR', 'BMW', 'type-ss', 'superbike', '999cc', '207hp', '113Nm', '水冷四行程並列四汽缸 DOHC 4V', '16.5L', '824mm', '197kg'),
    ('/HARLEY_DAVIDSON_SPORTSTER.png', 'HARLEY-DAVIDSON SPORTSTER', 'HARLEY-DAVIDSON', 'type-a', 'cruiser', '883cc', '50hp', '68Nm', '氣冷四行程V型雙汽缸 OHV 2V', '12.5L', '760mm', '256kg')
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    brand = VALUES(brand),
    price_category = VALUES(price_category);

-- 建立索引以提升查詢效能
ALTER TABLE motorcycles ADD INDEX idx_brand(brand);
ALTER TABLE motorcycles ADD INDEX idx_moto_type(moto_type);
ALTER TABLE motorcycles ADD INDEX idx_price_category(price_category);
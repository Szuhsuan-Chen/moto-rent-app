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
    engine_displacement INT NOT NULL,
    max_horsepower DECIMAL(5,1) NOT NULL,
    max_torque DECIMAL(5,1) NOT NULL,
    engine_type VARCHAR(200) NOT NULL,
    fuel_tank_capacity DECIMAL(4,1) NOT NULL,
    seat_height INT NOT NULL,
    weight INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 當插入新資料時，會自動填入當下的時間
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 當資料被修改時，這個欄位會自動更新為最新時間
) ENGINE=InnoDB;

-- 插入範例資料
INSERT INTO motorcycles (
    image, title, brand, price_category, moto_type, engine_displacement,
    max_horsepower, max_torque, engine_type, fuel_tank_capacity,
    seat_height, weight
) VALUES
    ('/KAWASAKI_NINJA_400.png', 'KAWASAKI NINJA 400', 'KAWASAKI', 'type-b', 'sport', 140, 45, 38, '水冷四行程單汽缸 SOHC 4V', 14.0, 785, 168),
    ('/YAMAHA_YZF_R3.png', 'YAMAHA YZF-R3', 'YAMAHA', 'type-c', 'sport', 321, 42, 29.6, '水冷四行程並列雙汽缸 DOHC 4V', 14.0, 780, 169),
    ('/HONDA_CB650R.png', 'HONDA CB650R', 'HONDA', 'type-a', 'naked', 649, 95, 64, '水冷四行程並列四汽缸 DOHC 4V', 15.4, 810, 200),
    ('/DUCATI_MONSTER_821.png', 'DUCATI MONSTER 821', 'DUCATI', 'type-s', 'naked', 821, 109, 86, '水冷四行程L型雙汽缸 Desmodromic 4V', 17.5, 785, 205),
    ('/BMW_S1000RR.png', 'BMW S1000RR', 'BMW', 'type-ss', 'superbike', 999, 207, 113, '水冷四行程並列四汽缸 DOHC 4V', 16.5, 824, 197),
    ('/HARLEY_DAVIDSON_SPORTSTER.png', 'HARLEY-DAVIDSON SPORTSTER', 'HARLEY-DAVIDSON', 'type-a', 'cruiser', 883, 50, 68, '氣冷四行程V型雙汽缸 OHV 2V', 12.5, 760, 256),
    ('/HONDA_CB750_HORNET.png', 'HONDA CB750 HORNET', 'HONDA', 'type-a', 'naked', 755, 92, 75, '水冷四行程並列雙汽缸 DOHC 4V', 15.1, 795, 190),
    ('/HONDA_CBR500R.png', 'HONDA CBR500R', 'HONDA', 'type-b', 'sport', 471, 47, 43, '水冷四行程並列雙汽缸 DOHC 4V', 17.3, 790, 192),
    ('/YAMAHA_YZF_R7.png', 'YAMAHA YZF-R7', 'YAMAHA', 'type-a', 'sport', 689, 73, 67, '水冷四行程並列雙汽缸 DOHC 4V', 13.0, 835, 188),
    ('/YAMAHA_MT07.png', 'YAMAHA MT-07', 'YAMAHA', 'type-a', 'naked', 689, 73, 67, '水冷四行程並列雙汽缸 DOHC 4V', 13.2, 805, 184),
    ('/KAWASAKI_NINJA_650.png', 'KAWASAKI NINJA 650', 'KAWASAKI', 'type-a', 'sport', 649, 68, 65, '水冷四行程並列雙汽缸 DOHC 4V', 15.0, 790, 193),
    ('/KAWASAKI_ZX4RR.png', 'KAWASAKI ZX-4RR', 'KAWASAKI', 'type-s', 'superbike', 399, 77, 40, '水冷四行程並列四汽缸 DOHC 4V', 15.0, 800, 183),
    ('/DUCATI_STREETFIGHTER_V2.png', 'DUCATI STREETFIGHTER V2', 'DUCATI', 'type-ss', 'naked', 955, 153, 101, '水冷四行程L型雙汽缸 Desmodromic 4V', 17.0, 845, 178),
    ('/BMW_F900R.png', 'BMW F900R', 'BMW', 'type-a', 'naked', 895, 105, 92, '水冷四行程並列雙汽缸 DOHC 4V', 13.0, 810, 211),
    ('/TRIUMPH_STREET_TRIPLE.png', 'TRIUMPH STREET TRIPLE R', 'TRIUMPH', 'type-s', 'naked', 765, 118, 79, '水冷四行程並列三汽缸 DOHC 4V', 17.4, 825, 166),
    ('/SUZUKI_GSX_S750.png', 'SUZUKI GSX-S750', 'SUZUKI', 'type-a', 'naked', 749, 114, 81, '水冷四行程並列四汽缸 DOHC 4V', 16.0, 820, 213)
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    brand = VALUES(brand),
    price_category = VALUES(price_category);

-- 建立索引以提升查詢效能
ALTER TABLE motorcycles ADD INDEX idx_moto_type(moto_type);
-- 複合索引：brand 選擇性最高放前面，已涵蓋 idx_brand 單一索引
ALTER TABLE motorcycles ADD INDEX idx_brand_price_type (brand, price_category, moto_type);

-- 建立租借記錄表格
CREATE TABLE IF NOT EXISTS rental_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    motorcycle_id INT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_email VARCHAR(100),
    branch VARCHAR(50) NOT NULL COMMENT '租借分店 (taipei, taichung, tainan)',
    rental_date DATE NOT NULL COMMENT '租借日期',
    start_time TIME NOT NULL COMMENT '開始時間',
    duration VARCHAR(10) NOT NULL COMMENT '租借時長 (5h, 10h, 24h, 48h)',
    end_datetime DATETIME NOT NULL COMMENT '預計歸還時間',
    total_price DECIMAL(10, 2) NOT NULL COMMENT '總租金',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '狀態: pending, confirmed, completed, cancelled',
    notes TEXT COMMENT '備註',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (motorcycle_id) REFERENCES motorcycles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 建立索引以提升查詢效能
ALTER TABLE rental_records ADD INDEX idx_customer_phone(customer_phone);
-- 複合索引：status 選擇性較高放前面，已涵蓋 branch 和 status 單一索引
ALTER TABLE rental_records ADD INDEX idx_status_branch (status, branch);
-- 複合索引：FOR UPDATE 鎖定查詢用
ALTER TABLE rental_records ADD INDEX idx_motorcycle_status (motorcycle_id, status);
-- 複合索引：衝突判斷查詢用，等值欄位放前面，範圍查詢 status 放最後
ALTER TABLE rental_records ADD INDEX idx_motorcycle_branch_status (motorcycle_id, branch, status);

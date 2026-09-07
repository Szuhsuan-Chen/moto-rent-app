-- 測試用的 schema，對照 db/init.sql，但拿掉 MySQL 專屬語法（ENGINE=InnoDB、COMMENT、CREATE DATABASE）讓 H2 能執行

CREATE TABLE IF NOT EXISTS motorcycles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    image VARCHAR(255) NOT NULL,
    title VARCHAR(100) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    price_category VARCHAR(20) NOT NULL,
    moto_type VARCHAR(50) NOT NULL,
    engine_displacement INT NOT NULL,
    max_horsepower DECIMAL(5,1) NOT NULL,
    max_torque DECIMAL(5,1) NOT NULL,
    engine_type VARCHAR(200) NOT NULL,
    fuel_tank_capacity DECIMAL(4,1) NOT NULL,
    seat_height INT NOT NULL,
    weight INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rental_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    motorcycle_id INT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_email VARCHAR(100),
    branch VARCHAR(50) NOT NULL,
    rental_date DATE NOT NULL,
    start_time TIME NOT NULL,
    duration VARCHAR(10) NOT NULL,
    end_datetime DATETIME NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (motorcycle_id) REFERENCES motorcycles(id) ON DELETE CASCADE
);

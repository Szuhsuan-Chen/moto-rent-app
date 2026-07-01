# 紀錄專案內裡面所有用到的 SQL 查詢

## motorcycles

```sql
-- 1. 依車型類別篩選
SELECT * FROM motorcycles
WHERE price_category = 'type-a'
ORDER BY price_category DESC;

-- 2. 查單台車
SELECT * FROM motorcycles WHERE id = 1;

-- 3. 查所有品牌
SELECT DISTINCT brand FROM motorcycles ORDER BY brand ASC;

-- 4. 查所有車型
SELECT DISTINCT moto_type FROM motorcycles ORDER BY moto_type ASC;
```

## rental_records

```sql
-- 5. 多條件篩選（含車輛資料）
SELECT r.*, m.title AS motorcycle_title, m.brand AS motorcycle_brand
FROM rental_records r
JOIN motorcycles m ON r.motorcycle_id = m.id
WHERE r.status = 'pending'
  AND r.branch = 'taipei'
  AND r.customer_phone = '0912345678'
ORDER BY r.created_at DESC;

-- 6. 查單筆租借（含車輛資料）
SELECT r.*, m.title AS motorcycle_title, m.brand AS motorcycle_brand
FROM rental_records r
JOIN motorcycles m ON r.motorcycle_id = m.id
WHERE r.id = 1;

-- 7. 新增租借
INSERT INTO rental_records
  (motorcycle_id, customer_name, customer_phone, customer_email,
   branch, rental_date, start_time, duration, end_datetime,
   total_price, status, notes)
VALUES
  (1, '陳小明', '0912345678', 'test@gmail.com',
   'taipei', '2026-07-01', '09:00:00', '5h', '2026-07-01 14:00:00',
   1500.00, 'pending', NULL);

-- 8. 更新狀態
UPDATE rental_records SET status = 'confirmed' WHERE id = 1;

-- 9. 刪除租借
DELETE FROM rental_records WHERE id = 1;

-- 10. 衝突時間判斷
SELECT COUNT(*) FROM rental_records
WHERE motorcycle_id = 1
  AND branch = 'taipei'
  AND status IN ('pending', 'confirmed')
  AND rental_date <= '2026-07-01'
  AND end_datetime > '2026-07-01 09:00:00';

-- 11. 鎖定租借記錄（防 Race Condition）
SELECT id FROM rental_records
WHERE motorcycle_id = 1
  AND status IN ('pending', 'confirmed')
FOR UPDATE;
```

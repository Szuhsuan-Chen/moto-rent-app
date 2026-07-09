# API Documentation

## 基本資訊

- **Base URL**: `http://localhost:5000`
- **Content-Type**: `application/json`
- **編碼**: UTF-8

---

## 目錄

1. [健康檢查](#健康檢查)
2. [摩托車相關 API](#摩托車相關-api)
3. [租借記錄 API](#租借記錄-api)
4. [基礎資料 API](#基礎資料-api)

---

## 健康檢查

### GET /api/health

檢查 API 服務和資料庫連接狀態。

**請求範例**:
```bash
GET http://localhost:5000/api/health
```

**回應範例**:
```json
{
  "status": "healthy",
  "database": "connected"
}
```

**狀態碼**:
- `200`: 服務正常
- `500`: 服務異常或資料庫連接失敗

---

## 摩托車相關 API

### GET /api/motorcycles

瀏覽摩托車目錄，**不查可用性**（`availability`、`filter_info` 一律為 `null`）。要查特定分店/時段的可用性請用下面的 `/api/motorcycles/availability`。

**查詢參數**:

| 參數 | 類型 | 必填 | 說明 | 範例 |
|------|------|------|------|------|
| `price_category` | string | 否 | 車型類別 | `type-ss`, `type-s`, `type-a`, `type-b`, `type-c`, `type-minibike` |
| `moto_type` | string | 否 | 車型類型 | `sport`, `naked`, `superbike`, `cruiser` |
| `brand` | string | 否 | 品牌 | `KAWASAKI`, `YAMAHA`, `HONDA` |

**請求範例**:
```bash
GET http://localhost:5000/api/motorcycles?price_category=type-s
```

**回應範例**:
```json
{
  "count": 2,
  "data": [
    {
      "id": 1,
      "image": "/KAWASAKI_NINJA_400.png",
      "title": "KAWASAKI NINJA 400",
      "brand": "KAWASAKI",
      "price_category": "type-s",
      "price": 5000,
      "moto_type": "sport",
      "engine_displacement": "399cc",
      "max_horsepower": "45hp",
      "max_torque": "38Nm",
      "engine_type": "水冷四行程並列雙汽缸 DOHC 4V",
      "fuel_tank_capacity": "14L",
      "seat_height": "785mm",
      "weight": "168kg",
      "availability": null,
      "filter_info": null
    }
  ],
  "filters_applied": {
    "price_category": "type-s",
    "moto_type": null,
    "brand": null
  }
}
```

**狀態碼**:
- `200`: 查詢成功
- `500`: 資料庫錯誤

---

### GET /api/motorcycles/availability

搜尋指定分店/時段的可用性。`branch`、`date`、`start_time`、`duration` **皆為必填**（全給才會真的查衝突，沒有「少給就略過檢查」的模糊狀態）。

**查詢參數**:

| 參數 | 類型 | 必填 | 說明 | 範例 |
|------|------|------|------|------|
| `branch` | string | 是 | 租借分店 | `taipei`, `taichung`, `tainan` |
| `date` | string | 是 | 租借日期 | `2025-12-01` (YYYY-MM-DD) |
| `start_time` | string | 是 | 開始時間 | `14:00` (HH:MM) |
| `duration` | string | 是 | 租借時長 | `5h`, `10h`, `24h`, `48h` |
| `price_category` | string | 否 | 車型類別 | `type-ss`, `type-s`, `type-a`, `type-b`, `type-c`, `type-minibike` |
| `moto_type` | string | 否 | 車型類型 | `sport`, `naked`, `superbike`, `cruiser` |
| `brand` | string | 否 | 品牌 | `KAWASAKI`, `YAMAHA`, `HONDA` |

**請求範例**:
```bash
GET http://localhost:5000/api/motorcycles/availability?branch=taipei&date=2025-12-15&start_time=14:00&duration=24h&price_category=type-s
```

**回應範例**:
```json
{
  "count": 2,
  "data": [
    {
      "id": 1,
      "image": "/KAWASAKI_NINJA_400.png",
      "title": "KAWASAKI NINJA 400",
      "brand": "KAWASAKI",
      "price_category": "type-s",
      "price": 5000,
      "moto_type": "sport",
      "engine_displacement": "399cc",
      "max_horsepower": "45hp",
      "max_torque": "38Nm",
      "engine_type": "水冷四行程並列雙汽缸 DOHC 4V",
      "fuel_tank_capacity": "14L",
      "seat_height": "785mm",
      "weight": "168kg",
      "availability": {
        "available": true,
        "message": "Available for rent"
      },
      "filter_info": {
        "branch": "taipei",
        "date": "2025-12-15",
        "start_time": "14:00",
        "duration": "24h"
      }
    }
  ],
  "filters_applied": {
    "branch": "taipei",
    "date": "2025-12-15",
    "start_time": "14:00",
    "duration": "24h",
    "price_category": "type-s",
    "moto_type": null,
    "brand": null
  }
}
```

**狀態碼**:
- `200`: 查詢成功
- `400`: 缺少必填參數或格式錯誤
- `500`: 資料庫錯誤

---

### GET /api/motorcycles/:motorcycle_id

獲取單一摩托車的詳細資訊，包含所有租借時長的價格。

**路徑參數**:

| 參數 | 類型 | 必填 | 說明 |
|------|------|------|------|
| `motorcycle_id` | integer | 是 | 摩托車 ID |

**請求範例**:
```bash
GET http://localhost:5000/api/motorcycles/1
```

**回應範例**:
```json
{
  "data": {
    "id": 1,
    "image": "/KAWASAKI_NINJA_400.png",
    "title": "KAWASAKI NINJA 400",
    "brand": "KAWASAKI",
    "price_category": "type-s",
    "moto_type": "sport",
    "engine_displacement": "399cc",
    "max_horsepower": "45hp",
    "max_torque": "38Nm",
    "engine_type": "水冷四行程並列雙汽缸 DOHC 4V",
    "fuel_tank_capacity": "14L",
    "seat_height": "785mm",
    "weight": "168kg",
    "prices": {
      "5h": 2500,
      "10h": 4000,
      "24h": 5000,
      "48h": 9000
    }
  }
}
```

**狀態碼**:
- `200`: 查詢成功
- `404`: 找不到該摩托車
- `500`: 資料庫錯誤

---

## 租借記錄 API

### POST /api/rentals

建立新的租借記錄。

**請求 Body**:

| 欄位 | 類型 | 必填 | 說明 | 範例 |
|------|------|------|------|------|
| `motorcycle_id` | integer | 是 | 摩托車 ID | `1` |
| `customer_name` | string | 是 | 顧客姓名 | `王小明` |
| `customer_phone` | string | 是 | 顧客電話 | `0912345678` |
| `customer_email` | string | 否 | 顧客信箱 | `example@email.com` |
| `branch` | string | 是 | 租借分店 | `taipei`, `taichung`, `tainan` |
| `rental_date` | string | 是 | 租借日期 | `2025-12-15` (YYYY-MM-DD) |
| `start_time` | string | 是 | 開始時間 | `14:00` (HH:MM) |
| `duration` | string | 是 | 租借時長 | `5h`, `10h`, `24h`, `48h` |
| `notes` | string | 否 | 備註 | `請準備大型安全帽` |

**請求範例**:
```bash
POST http://localhost:5000/api/rentals
Content-Type: application/json

{
  "motorcycle_id": 1,
  "customer_name": "王小明",
  "customer_phone": "0912345678",
  "customer_email": "wang@email.com",
  "branch": "taipei",
  "rental_date": "2025-12-15",
  "start_time": "14:00",
  "duration": "24h",
  "notes": "請準備大型安全帽"
}
```

**回應範例**:
```json
{
  "message": "Rental booking successful",
  "data": {
    "rental_id": 123,
    "total_price": 5000,
    "end_datetime": "2025-12-16 14:00"
  }
}
```

**狀態碼**:
- `201`: 租借成功
- `400`: 缺少必填欄位或該時段已被預訂
- `404`: 找不到該摩托車
- `500`: 資料庫錯誤

**錯誤訊息範例**:
```json
{
  "error": "This time slot is already booked"
}
```

---

### GET /api/rentals

獲取租借記錄列表。

**查詢參數**:

| 參數 | 類型 | 必填 | 說明 | 範例 |
|------|------|------|------|------|
| `status` | string | 否 | 訂單狀態 | `pending`, `confirmed`, `completed`, `cancelled` |
| `branch` | string | 否 | 租借分店 | `taipei`, `taichung`, `tainan` |
| `customer_phone` | string | 否 | 顧客電話 | `0912345678` |

**請求範例**:
```bash
GET http://localhost:5000/api/rentals?status=confirmed&branch=taipei
```

**回應範例**:
```json
{
  "count": 2,
  "data": [
    {
      "id": 123,
      "motorcycle_id": 1,
      "motorcycle_title": "KAWASAKI NINJA 400",
      "motorcycle_brand": "KAWASAKI",
      "customer_name": "王小明",
      "customer_phone": "0912345678",
      "customer_email": "wang@email.com",
      "branch": "taipei",
      "rental_date": "2025-12-15",
      "start_time": "14:00:00",
      "duration": "24h",
      "end_datetime": "2025-12-16 14:00",
      "total_price": 5000.0,
      "status": "confirmed",
      "notes": "請準備大型安全帽",
      "created_at": "2025-12-01 10:30:00",
      "updated_at": "2025-12-01 10:30:00"
    }
  ]
}
```

**狀態碼**:
- `200`: 查詢成功
- `500`: 資料庫錯誤

---

### GET /api/rentals/:rental_id

獲取單一租借記錄的詳細資訊。

**路徑參數**:

| 參數 | 類型 | 必填 | 說明 |
|------|------|------|------|
| `rental_id` | integer | 是 | 租借記錄 ID |

**請求範例**:
```bash
GET http://localhost:5000/api/rentals/123
```

**回應範例**:
```json
{
  "data": {
    "id": 123,
    "motorcycle_id": 1,
    "motorcycle_title": "KAWASAKI NINJA 400",
    "motorcycle_brand": "KAWASAKI",
    "motorcycle_image": "/KAWASAKI_NINJA_400.png",
    "motorcycle_price_category": "type-s",
    "customer_name": "王小明",
    "customer_phone": "0912345678",
    "customer_email": "wang@email.com",
    "branch": "taipei",
    "rental_date": "2025-12-15",
    "start_time": "14:00:00",
    "duration": "24h",
    "end_datetime": "2025-12-16 14:00",
    "total_price": 5000.0,
    "status": "confirmed",
    "notes": "請準備大型安全帽",
    "created_at": "2025-12-01 10:30:00",
    "updated_at": "2025-12-01 10:30:00"
  }
}
```

**狀態碼**:
- `200`: 查詢成功
- `404`: 找不到該租借記錄
- `500`: 資料庫錯誤

---

### PATCH /api/rentals/:rental_id

更新租借記錄的狀態。

**路徑參數**:

| 參數 | 類型 | 必填 | 說明 |
|------|------|------|------|
| `rental_id` | integer | 是 | 租借記錄 ID |

**請求 Body**:

| 欄位 | 類型 | 必填 | 說明 | 可選值 |
|------|------|------|------|--------|
| `status` | string | 是 | 訂單狀態 | `pending`, `confirmed`, `completed`, `cancelled` |

**請求範例**:
```bash
PATCH http://localhost:5000/api/rentals/123
Content-Type: application/json

{
  "status": "completed"
}
```

**回應範例**:
```json
{
  "message": "Status updated successfully"
}
```

**狀態碼**:
- `200`: 更新成功
- `400`: 缺少 status 欄位或狀態值無效
- `404`: 找不到該租借記錄
- `500`: 資料庫錯誤

---

### DELETE /api/rentals/:rental_id

刪除租借記錄。

**路徑參數**:

| 參數 | 類型 | 必填 | 說明 |
|------|------|------|------|
| `rental_id` | integer | 是 | 租借記錄 ID |

**請求範例**:
```bash
DELETE http://localhost:5000/api/rentals/123
```

**回應範例**:
```json
{
  "message": "Rental record deleted"
}
```

**狀態碼**:
- `200`: 刪除成功
- `404`: 找不到該租借記錄
- `500`: 資料庫錯誤

---

## 基礎資料 API

### GET /api/brands

獲取所有品牌列表。

**請求範例**:
```bash
GET http://localhost:5000/api/brands
```

**回應範例**:
```json
{
  "data": [
    "BMW",
    "DUCATI",
    "HONDA",
    "KAWASAKI",
    "SUZUKI",
    "YAMAHA"
  ]
}
```

**狀態碼**:
- `200`: 查詢成功
- `500`: 資料庫錯誤

---

### GET /api/types

獲取所有車型類型列表。

**請求範例**:
```bash
GET http://localhost:5000/api/types
```

**回應範例**:
```json
{
  "data": [
    "cruiser",
    "naked",
    "sport",
    "superbike"
  ]
}
```

**狀態碼**:
- `200`: 查詢成功
- `500`: 資料庫錯誤

---

### GET /api/branches

獲取所有分店資訊。

**請求範例**:
```bash
GET http://localhost:5000/api/branches
```

**回應範例**:
```json
{
  "data": [
    {
      "id": "taipei",
      "name": "台北旗艦店",
      "address": "台北市信義區信義路五段7號",
      "phone": "02-2345-6789",
      "hours": "10:00 - 22:00"
    },
    {
      "id": "taichung",
      "name": "台中概念店",
      "address": "台中市西屯區台灣大道三段99號",
      "phone": "04-2345-6789",
      "hours": "10:00 - 22:00"
    },
    {
      "id": "tainan",
      "name": "台南體驗店",
      "address": "台南市中西區中正路123號",
      "phone": "06-2345-6789",
      "hours": "10:00 - 22:00"
    }
  ]
}
```

**狀態碼**:
- `200`: 查詢成功

---

### GET /api/price-categories

獲取價格類別資訊（24小時基準價格）。

**請求範例**:
```bash
GET http://localhost:5000/api/price-categories
```

**回應範例**:
```json
{
  "data": [
    {
      "id": "type-ss",
      "name": "TYPE-SS",
      "price": 6000
    },
    {
      "id": "type-s",
      "name": "TYPE-S",
      "price": 5000
    },
    {
      "id": "type-a",
      "name": "TYPE-A",
      "price": 4000
    },
    {
      "id": "type-b",
      "name": "TYPE-B",
      "price": 3000
    },
    {
      "id": "type-c",
      "name": "TYPE-C",
      "price": 2000
    },
    {
      "id": "type-minibike",
      "name": "TYPE-MiniBike",
      "price": 1000
    }
  ]
}
```

**狀態碼**:
- `200`: 查詢成功

---

## 租借價格表

根據車型類別和租借時長計算價格：

| 車型類別 | 5H | 10H | 24H | 48H |
|---------|-----|------|------|------|
| TYPE-SS | 3000 | 4800 | 6000 | 10800 |
| TYPE-S | 2500 | 4000 | 5000 | 9000 |
| TYPE-A | 2000 | 3200 | 4000 | 7200 |
| TYPE-B | 1500 | 2400 | 3000 | 5400 |
| TYPE-C | 1000 | 1600 | 2000 | 3600 |
| TYPE-MiniBike | 500 | 800 | 1000 | 1800 |

---

## 錯誤處理

所有 API 在發生錯誤時會返回以下格式：

```json
{
  "error": "錯誤訊息描述"
}
```

**常見 HTTP 狀態碼**:
- `200`: 請求成功
- `201`: 資源建立成功
- `400`: 請求參數錯誤
- `404`: 資源不存在
- `500`: 伺服器內部錯誤

---

## 備註

1. **日期格式**: 所有日期使用 `YYYY-MM-DD` 格式
2. **時間格式**: 所有時間使用 `HH:MM` 24小時制格式
3. **租借狀態**:
   - `pending`: 待確認
   - `confirmed`: 已確認（目前建立訂單後自動確認）
   - `completed`: 已完成
   - `cancelled`: 已取消
4. **可用性檢查**: 系統會自動檢查選定時段是否已被預訂
5. **價格計算**: 根據車型類別和租借時長自動從價格表計算總價

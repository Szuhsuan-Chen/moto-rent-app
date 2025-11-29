from flask import Flask, jsonify, request
from flask_cors import CORS
import mysql.connector
from mysql.connector import Error
from datetime import datetime
import os

app = Flask(__name__)
CORS(app)

# 資料庫連接配置
DB_CONFIG = {
    'host': os.getenv('DB_HOST', 'db'),
    'user': os.getenv('DB_USER', 'root'),
    'password': os.getenv('DB_PASSWORD', 'rootpassword'),
    'database': os.getenv('DB_NAME', 'mydb'),
    'charset': 'utf8mb4',
    'collation': 'utf8mb4_unicode_ci'
}



# 車型類別與租借時長的價格對應表
# 格式: RENTAL_PRICE_MAP[車型類別][租借時長] = 價格
RENTAL_PRICE_MAP = {
    'type-ss': {
        '5h': 3000,   # TYPE-SS 5小時
        '10h': 4800,  # TYPE-SS 10小時
        '24h': 6000,  # TYPE-SS 24小時
        '48h': 10800  # TYPE-SS 48小時
    },
    'type-s': {
        '5h': 2500,   # TYPE-S 5小時
        '10h': 4000,  # TYPE-S 10小時
        '24h': 5000,  # TYPE-S 24小時
        '48h': 9000   # TYPE-S 48小時
    },
    'type-a': {
        '5h': 2000,   # TYPE-A 5小時
        '10h': 3200,  # TYPE-A 10小時
        '24h': 4000,  # TYPE-A 24小時
        '48h': 7200   # TYPE-A 48小時
    },
    'type-b': {
        '5h': 1500,   # TYPE-B 5小時
        '10h': 2400,  # TYPE-B 10小時
        '24h': 3000,  # TYPE-B 24小時
        '48h': 5400   # TYPE-B 48小時
    },
    'type-c': {
        '5h': 1000,   # TYPE-C 5小時
        '10h': 1600,  # TYPE-C 10小時
        '24h': 2000,  # TYPE-C 24小時
        '48h': 3600   # TYPE-C 48小時
    },
    'type-minibike': {
        '5h': 500,    # TYPE-MiniBike 5小時
        '10h': 800,   # TYPE-MiniBike 10小時
        '24h': 1000,  # TYPE-MiniBike 24小時
        '48h': 1800   # TYPE-MiniBike 48小時
    }
}

def get_db_connection():
    """建立資料庫連接"""
    try:
        connection = mysql.connector.connect(**DB_CONFIG)
        if connection.is_connected():
            return connection
    except Error as e:
        print(f"資料庫連接錯誤: {e}")
        return None

def close_db_connection(connection, cursor=None):
    """關閉資料庫連接"""
    if cursor:
        cursor.close()
    if connection and connection.is_connected():
        connection.close()

@app.route('/api/health', methods=['GET'])
def health_check():
    """健康檢查端點"""
    try:
        connection = get_db_connection()
        if connection:
            close_db_connection(connection)
            return jsonify({'status': 'healthy', 'database': 'connected'}), 200
        else:
            return jsonify({'status': 'unhealthy', 'database': 'disconnected'}), 500
    except Exception as e:
        return jsonify({'status': 'error', 'message': str(e)}), 500

@app.route('/api/motorcycles', methods=['GET'])
def get_motorcycles():
    """
    獲取摩托車列表，支援多種篩選條件
    查詢參數:
    - branch: 租借分店 (taipei, taichung, tainan)
    - date: 租借日期 (YYYY-MM-DD)
    - start_time: 開始時間 (HH:MM)
    - duration: 租借時長 (5h, 10h, 24h, 48h)
    - price_category: 車型類別 (type-ss, type-s, type-a, type-b, type-c, type-minibike)
    - moto_type: 車型類型 (sport, naked, superbike, cruiser)
    - brand: 品牌
    """
    connection = None
    cursor = None
    
    try:
        # 獲取查詢參數
        branch = request.args.get('branch')
        date = request.args.get('date')
        start_time = request.args.get('start_time')
        duration = request.args.get('duration')
        price_category = request.args.get('price_category')
        moto_type = request.args.get('moto_type')
        brand = request.args.get('brand')
        
        # 建立資料庫連接
        connection = get_db_connection()
        if not connection:
            return jsonify({'error': '資料庫連接失敗'}), 500
        
        cursor = connection.cursor(dictionary=True)
        
        # 構建 SQL 查詢
        query = "SELECT * FROM motorcycles WHERE 1=1"
        params = []
        
        # 根據車型類別篩選
        if price_category and price_category in RENTAL_PRICE_MAP:
            query += " AND price_category = %s"
            params.append(price_category)
        
        # 根據車型類型篩選
        if moto_type:
            query += " AND moto_type = %s"
            params.append(moto_type)
        
        # 根據品牌篩選
        if brand:
            query += " AND brand = %s"
            params.append(brand)
        
        # 排序（依車型類別的 24h 價格排序）
        query += " ORDER BY price_category DESC"
        
        # 執行查詢
        cursor.execute(query, params)
        motorcycles = cursor.fetchall()
        
        # 處理查詢結果
        result = []
        for moto in motorcycles:
            # 檢查日期和時間可用性
            availability_status = check_availability(
                moto['id'], 
                branch, 
                date, 
                start_time, 
                duration
            )
            
            # 根據車型類別和租借時長查表獲得價格
            price_cat = moto['price_category']
            rental_price = None
            
            if price_cat in RENTAL_PRICE_MAP:
                if duration and duration in RENTAL_PRICE_MAP[price_cat]:
                    # 如果有指定 duration，返回該時長的價格
                    rental_price = RENTAL_PRICE_MAP[price_cat][duration]
                else:
                    # 如果沒有指定 duration，返回 24h 價格作為預設顯示
                    rental_price = RENTAL_PRICE_MAP[price_cat]['24h']
            
            moto_data = {
                'id': moto['id'],
                'image': moto['image'],
                'title': moto['title'],
                'brand': moto['brand'],
                'price_category': price_cat,
                'price': rental_price,  # 動態查表得到的價格
                'moto_type': moto['moto_type'],
                'engine_displacement': moto['engine_displacement'],
                'max_horsepower': moto['max_horsepower'],
                'max_torque': moto['max_torque'],
                'engine_type': moto['engine_type'],
                'fuel_tank_capacity': moto['fuel_tank_capacity'],
                'seat_height': moto['seat_height'],
                'weight': moto['weight'],
                'availability': availability_status,
                'filter_info': {
                    'branch': branch,
                    'date': date,
                    'start_time': start_time,
                    'duration': duration
                }
            }
            result.append(moto_data)
        
        return jsonify({
            'success': True,
            'count': len(result),
            'data': result,
            'filters_applied': {
                'branch': branch,
                'date': date,
                'start_time': start_time,
                'duration': duration,
                'price_category': price_category,
                'moto_type': moto_type,
                'brand': brand
            }
        }), 200
        
    except Error as e:
        return jsonify({'error': f'資料庫查詢錯誤: {str(e)}'}), 500
    except Exception as e:
        return jsonify({'error': f'伺服器錯誤: {str(e)}'}), 500
    finally:
        close_db_connection(connection, cursor)

def check_availability(motorcycle_id, branch, date, start_time, duration):
    """
    檢查摩托車在指定時間和分店的可用性
    這是簡化版本，實際應該查詢租借記錄表
    """
    # 驗證日期格式
    if date:
        try:
            rental_date = datetime.strptime(date, '%Y-%m-%d')
            current_date = datetime.now()
            
            # 檢查日期是否在有效範圍內
            if rental_date < current_date:
                return {
                    'available': False,
                    'message': '選擇的日期已過期'
                }
        except ValueError:
            return {
                'available': False,
                'message': '日期格式不正確'
            }
    
    # 檢查分店是否有效
    valid_branches = ['taipei', 'taichung', 'tainan']
    if branch and branch not in valid_branches:
        return {
            'available': False,
            'message': '無效的分店選擇'
        }
    
    # 檢查開始時間格式
    if start_time:
        try:
            datetime.strptime(start_time, '%H:%M')
        except ValueError:
            return {
                'available': False,
                'message': '時間格式不正確'
            }
    
    # 檢查租借時長
    valid_durations = ['5h', '10h', '24h', '48h']
    if duration and duration not in valid_durations:
        return {
            'available': False,
            'message': '無效的租借時長'
        }
    
    # 如果所有檢查都通過，返回可用
    # 實際應用中，這裡應該查詢租借記錄表來確認真實可用性
    return {
        'available': True,
        'message': '可以租借'
    }

@app.route('/api/motorcycles/<int:motorcycle_id>', methods=['GET'])
def get_motorcycle_detail(motorcycle_id):
    """獲取單一摩托車詳細資訊"""
    connection = None
    cursor = None
    
    try:
        connection = get_db_connection()
        if not connection:
            return jsonify({'error': '資料庫連接失敗'}), 500
        
        cursor = connection.cursor(dictionary=True)
        query = "SELECT * FROM motorcycles WHERE id = %s"
        cursor.execute(query, (motorcycle_id,))
        motorcycle = cursor.fetchone()
        
        if not motorcycle:
            return jsonify({'error': '找不到指定的摩托車'}), 404
        
        # 根據車型類別查表獲得所有時長的價格
        price_cat = motorcycle['price_category']
        price_info = {}
        
        if price_cat in RENTAL_PRICE_MAP:
            price_info = RENTAL_PRICE_MAP[price_cat]
        
        motorcycle['prices'] = price_info  # 返回所有時長的價格
        
        return jsonify({
            'success': True,
            'data': motorcycle
        }), 200
        
    except Error as e:
        return jsonify({'error': f'資料庫查詢錯誤: {str(e)}'}), 500
    except Exception as e:
        return jsonify({'error': f'伺服器錯誤: {str(e)}'}), 500
    finally:
        close_db_connection(connection, cursor)

@app.route('/api/brands', methods=['GET'])
def get_brands():
    """獲取所有品牌列表"""
    connection = None
    cursor = None
    
    try:
        connection = get_db_connection()
        if not connection:
            return jsonify({'error': '資料庫連接失敗'}), 500
        
        cursor = connection.cursor()
        query = "SELECT DISTINCT brand FROM motorcycles ORDER BY brand"
        cursor.execute(query)
        brands = [row[0] for row in cursor.fetchall()]
        
        return jsonify({
            'success': True,
            'data': brands
        }), 200
        
    except Error as e:
        return jsonify({'error': f'資料庫查詢錯誤: {str(e)}'}), 500
    finally:
        close_db_connection(connection, cursor)

@app.route('/api/types', methods=['GET'])
def get_types():
    """獲取所有車型類型列表"""
    connection = None
    cursor = None
    
    try:
        connection = get_db_connection()
        if not connection:
            return jsonify({'error': '資料庫連接失敗'}), 500
        
        cursor = connection.cursor()
        query = "SELECT DISTINCT moto_type FROM motorcycles ORDER BY moto_type"
        cursor.execute(query)
        types = [row[0] for row in cursor.fetchall()]
        
        return jsonify({
            'success': True,
            'data': types
        }), 200
        
    except Error as e:
        return jsonify({'error': f'資料庫查詢錯誤: {str(e)}'}), 500
    finally:
        close_db_connection(connection, cursor)

@app.route('/api/branches', methods=['GET'])
def get_branches():
    """獲取所有分店資訊"""
    branches = [
        {
            'id': 'taipei',
            'name': '台北旗艦店',
            'address': '台北市信義區信義路五段7號',
            'phone': '02-2345-6789',
            'hours': '10:00 - 22:00'
        },
        {
            'id': 'taichung',
            'name': '台中概念店',
            'address': '台中市西屯區台灣大道三段99號',
            'phone': '04-2345-6789',
            'hours': '10:00 - 22:00'
        },
        {
            'id': 'tainan',
            'name': '台南體驗店',
            'address': '台南市中西區中正路123號',
            'phone': '06-2345-6789',
            'hours': '10:00 - 22:00'
        }
    ]
    
    return jsonify({
        'success': True,
        'data': branches
    }), 200

@app.route('/api/price-categories', methods=['GET'])
def get_price_categories():
    """獲取價格類別資訊"""
    categories = [
        {'id': 'type-ss', 'name': 'TYPE-SS', 'price': 6000},
        {'id': 'type-s', 'name': 'TYPE-S', 'price': 5000},
        {'id': 'type-a', 'name': 'TYPE-A', 'price': 4000},
        {'id': 'type-b', 'name': 'TYPE-B', 'price': 3000},
        {'id': 'type-c', 'name': 'TYPE-C', 'price': 2000},
        {'id': 'type-minibike', 'name': 'TYPE-MiniBike', 'price': 1000}
    ]
    
    return jsonify({
        'success': True,
        'data': categories
    }), 200

@app.errorhandler(404)
def not_found(error):
    """處理 404 錯誤"""
    return jsonify({'error': '找不到請求的資源'}), 404

@app.errorhandler(500)
def internal_error(error):
    """處理 500 錯誤"""
    return jsonify({'error': '伺服器內部錯誤'}), 500

if __name__ == '__main__':
    # 開發環境設定
    app.run(host='0.0.0.0', port=5000, debug=True)

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

# 車型類別價格映射
PRICE_CATEGORY_MAP = {
    'type-ss': {'min': 6000, 'max': 10000},
    'type-s': {'min': 4000, 'max': 5999},
    'type-a': {'min': 3000, 'max': 3999},
    'type-b': {'min': 2000, 'max': 2999},
    'type-c': {'min': 1000, 'max': 1999},
    'type-minibike': {'min': 0, 'max': 999}
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
        
        # 根據車型類別（價格範圍）篩選
        if price_category and price_category in PRICE_CATEGORY_MAP:
            price_range = PRICE_CATEGORY_MAP[price_category]
            query += " AND CAST(price AS UNSIGNED) BETWEEN %s AND %s"
            params.extend([price_range['min'], price_range['max']])
        
        # 根據車型類型篩選
        if moto_type:
            query += " AND moto_type = %s"
            params.append(moto_type)
        
        # 根據品牌篩選
        if brand:
            query += " AND brand = %s"
            params.append(brand)
        
        # 排序
        query += " ORDER BY price ASC"
        
        # 執行查詢
        cursor.execute(query, params)
        motorcycles = cursor.fetchall()
        
        # 處理查詢結果
        result = []
        for moto in motorcycles:
            # 檢查日期和時間可用性（這裡簡化處理，實際應該有租借記錄表）
            availability_status = check_availability(
                moto['id'], 
                branch, 
                date, 
                start_time, 
                duration
            )
            
            moto_data = {
                'id': moto['id'],
                'image': moto['image'],
                'title': moto['title'],
                'brand': moto['brand'],
                'price': moto['price'],
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
        {'id': 'type-ss', 'name': 'TYPE-SS', 'price_range': '6000+'},
        {'id': 'type-s', 'name': 'TYPE-S', 'price_range': '4000-5999'},
        {'id': 'type-a', 'name': 'TYPE-A', 'price_range': '3000-3999'},
        {'id': 'type-b', 'name': 'TYPE-B', 'price_range': '2000-2999'},
        {'id': 'type-c', 'name': 'TYPE-C', 'price_range': '1000-1999'},
        {'id': 'type-minibike', 'name': 'TYPE-MiniBike', 'price_range': '0-999'}
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

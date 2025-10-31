from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
import os
import time
import mysql.connector
from mysql.connector import Error

app = Flask(__name__)
CORS(app)

# 資料庫設定
DB_HOST = os.environ.get('DB_HOST', 'localhost')
DB_USER = os.environ.get('DB_USER', 'root')
DB_PASSWORD = os.environ.get('DB_PASSWORD', 'example')
DB_NAME = os.environ.get('DB_NAME', 'mydb')

app.config['SQLALCHEMY_DATABASE_URI'] = f'mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}/{DB_NAME}'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)

# 摩托車模型
class Motorcycle(db.Model):
    __tablename__ = 'motorcycles'
    
    id = db.Column(db.Integer, primary_key=True)
    image = db.Column(db.String(255), nullable=False)
    title = db.Column(db.String(100), nullable=False)
    brand = db.Column(db.String(50), nullable=False)
    price = db.Column(db.String(20), nullable=False)
    moto_type = db.Column(db.String(50), nullable=False)
    engine_displacement = db.Column(db.String(20), nullable=False)
    max_horsepower = db.Column(db.String(20), nullable=False)
    max_torque = db.Column(db.String(20), nullable=False)
    engine_type = db.Column(db.String(200), nullable=False)
    fuel_tank_capacity = db.Column(db.String(20), nullable=False)
    seat_height = db.Column(db.String(20), nullable=False)
    weight = db.Column(db.String(20), nullable=False)

    def to_dict(self):
        return {
            'id': self.id,
            'image': self.image,
            'title': self.title,
            'brand': self.brand,
            'price': self.price,
            'motoType': self.moto_type,
            'engineDisplacement': self.engine_displacement,
            'maxHorsepower': self.max_horsepower,
            'maxTorque': self.max_torque,
            'engineType': self.engine_type,
            'fuelTankCapacity': self.fuel_tank_capacity,
            'seatHeight': self.seat_height,
            'weight': self.weight
        }

def wait_for_db():
    """等待資料庫連線可用"""
    max_retries = 30
    retry_count = 0
    
    while retry_count < max_retries:
        try:
            connection = mysql.connector.connect(
                host=DB_HOST,
                user=DB_USER,
                password=DB_PASSWORD,
                database=DB_NAME
            )
            if connection.is_connected():
                connection.close()
                print("資料庫連線成功！")
                return True
        except Error as e:
            retry_count += 1
            print(f"等待資料庫連線... ({retry_count}/{max_retries})")
            time.sleep(2)
    
    return False

def init_db():
    """初始化資料庫和範例資料"""
    try:
        db.create_all()
        
        # 檢查是否已有資料
        if Motorcycle.query.first() is None:
            # 插入範例資料
            sample_motorcycles = [
                {
                    'image': '/KAWASAKI_NINJA_400.png',
                    'title': 'KAWASAKI NINJA 400',
                    'brand': 'KAWASAKI',
                    'price': '2400',
                    'moto_type': '跑車',
                    'engine_displacement': '140cc',
                    'max_horsepower': '45hp',
                    'max_torque': '38Nm',
                    'engine_type': '水冷四行程單汽缸 SOHC 4V',
                    'fuel_tank_capacity': '14L',
                    'seat_height': '785mm',
                    'weight': '168kg'
                },
                {
                    'image': '/YAMAHA_YZF_R3.png',
                    'title': 'YAMAHA YZF-R3',
                    'brand': 'YAMAHA',
                    'price': '2200',
                    'moto_type': '跑車',
                    'engine_displacement': '321cc',
                    'max_horsepower': '42hp',
                    'max_torque': '29.6Nm',
                    'engine_type': '水冷四行程並列雙汽缸 DOHC 4V',
                    'fuel_tank_capacity': '14L',
                    'seat_height': '780mm',
                    'weight': '169kg'
                },
                {
                    'image': '/HONDA_CB650R.png',
                    'title': 'HONDA CB650R',
                    'brand': 'HONDA',
                    'price': '3200',
                    'moto_type': '街車',
                    'engine_displacement': '649cc',
                    'max_horsepower': '95hp',
                    'max_torque': '64Nm',
                    'engine_type': '水冷四行程並列四汽缸 DOHC 4V',
                    'fuel_tank_capacity': '15.4L',
                    'seat_height': '810mm',
                    'weight': '200kg'
                },
                {
                    'image': '/DUCATI_MONSTER_821.png',
                    'title': 'DUCATI MONSTER 821',
                    'brand': 'DUCATI',
                    'price': '4500',
                    'moto_type': '街車',
                    'engine_displacement': '821cc',
                    'max_horsepower': '109hp',
                    'max_torque': '86Nm',
                    'engine_type': '水冷四行程L型雙汽缸 Desmodromic 4V',
                    'fuel_tank_capacity': '17.5L',
                    'seat_height': '785mm',
                    'weight': '205kg'
                },
                {
                    'image': '/BMW_S1000RR.png',
                    'title': 'BMW S1000RR',
                    'brand': 'BMW',
                    'price': '6800',
                    'moto_type': '超跑',
                    'engine_displacement': '999cc',
                    'max_horsepower': '207hp',
                    'max_torque': '113Nm',
                    'engine_type': '水冷四行程並列四汽缸 DOHC 4V',
                    'fuel_tank_capacity': '16.5L',
                    'seat_height': '824mm',
                    'weight': '197kg'
                },
                {
                    'image': '/HARLEY_DAVIDSON_SPORTSTER.png',
                    'title': 'HARLEY-DAVIDSON SPORTSTER',
                    'brand': 'HARLEY-DAVIDSON',
                    'price': '3800',
                    'moto_type': '美式巡航',
                    'engine_displacement': '883cc',
                    'max_horsepower': '50hp',
                    'max_torque': '68Nm',
                    'engine_type': '氣冷四行程V型雙汽缸 OHV 2V',
                    'fuel_tank_capacity': '12.5L',
                    'seat_height': '760mm',
                    'weight': '256kg'
                }
            ]
            
            for moto_data in sample_motorcycles:
                motorcycle = Motorcycle(**moto_data)
                db.session.add(motorcycle)
            
            db.session.commit()
            print("範例資料插入成功！")
    except Exception as e:
        print(f"初始化資料庫時發生錯誤: {e}")

# API 路由
@app.route("/")
def home():
    return jsonify({"message": "摩托車租賃 API 服務正在運行！"})

@app.route("/api/motorcycles", methods=['GET'])
def get_motorcycles():
    """獲取所有摩托車資料"""
    try:
        motorcycles = Motorcycle.query.all()
        return jsonify([moto.to_dict() for moto in motorcycles])
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/motorcycles/<int:moto_id>", methods=['GET'])
def get_motorcycle(moto_id):
    """獲取特定摩托車資料"""
    try:
        motorcycle = Motorcycle.query.get_or_404(moto_id)
        return jsonify(motorcycle.to_dict())
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/motorcycles/brand/<brand>", methods=['GET'])
def get_motorcycles_by_brand(brand):
    """根據品牌篩選摩托車"""
    try:
        motorcycles = Motorcycle.query.filter_by(brand=brand.upper()).all()
        return jsonify([moto.to_dict() for moto in motorcycles])
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/motorcycles/type/<moto_type>", methods=['GET'])
def get_motorcycles_by_type(moto_type):
    """根據類型篩選摩托車"""
    try:
        motorcycles = Motorcycle.query.filter_by(moto_type=moto_type).all()
        return jsonify([moto.to_dict() for moto in motorcycles])
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    # 等待資料庫連線
    if wait_for_db():
        with app.app_context():
            init_db()
        app.run(host="0.0.0.0", port=5000, debug=True)
    else:
        print("無法連接到資料庫！")

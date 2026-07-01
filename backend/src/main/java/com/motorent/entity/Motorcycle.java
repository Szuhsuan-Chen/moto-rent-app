package com.motorent.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 機車實體類別（POJO）
 * MyBatis 版：不需要 @Entity 等 JPA annotation，純粹作為資料載體
 * MyBatis 透過 map-underscore-to-camel-case 設定，自動把欄位名稱從底線轉換成駝峰式，例如 price_category → priceCategory
 */
@Data           // Lombok：自動產生 getter、setter、toString、equals、hashCode
@NoArgsConstructor // Lombok：產生無參數建構子，MyBatis 對應資料庫結果時需要
public class Motorcycle {

    private Long id;                   // 機車唯一識別碼（主鍵）
    private String image;              // 機車圖片路徑或 URL
    private String title;              // 機車型號名稱
    private String brand;              // 品牌（如 Honda、Yamaha）
    private String priceCategory;      // 價格分類（如 economy、standard、premium）
    private String motoType;           // 車種類型（如 scooter、sport、naked）
    private Integer engineDisplacement; // 排氣量（cc）
    private String maxHorsepower;       // 最大馬力
    private String maxTorque;           // 最大扭力
    private String engineType;          // 引擎型式（如 單缸、雙缸）
    private Double fuelTankCapacity;    // 油箱容量（公升）
    private Integer seatHeight;         // 座墊高度（毫米）
    private Integer weight;             // 車重（公斤）
    private LocalDateTime createdAt;   // 資料建立時間
    private LocalDateTime updatedAt;   // 資料最後更新時間
}

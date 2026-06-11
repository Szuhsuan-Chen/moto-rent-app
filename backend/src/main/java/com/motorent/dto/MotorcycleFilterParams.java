package com.motorent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// 傳給 MyBatis XML 動態查詢的參數物件
// XML 裡用 #{priceCategory} 對應這個 class 的欄位
@Data
@AllArgsConstructor
public class MotorcycleFilterParams {
    private String priceCategory;
    private String motoType;
    private String brand;
}

package com.motorent.repository;

import com.motorent.dto.MotorcycleFilterParams;
import com.motorent.entity.Motorcycle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// @Mapper: 告訴 MyBatis 這是一個 SQL 對應介面，Spring 會自動建立實作
// 面試考點：MyBatis vs JPA 差別 → MyBatis 你自己控制 SQL，更直觀，JPA 幫你產生
@Mapper
public interface MotorcycleMapper {

    // findByFilters 的動態 SQL 在 MotorcycleMapper.xml 裡（因為有 <if> 條件）
    List<Motorcycle> findByFilters(MotorcycleFilterParams params);

    // 簡單查詢直接用 annotation 寫 SQL，不需要 XML
    @Select("SELECT * FROM motorcycles WHERE id = #{id}")
    Motorcycle findById(@Param("id") Long id);

    @Select("SELECT DISTINCT brand FROM motorcycles ORDER BY brand ASC")
    List<String> findDistinctBrands();

    @Select("SELECT DISTINCT moto_type FROM motorcycles ORDER BY moto_type ASC")
    List<String> findDistinctMotoTypes();
}

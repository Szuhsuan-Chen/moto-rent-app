package com.motorent.repository;

import com.motorent.dto.RentalFilterParams;
import com.motorent.entity.RentalRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RentalRecordMapper {

    // 動態 WHERE 條件在 RentalRecordMapper.xml 裡
    List<RentalRecord> findByFilters(RentalFilterParams params);

    // JOIN 查詢帶出 motorcycle 資料，motorcycle_title/brand 會自動對應到 RentalRecord 欄位
    @Select("""
            SELECT r.*, m.title AS motorcycle_title, m.brand AS motorcycle_brand
            FROM rental_records r
            JOIN motorcycles m ON r.motorcycle_id = m.id
            WHERE r.id = #{id}
            """)
    RentalRecord findByIdWithMotorcycle(@Param("id") Long id);

    // @Options: 讓 MyBatis 把 DB 產生的 AUTO_INCREMENT id 寫回 record.id
    @Insert("""
            INSERT INTO rental_records
            (motorcycle_id, customer_name, customer_phone, customer_email,
             branch, rental_date, start_time, duration, end_datetime,
             total_price, status, notes)
            VALUES
            (#{motorcycleId}, #{customerName}, #{customerPhone}, #{customerEmail},
             #{branch}, #{rentalDate}, #{startTime}, #{duration}, #{endDatetime},
             #{totalPrice}, #{status}, #{notes})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(RentalRecord record);

    @Update("UPDATE rental_records SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Delete("DELETE FROM rental_records WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("""
            SELECT COUNT(*) FROM rental_records
            WHERE motorcycle_id = #{motorcycleId}
            AND branch = #{branch}
            AND status IN ('pending', 'confirmed')
            AND TIMESTAMP(rental_date, start_time) < #{endDatetime}
            AND end_datetime > #{startDatetime}
            """)
    long countConflictingRentals(
            @Param("motorcycleId") Long motorcycleId,
            @Param("branch") String branch,
            @Param("startDatetime") LocalDateTime startDatetime,
            @Param("endDatetime") LocalDateTime endDatetime
    );
}

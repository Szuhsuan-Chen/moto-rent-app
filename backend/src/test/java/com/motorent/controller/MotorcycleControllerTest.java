package com.motorent.controller;

import com.motorent.dto.MotorcycleDto;
import com.motorent.exception.ResourceNotFoundException;
import com.motorent.service.MotorcycleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest：只載入 Controller 層相關的 Bean，不會啟動整個 Spring Context，也不會連資料庫
@WebMvcTest(MotorcycleController.class)
class MotorcycleControllerTest {

    @Autowired
    private MockMvc mockMvc; // 模擬送 HTTP request，不用真的啟動 web server

    @MockitoBean
    private MotorcycleService motorcycleService; // Controller 依賴的 Service，這裡一樣要 mock 掉

    @Test
    void getMotorcycles_returnsOkAndMatchingJsonBody() throws Exception {
        // Arrange
        MotorcycleDto dto = MotorcycleDto.builder()
                .id(1L)
                .title("Yamaha R3")
                .brand("Yamaha")
                .priceCategory("type-a")
                .price(2000)
                .build();
        when(motorcycleService.findMotorcycles("type-a", "sport", "Yamaha"))
                .thenReturn(List.of(dto));

        // Act & Assert：一次呼叫裡同時發送 request、檢查 status code、檢查 JSON 內容
        mockMvc.perform(get("/api/motorcycles")
                        .param("price_category", "type-a")
                        .param("moto_type", "sport")
                        .param("brand", "Yamaha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Yamaha R3"))
                .andExpect(jsonPath("$.data[0].price_category").value("type-a"))
                .andExpect(jsonPath("$.filters_applied.brand").value("Yamaha"));
    }

    @Test
    void getMotorcycles_whenPriceCategoryInvalid_returns400() throws Exception {
        // Act & Assert：price_category 不在允許的列表內，應該在進到 service 前就被 @Pattern 擋下
        mockMvc.perform(get("/api/motorcycles")
                        .param("price_category", "invalid-value"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("price_category is invalid"));
    }

    @Test
    void getMotorcycle_whenFound_returns200AndBody() throws Exception {
        // Arrange
        MotorcycleDto dto = MotorcycleDto.builder()
                .id(1L)
                .title("Yamaha R3")
                .brand("Yamaha")
                .priceCategory("type-a")
                .price(2000)
                .build();
        when(motorcycleService.getMotorcycleById(1L))
                .thenReturn(dto);

        // Act & Assert：一次呼叫裡同時發送 request、檢查 status code、檢查 JSON 內容
        mockMvc.perform(get("/api/motorcycles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Yamaha R3"))
                .andExpect(jsonPath("$.data.price_category").value("type-a"));
    }

    @Test
    void getMotorcycle_whenNotFound_returns404() throws Exception {
        // Arrange：讓 mock 拋出例外，模擬 service 找不到資料的情況
        when(motorcycleService.getMotorcycleById(999L))
                .thenThrow(new ResourceNotFoundException("Motorcycle not found"));

        // Act & Assert：確認 GlobalExceptionHandler 有把例外轉成 404 + error 訊息
        mockMvc.perform(get("/api/motorcycles/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Motorcycle not found"));
    }

}

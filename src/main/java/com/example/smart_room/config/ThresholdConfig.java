package com.example.smart_room.config;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ThresholdConfig {
    public static final Map<String, Double> UPPER_LIMITS = Map.of(
            "temp", 30.0, // Cảnh báo khi nhiệt độ trên 30°C
            "humidity", 80.0, // Cảnh báo khi độ ẩm trên 80%
            "light", 80.0 // Cảnh báo khi độ sáng quá cao
    );

    public static final Map<String, Double> LOWER_LIMITS = Map.of(
            "temp", 15.0, // Cảnh báo khi nhiệt độ dưới 15°C
            "humidity", 20.0, // Cảnh báo khi độ ẩm dưới 20%
            "light", 10.0 // Cảnh báo khi độ sáng quá thấp
    );
}

package com.example.smart_room.controller;

import com.example.smart_room.model.SensorData;
import com.example.smart_room.service.AdafruitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/adafruit")
public class AdafruitController {

    private static final Logger logger = Logger.getLogger(AdafruitController.class.getName());

    private final AdafruitService adafruitService;

    public AdafruitController(AdafruitService adafruitService) {
        this.adafruitService = adafruitService;
    }

    /**
     * API lấy danh sách tất cả các feeds từ Adafruit IO
     */
    @GetMapping("/feeds")
    public ResponseEntity<?> getFeeds() {
        List<Map<String, Object>> feeds = adafruitService.getAllFeeds();
        if (feeds == null || feeds.isEmpty()) {
            return ResponseEntity.status(404).body("Không tìm thấy feeds nào từ Adafruit.");
        }
        return ResponseEntity.ok(feeds);
    }

    /**
     * API lấy dữ liệu mới nhất của một feed cụ thể từ Adafruit IO
     */
    @GetMapping("/feeds/{feedKey}")
    public ResponseEntity<?> getFeedData(@PathVariable String feedKey) {
        Map<String, Object> feedData = adafruitService.getFeedData(feedKey);
        if (feedData == null) {
            return ResponseEntity.status(404).body("Không tìm thấy dữ liệu cho feed: " + feedKey);
        }
        return ResponseEntity.ok(feedData);
    }

    /**
     * API đồng bộ dữ liệu từ Adafruit vào PostgreSQL & Firebase
     */
    @PostMapping("/fetch/{feedKey}")
    public ResponseEntity<?> fetchAndSaveData(@PathVariable String feedKey) {
        try {
            adafruitService.fetchAndSaveData(feedKey);
            return ResponseEntity.ok("Data fetched and saved for feed: " + feedKey);
        } catch (Exception e) {
            logger.severe("Error in fetchAndSaveData: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi đồng bộ  liệu: " + e.getMessage());
        }
    }

    /**
     * API lấy tất cả dữ liệu cảm biến đã lưu trong PostgreSQL
     */
    @GetMapping("/data")
    public ResponseEntity<List<SensorData>> getAllSensorData() {
        List<SensorData> dataList = adafruitService.getAllSensorData();
        return ResponseEntity.ok(dataList);
    }
}

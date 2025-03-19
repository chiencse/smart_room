package com.example.smart_room.controller;

import com.example.smart_room.model.SensorData;
import com.example.smart_room.model.User;
import com.example.smart_room.request.ControlDeviceRequestDto;
import com.example.smart_room.service.AdafruitService;
import com.example.smart_room.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/adafruit")
public class AdafruitController {

    @Autowired
    private UserService userService;

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
            return ResponseEntity.status(404).body("not found Adafruit.");
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
            return ResponseEntity.status(404).body("not found feed data: " + feedKey);
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
            return ResponseEntity.internalServerError().body("error sync: " + e.getMessage());
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

    @PostMapping("/command")
    public ResponseEntity<String> sendCommand(@RequestHeader("Authorization") String authorizationHeader, @RequestBody ControlDeviceRequestDto controlDevice) {
        String token = authorizationHeader.replace("Bearer ", "");
        User user =  userService.getUserInfoFromToken(token);

        boolean success = adafruitService.sendCommandToDevice(controlDevice.getDeviceKey(), controlDevice.getValue(), user.getId());
        if (success) {
            return ResponseEntity.ok("Command sent successfully to " + controlDevice.getDeviceKey());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send command");
        }
    }
}

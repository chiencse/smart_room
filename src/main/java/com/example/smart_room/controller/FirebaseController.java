package com.example.smart_room.controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/firebase")
public class FirebaseController {

    private final DatabaseReference firebaseDbRef;

    public FirebaseController(FirebaseDatabase firebaseDatabase) {
        this.firebaseDbRef = firebaseDatabase.getReference("sensor_data"); // Đường dẫn lưu trên Firebase
    }

    /**
     * API gửi dữ liệu lên Firebase
     * 
     * @param data JSON gồm { "feedKey": "device.lamp", "value": "ON", "timestamp":
     *             "2025-03-14T12:00:00" }
     */
    @PostMapping("/send")
    public String sendDataToFirebase(@RequestBody Map<String, Object> data) {
        try {
            // Lấy dữ liệu từ request
            String feedKey = (String) data.get("feedKey");
            String value = (String) data.get("value");
            String timestamp = (String) data.get("timestamp");

            // Kiểm tra dữ liệu hợp lệ
            if (feedKey == null || value == null || timestamp == null) {
                return "Dữ liệu không hợp lệ!";
            }

            // Firebase không hỗ trợ dấu "." nên thay thế bằng "_"
            String sanitizedFeedKey = feedKey.replace(".", "_");

            // Dữ liệu gửi lên Firebase
            Map<String, String> firebaseData = new HashMap<>();
            firebaseData.put("value", value);
            firebaseData.put("timestamp", timestamp);

            // Gửi lên Firebase
            firebaseDbRef.child(sanitizedFeedKey).push().setValueAsync(firebaseData);

            return "Dữ liệu đã gửi lên Firebase: " + sanitizedFeedKey;
        } catch (Exception e) {
            return "Lỗi khi gửi dữ liệu lên Firebase: " + e.getMessage();
        }
    }
}

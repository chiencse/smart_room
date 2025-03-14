package com.example.smart_room.service;

import com.example.smart_room.model.ActivityLog;
import com.example.smart_room.model.SensorData;
import com.example.smart_room.repository.ActivityLogRepository;
import com.example.smart_room.repository.SensorDataRepository;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import java.util.logging.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.smart_room.config.ThresholdConfig;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class AdafruitService {

    @Value("${adafruit.api.base-url}")
    private String adafruitBaseUrl;

    @Value("${adafruit.api.key}")
    private String adafruitApiKey;

    private static final Logger logger = Logger.getLogger(AdafruitService.class.getName());
    private final RestTemplate restTemplate = new RestTemplate();
    private final DatabaseReference firebaseDbRef;
    private final SensorDataRepository sensorDataRepository;
    private final ActivityLogRepository activityLogRepository;

    public AdafruitService(FirebaseDatabase firebaseDatabase, SensorDataRepository sensorDataRepository,
            ActivityLogRepository activityLogRepository) {
        this.firebaseDbRef = firebaseDatabase.getReference("feeds");
        this.sensorDataRepository = sensorDataRepository;
        this.activityLogRepository = activityLogRepository;
    }

    /**
     * Lấy danh sách tất cả các feeds từ Adafruit IO
     */
    public List<Map<String, Object>> getAllFeeds() {
        String url = adafruitBaseUrl + "/feeds";
        return restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {
                }).getBody();
    }

    /**
     * Lấy dữ liệu chi tiết của một feed từ Adafruit
     */
    public Map<String, Object> getFeedData(String feedKey) {
        String url = adafruitBaseUrl + "/feeds/" + feedKey + "/data";

        List<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {
                }).getBody();

        if (response != null && !response.isEmpty()) {
            Map<String, Object> latestData = response.get(0);

            // Kiểm tra null trước khi trả về
            if (latestData.get("last_value") == null) {
                latestData.put("last_value", "N/A"); // Giá trị mặc định
            }
            if (latestData.get("last_value_at") == null) {
                latestData.put("last_value_at", LocalDateTime.now().toString()); // Thời gian hiện tại
            }

            return latestData;
        }

        return null; // Trả về null nếu không có dữ liệu
    }

    /*
     * Lấy dữ liệu từ Adafruit và lưu vào PostgreSQL & Firebase
     */
    public void fetchAndSaveData(String feedKey) {
        // Gọi API để lấy dữ liệu từ "/feeds/{feedKey}/data"
        Map<String, Object> latestData = getFeedData(feedKey);

        if (latestData != null) {
            String value = (String) latestData.get("value"); // Đảm bảo lấy đúng key
            String timestampStr = (String) latestData.get("created_at"); // Adafruit lưu timestamp tại "created_at"

            LocalDateTime timestamp;
            // Kiểm tra nếu timestamp không hợp lệ, dùng thời gian hiện tại
            if (timestampStr == null || timestampStr.trim().isEmpty()) {
                timestamp = LocalDateTime.now();
                timestampStr = timestamp.toString();
                logger.warning("Timestamp not suitable: " + timestampStr);
            } else {
                try {
                    timestamp = java.time.OffsetDateTime.parse(timestampStr, DateTimeFormatter.ISO_DATE_TIME)
                            .toLocalDateTime();
                } catch (Exception e) {
                    logger.severe("error parse timestamp: " + e.getMessage() + ". default now.");
                    timestamp = LocalDateTime.now();
                    timestampStr = timestamp.toString();
                }
            }
            checkThreshold(feedKey, Double.parseDouble(value));
            // Lưu vào PostgreSQL
            SensorData sensorData = new SensorData();
            sensorData.setName(feedKey);
            sensorData.setKey(feedKey);
            sensorData.setValue(value);
            sensorData.setTimestamp(timestamp);
            sensorDataRepository.save(sensorData);

            // Lưu vào Firebase
            saveToFirebase(feedKey, value, timestampStr);
        } else {
            logger.warning("no data for feed: " + feedKey);
        }
    }

    private void saveToFirebase(String feedKey, String value, String timestamp) {
        // Ensure value and timestamp are not null
        if (value == null) {
            value = "default_value"; // or handle it appropriately
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now().toString(); // or handle it appropriately
        }

        firebaseDbRef.child(feedKey).push().setValueAsync(Map.of(
                "value", value,
                "timestamp", timestamp));
    }

    private void checkThreshold(String feedKey, double value) {
        if (ThresholdConfig.UPPER_LIMITS.containsKey(feedKey) &&
                value > ThresholdConfig.UPPER_LIMITS.get(feedKey)) {
            logger.warning("caution! " + feedKey + " high: " + value);
            sendWarning(feedKey, "high value: " + value);
        }

        if (ThresholdConfig.LOWER_LIMITS.containsKey(feedKey) &&
                value < ThresholdConfig.LOWER_LIMITS.get(feedKey)) {
            logger.warning("caution! " + feedKey + " low: " + value);
            sendWarning(feedKey, "low value: " + value);
        }
    }

    private void sendWarning(String feedKey, String message) {
        // Lưu cảnh báo vào Firebase
        firebaseDbRef.child("alerts").push().setValueAsync(Map.of(
                "feed", feedKey,
                "message", message,
                "timestamp", LocalDateTime.now().toString()));

        // Bạn có thể thêm gửi thông báo qua Firebase Cloud Messaging (FCM) hoặc MQTT
    }

    @Scheduled(fixedRate = 60000) // Chạy mỗi 60 giây
    public void syncDataToFirebase() {
        String[] feedKeys = { "device.lamp", "device.fan", "temp", "humidity", "light", "air", "device.door",
                "device.status-fan", "device.status-lamp" };

        for (String feedKey : feedKeys) {
            Map<String, Object> latestData = getFeedData(feedKey);

            if (latestData != null) {
                String value = (String) latestData.get("last_value"); // Adafruit lưu giá trị tại last_value
                String timestamp = (String) latestData.get("last_value_at"); // Thời gian lưu tại last_value_at

                if (timestamp == null || timestamp.trim().isEmpty()) {
                    timestamp = LocalDateTime.now().toString();
                }

                // Chuyển key thành định dạng hợp lệ (Firebase không hỗ trợ dấu '.')
                String sanitizedFeedKey = feedKey.replace(".", "_");

                firebaseDbRef.child(sanitizedFeedKey).push().setValueAsync(Map.of(
                        "value", value != null ? value : "N/A",
                        "timestamp", timestamp));

                logger.info("Updated Firebase: " + sanitizedFeedKey + " = " + value);
            } else {
                logger.warning("No data received from Adafruit for feed: " + feedKey);
            }
        }
    }

    public List<SensorData> getAllSensorData() {
        return sensorDataRepository.findAll();
    }

    public boolean sendCommandToDevice(String deviceKey, String value, Long userId) {
        try {
            String url = adafruitBaseUrl + "/feeds/" + deviceKey + "/data";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-AIO-Key", adafruitApiKey);
            headers.set("Content-Type", "application/json");

            String jsonBody = "{\"value\": \"" + value + "\"}";
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Gửi lệnh thành công: " + deviceKey + " -> " + value);

                // 🌟 Lưu vào Log Activity
                ActivityLog log = new ActivityLog("COMMAND", value, userId, deviceKey);
                activityLogRepository.save(log);
                return true;
            } else {
                logger.warning("⚠️ Lỗi khi gửi lệnh: " + response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            logger.severe("❌ Lỗi gửi lệnh: " + e.getMessage());
            return false;
        }
    }
}

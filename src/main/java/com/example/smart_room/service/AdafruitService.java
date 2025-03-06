package com.example.smart_room.service;

import com.example.smart_room.model.LogFeed;
import com.example.smart_room.repository.LogFeedRepository;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class AdafruitService {

    @Value("${adafruit.api.base-url}")
    private String adafruitBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final DatabaseReference firebaseDbRef;
    private final LogFeedRepository logFeedRepository;

    public AdafruitService(FirebaseDatabase firebaseDatabase, LogFeedRepository logFeedRepository) {
        this.firebaseDbRef = firebaseDatabase.getReference("feeds");
        this.logFeedRepository = logFeedRepository;
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
     * Lấy dữ liệu chi tiết của một feed từ Adafruit và lưu vào Firebase &
     * PostgreSQL
     */
    public void fetchAndStoreFeedData(String feedKey) {
        String url = adafruitBaseUrl + "/feeds/" + feedKey;
        // Lấy dữ liệu của feed (bao gồm last_value, created_at, etc.)
        Map<String, Object> feedData = restTemplate.getForObject(url, Map.class);
        if (feedData != null) {
            // Lấy dữ liệu cần thiết
            Long adafruitId = ((Number) feedData.get("id")).longValue();
            String name = (String) feedData.get("name");
            String lastValue = (String) feedData.get("last_value");
            String lastValueAtStr = (String) feedData.get("last_value_at"); // Format: "2025-03-04T07:25:19Z"
            String key = (String) feedData.get("key");

            String groupKey = null;
            Map<String, Object> group = (Map<String, Object>) feedData.get("group");
            if (group != null) {
                groupKey = (String) group.get("key");
            }

            LocalDateTime lastValueAt = null;
            if (lastValueAtStr != null) {
                // Chuyển đổi chuỗi ISO-8601 sang LocalDateTime
                lastValueAt = LocalDateTime.parse(lastValueAtStr, DateTimeFormatter.ISO_DATE_TIME);
            }

            // Tạo đối tượng LogFeed để lưu vào PostgreSQL
            LogFeed logFeed = new LogFeed();
            logFeed.setAdafruitId(adafruitId);
            logFeed.setName(name);
            logFeed.setLastValue(lastValue);
            logFeed.setLastValueAt(lastValueAt);
            logFeed.setFeedKey(key);
            logFeed.setGroupKey(groupKey);
            // Giả sử created_at của feed cũng được lưu từ Adafruit
            String createdAtStr = (String) feedData.get("created_at");
            if (createdAtStr != null) {
                LocalDateTime createdAt = LocalDateTime.parse(createdAtStr, DateTimeFormatter.ISO_DATE_TIME);
                logFeed.setCreatedAt(createdAt);
            } else {
                logFeed.setCreatedAt(LocalDateTime.now());
            }
            logFeedRepository.save(logFeed);

            // Lưu dữ liệu lên Firebase Realtime Database
            // Sử dụng node "feeds/{feedKey}"
            DatabaseReference feedRef = firebaseDbRef.child(key);
            feedRef.setValueAsync(feedData);
        }
    }
}

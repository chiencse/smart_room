package com.example.smart_room.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime time;
    private String type;
    private String value;
    private Long userId;
    private String deviceKey;

    public ActivityLog() {
        this.time = LocalDateTime.now(); // Mặc định là thời gian hiện tại
    }

    // Constructor
    public ActivityLog(String type, String value, Long userId, String deviceKey) {
        this.time = LocalDateTime.now();
        this.type = type;
        this.value = value;
        this.userId = userId;
        this.deviceKey = deviceKey;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Long getUserId() {
        return userId;
    }

    public String getdeviceKey() {
        return deviceKey;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setdeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }
}

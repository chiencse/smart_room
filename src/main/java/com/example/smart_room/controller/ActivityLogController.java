package com.example.smart_room.controller;

import com.example.smart_room.model.ActivityLog;
import com.example.smart_room.repository.ActivityLogRepository;
import com.example.smart_room.response.ActivityLogUser;
import com.example.smart_room.service.ActivityLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    // API để ghi nhận hoạt động
    @PostMapping("/log")
    public ResponseEntity<ActivityLog> logActivity(
            @RequestParam String type,
            @RequestParam String value,
            @RequestParam Long userId,
            @RequestParam String deviceKey) {
        ActivityLog log = activityLogService.logActivity(type, value, userId, deviceKey);
        return ResponseEntity.ok(log);
    }

    // API lấy nhật ký theo ID người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityLog>> getLogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(activityLogService.getLogsByUser(userId));
    }

    // API lấy nhật ký theo ID thiết bị
    @GetMapping("/device/{deviceKey}")
    public ResponseEntity<List<ActivityLog>> getLogsByDevice(@PathVariable String deviceKey) {
        return ResponseEntity.ok(activityLogService.getLogsByDevice(deviceKey));
    }

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @GetMapping("/logs")
    public ResponseEntity<List<ActivityLogUser>> getAllLogs() {
        List<ActivityLogUser> logs = activityLogRepository.findAllwithUsername();
        return ResponseEntity.ok(logs);
    }
}

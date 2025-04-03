package com.example.smart_room.service;

import com.example.smart_room.model.ActivityLog;
import com.example.smart_room.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    // Ghi nhận hoạt động vào cơ sở dữ liệu
    public ActivityLog logActivity(String type, String value, Long userId, String deviceKey) {
        ActivityLog log = new ActivityLog(type, value, userId, deviceKey);
        return activityLogRepository.save(log);
    }

    // Lấy nhật ký theo ID người dùng
    public List<ActivityLog> getLogsByUser(Long userId) {
        return activityLogRepository.findByUserId(userId);
    }

    // Lấy nhật ký theo ID thiết bị
    public List<ActivityLog> getLogsByDevice(String deviceKey) {
        return activityLogRepository.findBydeviceKey(deviceKey);
    }
}

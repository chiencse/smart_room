package com.example.smart_room.repository;

import com.example.smart_room.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByDeviceId(String deviceId);

    List<ActivityLog> findByUserId(Long userId);
}

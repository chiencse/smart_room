package com.example.smart_room.repository;

import com.example.smart_room.model.ActivityLog;
import com.example.smart_room.response.ActivityLogUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findBydeviceKey(String deviceKey);

    List<ActivityLog> findByUserId(Long userId);

    @Query(value = "SELECT new com.example.smart_room.response.ActivityLogUser(a.time, a.value, a.deviceKey, u.username) FROM ActivityLog a JOIN User u ON a.userId = u.id ")
    List<ActivityLogUser> findAllwithUsername();
}

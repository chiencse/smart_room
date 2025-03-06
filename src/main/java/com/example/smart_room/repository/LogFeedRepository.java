package com.example.smart_room.repository;

import com.example.smart_room.model.LogFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogFeedRepository extends JpaRepository<LogFeed, Long> {
}

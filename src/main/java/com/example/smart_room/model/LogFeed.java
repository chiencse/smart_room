package com.example.smart_room.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "log_feeds")
public class LogFeed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adafruitId; // id từ Adafruit
    private String name; // Tên feed
    private String lastValue; // Giá trị mới nhất
    private LocalDateTime lastValueAt; // Thời gian cập nhật mới nhất
    private String feedKey; // Key của feed
    private String groupKey; // Key của group (nếu có)
    private LocalDateTime createdAt; // Thời gian tạo feed trên Adafruit
}

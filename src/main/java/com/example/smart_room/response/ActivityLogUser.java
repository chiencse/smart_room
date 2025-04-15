package com.example.smart_room.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ActivityLogUser {
    private LocalDateTime time;
    private String value;
    private String deviceKey;
    private String username;
}

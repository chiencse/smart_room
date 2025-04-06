package com.example.smart_room.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class DeviceValueDTO {
    private Long deviceId;
    private String value;
}

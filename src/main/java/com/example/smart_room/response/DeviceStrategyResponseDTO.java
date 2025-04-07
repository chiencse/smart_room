package com.example.smart_room.response;

import com.example.smart_room.request.DeviceValueDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceStrategyResponseDTO {
    Long Id;
    String name;
    String description;
    String status;
    Time startTime;

    List<DeviceInStrategyDTO> listDeviceValues;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static  class DeviceInStrategyDTO {
        Long deviceId;
        String deviceName;
        String deviceType;
        String deviceStatus;
        String value;
    }
}

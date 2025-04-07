package com.example.smart_room.request;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceStrategyDTO {
    String name;
    String description;
    String status;
    String startTime;

    List<DeviceValueDTO> listDeviceValues;
}

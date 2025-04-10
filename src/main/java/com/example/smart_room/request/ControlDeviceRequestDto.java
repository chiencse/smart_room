package com.example.smart_room.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ControlDeviceRequestDto {
    private String deviceKey;
    private String value;
}

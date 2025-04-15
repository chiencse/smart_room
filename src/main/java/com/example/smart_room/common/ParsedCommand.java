package com.example.smart_room.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParsedCommand {
    private String deviceKey;
    private String value;
    private String type;
}

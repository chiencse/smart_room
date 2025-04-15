package com.example.smart_room.response;


import com.example.smart_room.common.ParsedCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandResponse {
    String message;
    List<ParsedCommand> commands;
}

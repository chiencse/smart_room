package com.example.smart_room.controller;


import com.example.smart_room.common.ParsedCommand;
import com.example.smart_room.model.User;
import com.example.smart_room.response.CommandResponse;
import com.example.smart_room.service.AdafruitService;
import com.example.smart_room.service.GeminiService;
import com.example.smart_room.response.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@SecurityRequirement(name = "bearerAuth")
public class GeminiController {

    private final GeminiService geminiService;
    private final AdafruitService adafruitService;

    public GeminiController(GeminiService geminiService, AdafruitService adafruitService) {
        this.geminiService = geminiService;
        this.adafruitService = adafruitService;
    }

    @PostMapping("/chat")
    public ApiResponse<?> handleChat(@AuthenticationPrincipal User userDetails, @RequestBody  String message) {
        try {
            if (message == null || message.trim().isEmpty()) {
                return new ApiResponse<>(400, "⚠️ Message is empty", null);
            }

            CommandResponse getCommands = geminiService.analyzeCommand(message);
            List<ParsedCommand> commands = getCommands.getCommands();
            if (commands == null || commands.isEmpty()) {
                return new ApiResponse<>(400, getCommands.getMessage(), null);
            }

            List<Map<String, Object>> results = new ArrayList<>();

            for (ParsedCommand command : commands) {
                if (command == null || command.getDeviceKey() == null) continue;

                String type = command.getType().toUpperCase();
                String deviceKey = command.getDeviceKey();
                System.out.println("Command: " + command + ", Type: " + type + ", DeviceKey: " + deviceKey + "COMMAND".equals(type));
                if ("COMMAND".equals(type)) {
                    boolean result = adafruitService.sendCommandToDevice(deviceKey, command.getValue(), userDetails.getId());
                    results.add(Map.of(
                            "device", deviceKey,
                            "value", command.getValue(),
                            "type", "COMMAND",
                            "success", result
                    ));
                } else if ("QUERY".equals(type)) {
                    Map<String, Object> data = adafruitService.getFeedData(deviceKey);
                    results.add(Map.of(
                            "device", deviceKey,
                            "type", "QUERY",
                            "data", data
                    ));
                } else {
                    results.add(Map.of(
                            "device", deviceKey,
                            "type", type,
                            "error", "❌ Không xác định được loại lệnh"
                    ));
                }
            }

            if (results.isEmpty()) {
                return new ApiResponse<>(400, "❌ Không có lệnh hợp lệ được xử lý. Bạn vui lòng thử lại", null);
            }

            return new ApiResponse<>(200, getCommands.getMessage() , results);

        } catch (Exception e) {
            return new ApiResponse<>(500, "❌ Lỗi xử lý yêu cầu: " + e.getMessage(), null);
        }

    }
}

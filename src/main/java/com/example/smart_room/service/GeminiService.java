package com.example.smart_room.service;


import com.example.smart_room.common.ParsedCommand;
import com.example.smart_room.response.CommandResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.model}")
    private String geminiModel;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CommandResponse analyzeCommand(String userPrompt) {
        String fullPrompt = """
                Bạn là trợ lý điều khiển thiết bị thông minh trong nhà.
                Phân tích câu người dùng và trích xuất:
                - deviceKey (ví dụ: light, temp, humidity, air, device.fan(0-100), device.lamp(chỉ để bật tắt ON/OFF), device.door(ON/OFF), device.status-fan(Manual, Auto), device.status-lamp(Manual, Auto),...)
                - value nếu là lệnh điều khiển (ví dụ: "ON", "OFF", "20", "Manual", "Auto")
                - type: COMMAND hoặc QUERY

                Trả về **chỉ** JSON thuần túy, không bao quanh bởi ```json hay bất kỳ ký tự nào khác, với cấu trúc:
                {
                  "message": "thông báo thân thiện giao tiếp cho người dùng ",
                  "commands": [
                    {"deviceKey": "...", "value": "...", "type": "..."},
                    ...
                  ]
                }

                Ví dụ:
                - "Bật đèn phòng khách" -> {
                  "message": "Ok, mình đã bật đèn nhé!",
                  "commands": [
                    {"deviceKey": "device.lamp", "value": "ON", "type": "COMMAND"}
                  ]
                }
                - "Thay đổi độ sáng thành 50 và nhiệt độ là 24" -> {
                  "message": "Ok, mình đã thay đổi độ sáng thành 50 và nhiệt độ là 24, bạn cần giúp gì thêm không!",
                  "commands": [
                    {"deviceKey": "light", "value": "50", "type": "COMMAND"},
                    {"deviceKey": "temp", "value": "24", "type": "COMMAND"}
                  ]
                }
                - "Nhiệt độ bao nhiêu?" -> {
                  "message": "Đang kiểm tra nhiệt độ cho bạn!",
                  "commands": [
                    {"deviceKey": "temp", "value": null, "type": "QUERY"}
                  ]
                }
                - "abc" -> {
                  "message": "Lệnh không rõ, bạn muốn mình làm gì?",
                  "commands": []
                }

                Câu người dùng: "%s"
                """.formatted(userPrompt);

        try {
            // Tạo JSON payload
            ObjectNode payload = objectMapper.createObjectNode();

            // contents
            ObjectNode message = objectMapper.createObjectNode();
            ArrayNode parts = objectMapper.createArrayNode();
            ObjectNode part = objectMapper.createObjectNode();
            part.put("text", fullPrompt);
            parts.add(part);
            message.set("parts", parts);
            ArrayNode contents = objectMapper.createArrayNode();
            contents.add(message);
            payload.set("contents", contents);

            // generationConfig
            ObjectNode config = objectMapper.createObjectNode();
            config.put("temperature", 0.2);
            config.put("maxOutputTokens", 200);
            payload.set("generationConfig", config);

            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(payload), headers);

            // URL
            String fullUrl = geminiApiUrl + "?key=" + geminiApiKey;

            // Gửi request
            ResponseEntity<String> response = restTemplate.exchange(fullUrl, HttpMethod.POST, entity, String.class);

            // Log toàn bộ
            System.out.println("🔍 Full Gemini response: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode textNode = root.path("candidates").get(0).path("content").path("parts").get(0).path("text");

                if (textNode.isMissingNode()) {
                    System.err.println("❌ Không tìm thấy nội dung trong phản hồi Gemini");
                    return new CommandResponse("Không nhận được phản hồi từ Gemini", Collections.emptyList());
                }

                String jsonText = textNode.asText().trim();

                // Loại bỏ Markdown code block nếu có
                jsonText = jsonText.replaceAll("(?s)```json\\n(.*?)\\n```", "$1")
                        .replaceAll("(?s)```\\n(.*?)\\n```", "$1")
                        .trim();

                System.out.println("🧠 Gemini content: " + jsonText);

                // Parse JSON
                JsonNode parsedJson;
                try {
                    parsedJson = objectMapper.readTree(jsonText);
                } catch (Exception e) {
                    System.err.println("❌ JSON không hợp lệ: " + jsonText);
                    return new CommandResponse("JSON phản hồi không hợp lệ", Collections.emptyList());
                }

                // Trích xuất message
                String messageText = parsedJson.path("message").asText("Xử lý thành công");

                // Trích xuất commands
                List<ParsedCommand> commands = new ArrayList<>();
                JsonNode commandsNode = parsedJson.path("commands");

                if (commandsNode.isArray()) {
                    for (JsonNode item : commandsNode) {
                        String deviceKey = item.path("deviceKey").asText(null);
                        String value = item.has("value") && !item.get("value").isNull() ? item.get("value").asText() : null;
                        String type = item.path("type").asText(null);

                        if (deviceKey != null && type != null) {
                            commands.add(new ParsedCommand(deviceKey, value, type));
                        }
                    }
                }

                // Trả về CommandResponse
                return new CommandResponse(messageText, commands.isEmpty() ? Collections.emptyList() : commands);
            } else {
                System.err.println("❌ Phản hồi không hợp lệ từ Gemini: " + response.getStatusCode());
                return new CommandResponse("Phản hồi từ Gemini không hợp lệ", Collections.emptyList());
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi xử lý Gemini: " + e.getMessage());
            e.printStackTrace();
            return new CommandResponse("Lỗi hệ thống khi xử lý yêu cầu", Collections.emptyList());
        }
    }
}
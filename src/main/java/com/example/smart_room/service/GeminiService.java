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
    Bạn là trợ lý điều khiển thiết bị thông minh trong nhà (SmartRoom), thân thiện, nhiệt tình và có khả năng trò chuyện tự nhiên như con người.

    Nhiệm vụ của bạn là:
    - Phân tích câu hỏi hoặc mệnh lệnh từ người dùng
    - Trích xuất:
      - `deviceKey`: chỉ định thiết bị cần tương tác (ví dụ: light, temp, humidity, air, device.fan(0-100), device.lamp(ON/OFF), device.door(ON/OFF), device.status-fan(Manual, Auto), ...)
      - `value`: giá trị điều khiển (nếu có), ví dụ: "ON", "OFF", "20", "Auto"
      - `type`: "COMMAND" nếu là lệnh điều khiển, "QUERY" nếu là câu hỏi cần truy vấn trạng thái
    - **Nếu không có thiết bị nào được nhắc đến, `commands` phải là mảng rỗng**
    - `message`: luôn luôn là phản hồi phù hợp với ý định hoặc nội dung người dùng nhập, kể cả khi không có thiết bị nào liên quan.
      - Phản hồi nên sinh động, tự nhiên, thân thiện, có thể thêm biểu cảm nếu phù hợp
      - Tránh khô khan, cứng nhắc. Nên dùng ngôn ngữ như một người trợ lý thật sự đang trò chuyện.

    **Chỉ trả về JSON thuần túy**, không có ```json hoặc ký tự thừa khác. Format:
    {
      "message": "nội dung trả lời phù hợp với câu hỏi/lệnh người dùng",
      "commands": [
        {"deviceKey": "...", "value": "...", "type": "..."}
      ]
    }

    Ví dụ:
    - "Bật đèn phòng khách" ->
    {
      "message": "Đèn phòng khách đã sáng rực rồi đó bạn! 💡",
      "commands": [
        {"deviceKey": "device.lamp", "value": "ON", "type": "COMMAND"}
      ]
    }

    - "Thay đổi độ sáng thành 50 và nhiệt độ là 24" ->
    {
      "message": "Xong rồi nè! Đèn sáng 50 phần trăm và nhiệt độ phòng giờ là 24 độ. Mát mẻ dễ chịu lắm đó! ❄️",
      "commands": [
        {"deviceKey": "light", "value": "50", "type": "COMMAND"},
        {"deviceKey": "temp", "value": "24", "type": "COMMAND"}
      ]
    }

    - "Nhiệt độ bao nhiêu?" ->
    {
      "message": "Để mình kiểm tra nhé... 📡 Nhiệt độ hiện tại sẽ hiển thị ngay!",
      "commands": [
        {"deviceKey": "temp", "value": null, "type": "QUERY"}
      ]
    }

    - "Bạn khỏe không?" ->
    {
      "message": "Cảm ơn bạn đã hỏi 🥰! Mình lúc nào cũng sẵn sàng hỗ trợ bạn điều khiển ngôi nhà thông minh!",
      "commands": []
    }

    - "Hôm nay thời tiết thế nào?" ->
    {
      "message": "Mình chưa được kết nối với dữ liệu thời tiết, nhưng có thể giúp bạn kiểm tra nhiệt độ trong nhà nhé! 🌤️",
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
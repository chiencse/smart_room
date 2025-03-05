package com.example.smart_room.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;
import java.util.Map;

@Service
public class AdafruitService {

    @Value("${adafruit.api.base-url}")
    private String adafruitBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, Object>> getFeeds() {
        String url = adafruitBaseUrl + "/feeds";
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {
                });
        return response.getBody();
    }
}
package com.example.smart_room.controller;

import org.springframework.web.bind.annotation.*;
import com.example.smart_room.service.AdafruitService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/adafruit")
public class AdafruitController {

    private final AdafruitService adafruitService;

    public AdafruitController(AdafruitService adafruitService) {
        this.adafruitService = adafruitService;
    }

    @GetMapping("/feeds")
    public List<Map<String, Object>> getFeeds() {
        return adafruitService.getFeeds();
    }
}
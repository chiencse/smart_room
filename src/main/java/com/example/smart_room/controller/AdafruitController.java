package com.example.smart_room.controller;

import com.example.smart_room.service.AdafruitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adafruit")
public class AdafruitController {

    private final AdafruitService adafruitService;

    @Autowired
    public AdafruitController(AdafruitService adafruitService) {
        this.adafruitService = adafruitService;
    }

    // API để lấy danh sách feeds
    @GetMapping("/feeds")
    public Object getFeeds() {
        return adafruitService.getAllFeeds();
    }

    // API để đồng bộ dữ liệu của một feed cụ thể
    @GetMapping("/sync")
    public String syncFeedData(@RequestParam String feedKey) {
        try {
            adafruitService.fetchAndStoreFeedData(feedKey);
            return "Dữ liệu của feed '" + feedKey + "' đã được đồng bộ lên PostgreSQL và Firebase.";
        } catch (Exception e) {
            return "Lỗi khi đồng bộ: " + e.getMessage();
        }
    }
}

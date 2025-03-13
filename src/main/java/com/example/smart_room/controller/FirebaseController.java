package com.example.smart_room.controller;

import com.example.smart_room.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/firebase")
public class FirebaseController {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/save")
    public String saveData(@RequestParam String sensorId, @RequestParam double value) {
        firebaseService.saveData(sensorId, value);
        return "saved Firebase!";
    }
}
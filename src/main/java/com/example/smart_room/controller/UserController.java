package com.example.smart_room.controller;

import com.example.smart_room.model.User;
import com.example.smart_room.response.ApiResponse;
import com.example.smart_room.response.UserInfoResponse;
import com.example.smart_room.service.AuthService;
import com.example.smart_room.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        User user =  userService.getUserInfoFromToken(token);
        return ResponseEntity.ok(new ApiResponse<>(200, "OK!", new UserInfoResponse(user.getUsername(), user.getEmail(), user.getPhoneNumber())));
    }
}

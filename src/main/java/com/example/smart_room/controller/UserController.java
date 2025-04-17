package com.example.smart_room.controller;

import com.example.smart_room.model.User;
import com.example.smart_room.response.ApiResponse;
import com.example.smart_room.response.UserInfoResponse;
import com.example.smart_room.service.AuthService;
import com.example.smart_room.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ApiResponse<List<User>> getAllUsers() {
        return new ApiResponse<List<User>>(200, "OK!", userService.getAllUsers());
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/set-active/{id}")
    public ApiResponse<?> setUserActive(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return new ApiResponse<>(404, "User not found", null);
        }

        return new ApiResponse<User>(200, "OK!", userService.setActive(user));
    }
}

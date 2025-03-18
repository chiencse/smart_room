package com.example.smart_room.response;

import lombok.Data;

@Data
public class UserInfoResponse {
    private String username;
    private String email;
    private String phoneNumber;

    public UserInfoResponse(String username, String email, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}

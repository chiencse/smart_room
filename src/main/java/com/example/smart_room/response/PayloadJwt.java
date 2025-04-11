package com.example.smart_room.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayloadJwt {
    private String username;
    private Set<String> roles;  // To carry the user's roles

    // Optional fields based on your needs
    private Long id;           // If you need user ID in the token
    private String email;

}

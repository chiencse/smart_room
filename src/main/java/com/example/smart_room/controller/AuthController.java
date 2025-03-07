package com.example.smart_room.controller;


import com.example.smart_room.request.LoginRequestDto;
import com.example.smart_room.response.ApiResponse;
import com.example.smart_room.response.JwtResponse;
import com.example.smart_room.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ApiResponse<JwtResponse>  login(@RequestBody LoginRequestDto loginRequest) throws Exception {
        // Authenticate the user
        System.out.println(loginRequest);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        System.out.println(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        // Generate JWT token
        String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return new ApiResponse<JwtResponse>(200, "Login successful", new JwtResponse(jwt, userDetails.getUsername()));
    }
}
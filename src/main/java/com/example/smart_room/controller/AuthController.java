package com.example.smart_room.controller;

import com.example.smart_room.model.User;
import com.example.smart_room.request.LoginRequestDto;
import com.example.smart_room.request.RegisterRequestDto;
import com.example.smart_room.response.ApiResponse;
import com.example.smart_room.response.JwtResponse;
import com.example.smart_room.security.JwtUtil;
import com.example.smart_room.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;

@RestController
@RequestMapping("/auth")

public class AuthController {

    @Value("${frontend.url}")
    private String frontendUrl;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    private final AuthService authService;

    @Autowired
    private UserDetailsService userDetailsService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Invalid username or password"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<JwtResponse>> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

            // Generate JWT token
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(new ApiResponse<>(200, "Login successful", new JwtResponse(jwt, userDetails.getUsername())));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(401, "Invalid username or password", null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Internal server error", null));
        }
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody RegisterRequestDto registerRequest) {
        try {
            User user = new User();

            user.setUsername(registerRequest.getUsername());
            user.setPassword(registerRequest.getPassword());
            user.setEmail(registerRequest.getEmail());
            user.setPhoneNumber(registerRequest.getPhoneNumber());

            authService.register(user);

            return ResponseEntity.ok(new ApiResponse<>(200, "Register successfully", null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, ex.getMessage(), null));
        }
    }


    @GetMapping("/google/login")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String url = authService.getGoogleAuthUrl();
        response.sendRedirect(url);
    }

    @GetMapping("/google/redirect")
    public void handleGoogleLogin(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        String jwt = authService.loginWithGoogle(code);

        if (jwt == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return;
        }
        // Chuyển hướng về FE với JWT trong URL hoặc lưu vào cookie
        System.out.println("JWT: " + jwt);
        response.sendRedirect("/test?token=" + jwt);
//        String fe = "${frontendUrl}?token=" + jwt;
//        response.sendRedirect(fe);
    }
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/test")
    public ApiResponse<String> test() {
        return new ApiResponse<String>(200, "Heelll", "");
    }
}
package com.example.smart_room.service;

import com.example.smart_room.model.User;
import com.example.smart_room.repository.UserRepo;
import com.example.smart_room.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;
    private final RestTemplate restTemplate;

    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String login(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        User user = this.userRepository.findByUsername(username);
        return jwtUtil.generateToken(user);
    }

     public void register(User user) {
         if (userRepository.findByUsername(user.getUsername()) != null) {
             throw new IllegalArgumentException("Username has been existed");
         }

         if (userRepository.findByEmail(user.getEmail()) != null) {
             throw new IllegalArgumentException("Email has been existed");
         }

         if (userRepository.findByPhoneNumber(user.getPhoneNumber()) != null) {
             throw new IllegalArgumentException("Phone number has been existed");
         }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
     }

    public String getGoogleAuthUrl() {
        return "https://accounts.google.com/o/oauth2/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=email%20profile";
    }

    // Đổi code lấy thông tin user từ Google và tạo JWT
    public String loginWithGoogle(String code) {
        String tokenEndpoint = "https://oauth2.googleapis.com/token";

        // Gửi request lấy access_token
        Map<String, String> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");
        params.put("code", code);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, params, Map.class);
        String accessToken = (String) response.getBody().get("access_token");

        // Lấy thông tin user từ Google
        String userInfoEndpoint = "https://www.googleapis.com/oauth2/v2/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> userResponse = restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, entity, Map.class);

        String email = (String) userResponse.getBody().get("email");
        String name = (String) userResponse.getBody().get("name");

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        return jwtUtil.generateToken(user);
    }

    @Service
    public static class CustomUserDetailsService implements UserDetailsService {

        @Autowired
        private UserRepo userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUsername(username);
            System.out.println(user);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
            return user;
        }
    }

}

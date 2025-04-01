package com.example.smart_room.service;

import com.example.smart_room.model.User;
import com.example.smart_room.repository.UserRepo;
import com.example.smart_room.response.JwtResponse;
import com.example.smart_room.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        System.out.println("Authentication success");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        System.out.println("Email: " + email + " Name: " + name);

        // Kiểm tra nếu user chưa tồn tại thì lưu vào database
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            System.out.println("User not found, creating new user");
        }

        // Tạo JWT Token
        String jwtToken = jwtUtil.generateToken(existingUser);

        System.out.println("Token: " + jwtToken  );
        // Trả token về client
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(
                new JwtResponse(jwtToken, email)
        ));
//        String redirectUrl = "http://localhost:3000/auth/callback?token=" + jwtToken;
//        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}

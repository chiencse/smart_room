package com.example.smart_room.service;

import com.example.smart_room.model.User;
import com.example.smart_room.repository.UserRepo;
import com.example.smart_room.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepository;

    public User getUserInfoFromToken(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        return userRepository.findByUsername(username);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User setActive(User user) {
        user.setIsActive(!user.getIsActive());
        return userRepository.save(user);
    }
}

package com.example.smart_room.service;

import com.example.smart_room.model.User;
import com.example.smart_room.repository.UserRepo;
import com.example.smart_room.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public String login(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return jwtUtil.generateToken(username);
    }

//    public User register(User user) {
//        user.setHash_password(passwordEncoder.encode(user.getHash_password()));
//        return userRepository.save(user);
//    }

    @Service
    public static class CustomUserDetailsService implements UserDetailsService {

        @Autowired
        private UserRepo userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            System.out.println("Get Username" + username);
            User user = userRepository.findByUsername(username);
            System.out.println(user);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
            return user;
        }
    }
}

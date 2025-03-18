package com.example.smart_room;

import com.example.smart_room.model.User;
import com.example.smart_room.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartRoomApplication implements CommandLineRunner {

	@Autowired
	private UserRepo userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SmartRoomApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}
}

package com.example.smart_room;

import com.example.smart_room.model.User;
import com.example.smart_room.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Set;

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

		if (userRepository.count() == 0) {
			User admin = new User();
			admin.setEmail("admin@smartroom.com");
			admin.setUsername("admin");
			admin.setPhoneNumber("0123456789");
			admin.setPassword("admin");
			admin.setRoles(Set.of("ADMIN"));

			userRepository.save(admin);
			System.out.println("✅ Admin user created successfully!");
		} else {
			System.out.println("ℹ️ Admin user already exists.");
		}
	}
}

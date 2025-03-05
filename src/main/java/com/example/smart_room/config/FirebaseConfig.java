package com.example.smart_room.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = Logger.getLogger(FirebaseConfig.class.getName());

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        String serviceAccountPath = "src/main/resources/serviceAccountKey.json";
        logger.info("Initializing Firebase with service account key: " + serviceAccountPath);

        FileInputStream serviceAccount;
        try {
            serviceAccount = new FileInputStream(serviceAccountPath);
        } catch (IOException e) {
            logger.severe("Service account key file not found: " + serviceAccountPath);
            throw e;
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://smarthouse-6b26c-default-rtdb.firebaseio.com/")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            logger.info("Initializing new FirebaseApp instance.");
            return FirebaseApp.initializeApp(options);
        } else {
            logger.info("Using existing FirebaseApp instance.");
            return FirebaseApp.getInstance();
        }
    }
}
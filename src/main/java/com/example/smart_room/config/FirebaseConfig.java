package com.example.smart_room.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = Logger.getLogger(FirebaseConfig.class.getName());

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        String serviceAccountPath = "src/main/resources/serviceAccountKey.json";
        logger.info("Initializing Firebase with service account key: " + serviceAccountPath);

        FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://smarthouse-6b26c-default-rtdb.firebaseio.com/") // thay bằng URL Firebase của
                                                                                         // bạn
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        } else {
            return FirebaseApp.getInstance();
        }
    }

    @Bean
    public FirebaseDatabase firebaseDatabase(FirebaseApp firebaseApp) {
        return FirebaseDatabase.getInstance(firebaseApp);
    }
}

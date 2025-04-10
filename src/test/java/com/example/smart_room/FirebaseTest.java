package com.example.smart_room;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseTest {
    public static void main(String[] args) {
        try {
            // Đảm bảo đường dẫn chính xác
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://smarthouse-6b26c-default-rtdb.firebaseio.com/") // Thay bằng Firebase
                                                                                             // Database URL của bạn
                    .build();

            FirebaseApp.initializeApp(options);

            // Kiểm tra kết nối với database
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("test_connection");
            ref.setValueAsync("Firebase kết nối thành công!");

            System.out.println("✅ Kết nối Firebase thành công!");
        } catch (IOException e) {
            System.out.println("❌ Lỗi khi kết nối Firebase:");
            e.printStackTrace();
        }
    }
}

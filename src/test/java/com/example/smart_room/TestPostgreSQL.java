package com.example.smart_room;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestPostgreSQL {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/smart_room"; // Thay localhost bằng host của bạn
        String user = "postgres"; // Thay bằng user PostgreSQL
        String password = "tan01012004"; // Thay bằng mật khẩu của bạn

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("✅ Kết nối PostgreSQL thành công!");
            } else {
                System.out.println("❌ Kết nối PostgreSQL thất bại!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi kết nối PostgreSQL:");
            e.printStackTrace();
        }
    }
}

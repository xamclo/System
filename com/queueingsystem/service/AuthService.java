package com.queueingsystem.service;

import com.queueingsystem.enumtypes.OfficeType;
import com.queueingsystem.model.AdminAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {

    public AuthService() {
        DatabaseManager.initializeDatabase();
    }

    public AdminAccount login(String username, String password) {
        String sql = """
                SELECT username, password, office_type, window_number
                FROM admin_accounts
                WHERE LOWER(username) = LOWER(?)
                AND password = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, password.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AdminAccount(
                            rs.getString("username"),
                            rs.getString("password"),
                            OfficeType.valueOf(rs.getString("office_type")),
                            rs.getInt("window_number")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
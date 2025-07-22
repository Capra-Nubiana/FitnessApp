package com.fitness;

import java.sql.*;

public class DatabaseLogger {
    private static Connection conn;

    public static void init() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:fitness.db");
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS logs (user TEXT, label TEXT, value INTEGER, timestamp TEXT)");
            }
        } catch (SQLException e) {
            System.out.println("DB init error: " + e.getMessage());
        }
    }

    public static void log(String user, String label, int value) {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO logs VALUES (?, ?, ?, datetime('now'))")) {
            stmt.setString(1, user);
            stmt.setString(2, label);
            stmt.setInt(3, value);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("DB log error: " + e.getMessage());
        }
    }
}

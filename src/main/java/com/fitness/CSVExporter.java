package com.fitness;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import javax.swing.JOptionPane;
public class CSVExporter {

    public static void exportUserLogs(String user, String filename) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:fitness.db");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM logs WHERE user = ?");
             FileWriter writer = new FileWriter(filename)) {

            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();

            writer.write("User,Label,Value,Timestamp\n"); // CSV header

            while (rs.next()) {
                writer.write(String.format("%s,%s,%d,%s\n",
                        rs.getString("user"),
                        rs.getString("label"),
                        rs.getInt("value"),
                        rs.getString("timestamp")));
            }

            JOptionPane.showMessageDialog(null, "CSV Exported to: " + filename);
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(null, "CSV Export failed: " + e.getMessage());
        }
    }
}

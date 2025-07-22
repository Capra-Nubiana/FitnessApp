package com.fitness;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class DataLogger {
    public static void log(String label, int value) {
        try (FileWriter writer = new FileWriter("fitness_log.txt", true)) {
            writer.write(LocalDateTime.now() + " - " + label + ": " + value + "\n");
        } catch (IOException e) {
            System.out.println("Failed to log data: " + e.getMessage());
        }
    }
}

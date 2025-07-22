package com.fitness;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderManager {
    private static Timer timer = new Timer();

    public static void startReminders() {
        // Every 2 hours (7200000 ms) or adjust to your test frequency
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null,
                            "Reminder: Time to move or hydrate!",
                            "Fitness Reminder",
                            JOptionPane.INFORMATION_MESSAGE);
                });
            }
        }, 10000, 7200000); // First reminder after 10s (test), then every 2hrs
    }

    public static void stopReminders() {
        timer.cancel();
    }
}

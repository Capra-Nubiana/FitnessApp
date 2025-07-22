package com.fitness;

import javax.swing.*;
import java.awt.*;

public class FitnessApp extends JFrame {
    private int steps = 0;
    private int calories = 0;
    private final JLabel stepLabel = new JLabel("Steps: 0");
    private final JLabel calorieLabel = new JLabel("Calories: 0");
    private final ChartPanelWrapper chartPanel;

    public FitnessApp() {
        setTitle("Fitness Tracker App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 500);
        setLayout(new BorderLayout());

        // 🔐 User login
        String username = JOptionPane.showInputDialog(this, "Enter Username:");
        if (username == null || username.trim().isEmpty()) username = "guest";
        UserProfileManager.login(username);

        // 🔄 Initialize SQLite and Reminders
        DatabaseLogger.init();
        ReminderManager.startReminders();

        // 🔼 Top panel (controls + stats)
        JPanel topPanel = new JPanel(new FlowLayout());
        JButton stepButton = new JButton("Simulate Step");
        JButton saveButton = new JButton("Save Log");
        JButton exportButton = new JButton("Export to CSV");
        topPanel.add(stepButton);
        topPanel.add(saveButton);
        topPanel.add(exportButton);
        topPanel.add(stepLabel);
        topPanel.add(calorieLabel);

        // 📊 Center panel - Chart
        chartPanel = new ChartPanelWrapper();
        add(chartPanel.getChartPanel(), BorderLayout.CENTER);

        // ⏱️ Right panel - Workout timer
        TimerPanel timerPanel = new TimerPanel();
        add(timerPanel, BorderLayout.EAST);

        // ▶️ Simulate Step event
        stepButton.addActionListener(e -> {
            UserProfileManager.addStep();
            steps = UserProfileManager.getSteps();
            calories = calculateCalories(steps);

            // Update UI
            stepLabel.setText("Steps: " + steps);
            calorieLabel.setText("Calories: " + calories);

            // Update Chart
            chartPanel.updateChart(steps);

            // Log to SQLite
            DatabaseLogger.log(UserProfileManager.getCurrentUser(), "step", 1);
            DatabaseLogger.log(UserProfileManager.getCurrentUser(), "calories", calories);

            // 🔁 Sync to Firebase
            RemoteSyncManager.syncStep(UserProfileManager.getCurrentUser(), steps);
            RemoteSyncManager.logToFirebase("steps", steps);
            RemoteSyncManager.logToFirebase("calories", calories);
            UserProfileManager.syncProfileToCloud(); // 🔄 Full profile sync
        });

        // 💾 Save log (informative only - actual logging is automatic)
        saveButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "✅ Data auto-saved to SQLite!")
        );

        // 📁 Export logs to CSV
        exportButton.addActionListener(e -> {
            String user = UserProfileManager.getCurrentUser();
            CSVExporter.exportUserLogs(user, user + "_fitness_logs.csv");
        });

        // 🔼 Attach top panel
        add(topPanel, BorderLayout.NORTH);
        setVisible(true);
    }

    // 🔢 Calorie calculation logic
    private int calculateCalories(int steps) {
        return steps / 20; // Basic formula (can be enhanced later)
    }

    public static void main(String[] args) {
        // 🔌 Firebase + DB init
        FirebaseInitializer.init();
        DatabaseLogger.init();

        // 🚀 Launch app on EDT
        SwingUtilities.invokeLater(FitnessApp::new);
    }
}

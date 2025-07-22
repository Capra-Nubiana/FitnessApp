package com.fitness;

import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ChartPanelWrapper {
    private final DefaultCategoryDataset dataset;
    private final ChartPanel chartPanel;
    private final JPanel containerPanel;

    public ChartPanelWrapper() {
        dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createLineChart(
                "Steps Over Time", "Date", "Steps",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        plot.setRenderer(renderer);

        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 300));

        JButton dailyButton = new JButton("Daily View");
        JButton weeklyButton = new JButton("Weekly View");
        JButton monthlyButton = new JButton("Monthly View");

        dailyButton.addActionListener(e -> loadDailyData());
        weeklyButton.addActionListener(e -> loadWeeklyData());
        monthlyButton.addActionListener(e -> loadMonthlyData());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(dailyButton);
        buttonPanel.add(weeklyButton);
        buttonPanel.add(monthlyButton);

        containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(chartPanel, BorderLayout.CENTER);
        containerPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadDailyData();  // Load default view
    }

    public JPanel getChartPanel() {
        return containerPanel;
    }

    private void loadDailyData() {
        dataset.clear();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:fitness.db")) {
            String query = """
                    SELECT DATE(timestamp) as day, SUM(value) as steps 
                    FROM logs 
                    WHERE user = ? AND label = 'step'
                    GROUP BY day 
                    ORDER BY day DESC LIMIT 7
                    """;
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, UserProfileManager.getCurrentUser());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String date = rs.getString("day");
                int steps = rs.getInt("steps");
                dataset.addValue(steps, "Steps", date);
            }
        } catch (SQLException e) {
            System.out.println("Chart load error (daily): " + e.getMessage());
        }
    }

    private void loadWeeklyData() {
        dataset.clear();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:fitness.db")) {
            String query = """
                    SELECT strftime('%Y-W%W', timestamp) as week, SUM(value) as steps 
                    FROM logs 
                    WHERE user = ? AND label = 'step'
                    GROUP BY week 
                    ORDER BY week DESC LIMIT 4
                    """;
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, UserProfileManager.getCurrentUser());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String week = rs.getString("week");
                int steps = rs.getInt("steps");
                dataset.addValue(steps, "Steps", week);
            }
        } catch (SQLException e) {
            System.out.println("Chart load error (weekly): " + e.getMessage());
        }
    }

    private void loadMonthlyData() {
        dataset.clear();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:fitness.db")) {
            String query = """
                    SELECT strftime('%Y-%m', timestamp) as month, SUM(value) as steps 
                    FROM logs 
                    WHERE user = ? AND label = 'step'
                    GROUP BY month 
                    ORDER BY month DESC LIMIT 6
                    """;
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, UserProfileManager.getCurrentUser());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String month = rs.getString("month");
                int steps = rs.getInt("steps");
                dataset.addValue(steps, "Steps", month);
            }
        } catch (SQLException e) {
            System.out.println("Chart load error (monthly): " + e.getMessage());
        }
    }

    // Optional: allow updating chart on live step count
    public void updateChart(int steps) {
        dataset.addValue(steps, UserProfileManager.getCurrentUser(), "T+" + System.currentTimeMillis() / 1000 + "s");
    }
}

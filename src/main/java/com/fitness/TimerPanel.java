package com.fitness;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerPanel extends JPanel {
    private Timer timer;
    private int seconds = 0;
    private JLabel timeLabel;

    public TimerPanel() {
        timeLabel = new JLabel("Workout Time: 0s");
        JButton startButton = new JButton("Start Timer");

        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                seconds++;
                timeLabel.setText("Workout Time: " + seconds + "s");
            }
        });

        startButton.addActionListener(e -> timer.start());
        add(startButton);
        add(timeLabel);
    }
}

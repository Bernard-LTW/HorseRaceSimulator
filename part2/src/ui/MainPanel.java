package ui;

import javax.swing.*;
import java.awt.*;

import static ui.Button.createMenuButton;

public class MainPanel extends JPanel {
    public MainPanel() {
        setLayout(new BorderLayout(10, 20)); // Add some spacing between components

        JLabel bannerLabel = new JLabel("Horse Race Simulator", SwingConstants.CENTER);
        bannerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        bannerLabel.setForeground(new Color(51, 51, 51));
        bannerLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton transactionsButton = createMenuButton("Transactions");
        JButton raceSetupButton = createMenuButton("New Race");
        JButton statisticsButton = createMenuButton("Statistics");
        JButton horseCustomizerButton = createMenuButton("Horse Customizer");
        JButton trackCustomizerButton = createMenuButton("Track Customizer");

        transactionsButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) getParent().getLayout();
            cardLayout.show(getParent(), "TRANSACTIONS");
        });

        raceSetupButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) getParent().getLayout();
            cardLayout.show(getParent(), "RACE_SETUP");
        });

        statisticsButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) getParent().getLayout();
            cardLayout.show(getParent(), "STATISTICS");
        });

        horseCustomizerButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) getParent().getLayout();
            cardLayout.show(getParent(), "HORSE_CUSTOMIZER");
        });

        trackCustomizerButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) getParent().getLayout();
            cardLayout.show(getParent(), "TRACK_MANAGEMENT");
        });

        buttonPanel.add(transactionsButton);
        buttonPanel.add(raceSetupButton);
        buttonPanel.add(statisticsButton);
        buttonPanel.add(horseCustomizerButton);
        buttonPanel.add(trackCustomizerButton);

        add(bannerLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }


}

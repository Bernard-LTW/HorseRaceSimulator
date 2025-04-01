package ui;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class MainPanel extends JPanel {
    public MainPanel() {
        setLayout(new BorderLayout(10, 20)); // Add some spacing between components

        // Create and style the banner
        JLabel bannerLabel = new JLabel("Horse Race Simulator", SwingConstants.CENTER);
        bannerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        bannerLabel.setForeground(new Color(51, 51, 51));
        bannerLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        // Create panel for buttons with GridLayout (changed to 2x3)
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create and style the buttons
        JButton transactionsButton = createMenuButton("Transactions");
        JButton raceSetupButton = createMenuButton("New Race");
        JButton bettingStatsButton = createMenuButton("Betting Stats");
        JButton horseStatsButton = createMenuButton("Horse Stats");
        JButton horseCustomizerButton = createMenuButton("Horse Customizer");
        JButton trackCustomizerButton = createMenuButton("Track Customizer");

        // Add action listeners
        transactionsButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) getParent().getLayout();
            cardLayout.show(getParent(), "TRANSACTIONS");
        });

        raceSetupButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) getParent().getLayout();
            cardLayout.show(getParent(), "RACE_SETUP");
        });

        bettingStatsButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) getParent().getLayout();
            cardLayout.show(getParent(), "STATISTICS");
        });

        horseStatsButton.addActionListener(e -> {
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

        // Add buttons to the grid
        buttonPanel.add(transactionsButton);
        buttonPanel.add(raceSetupButton);
        buttonPanel.add(bettingStatsButton);
        buttonPanel.add(horseStatsButton);
        buttonPanel.add(horseCustomizerButton);
        buttonPanel.add(trackCustomizerButton);

        // Add components to main panel
        add(bannerLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(200, 200));
        button.setBackground(new Color(70, 130, 180)); // Steel blue color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237)); // Lighter blue when hovering
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180)); // Return to original color
            }
        });

        return button;
    }
}

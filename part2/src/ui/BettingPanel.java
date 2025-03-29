package ui;

import models.Race;
import javax.swing.*;
import java.awt.*;

public class BettingPanel extends JPanel {
    private Race currentRace;

    public BettingPanel() {
        setLayout(new BorderLayout(10, 20));
        setBackground(new Color(70, 130, 180));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        headerPanel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("Place Your Bets", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Placeholder message
        JLabel placeholderLabel = new JLabel("Betting functionality coming soon...", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.BOLD, 18));
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBackground(Color.WHITE);

        JButton startRaceButton = createStyledButton("Start Race");
        JButton backButton = createStyledButton("Back");

        startRaceButton.addActionListener(e -> startRace());
        backButton.addActionListener(e -> goBack());

        buttonsPanel.add(startRaceButton);
        buttonsPanel.add(backButton);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void setRace(Race race) {
        this.currentRace = race;
    }

    private void startRace() {
        if (currentRace != null) {
            // Get the RaceSimulationPanel from the parent container
            CardLayout cardLayout = (CardLayout) getParent().getLayout();
            RaceSimulationPanel raceSimPanel = (RaceSimulationPanel) getParent().getComponent(5); // Index of RACE_SIMULATION panel
            raceSimPanel.setRace(currentRace);
            cardLayout.show(getParent(), "RACE_SIMULATION");
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 35));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
                button.setBorder(BorderFactory.createLineBorder(new Color(133, 193, 233), 2));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
                button.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
            }
        });

        return button;
    }

    private void goBack() {
        CardLayout cardLayout = (CardLayout) getParent().getLayout();
        cardLayout.show(getParent(), "RACE_SETUP");
    }
} 
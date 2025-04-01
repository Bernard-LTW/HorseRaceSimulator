package ui;

import models.Race;
import models.Horse;
import models.Bet;
import core.BetManager;
import models.Track;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class RaceSimulationPanel extends JPanel {
    private RaceVisualizationPanel visualPanel;
    private Race currentRace;
    private BetManager betManager;

    public RaceSimulationPanel(BetManager betManager) {
        setLayout(new BorderLayout(10, 20));
        setBackground(new Color(70, 130, 180));
        this.betManager = betManager;

        // // Header
        // JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        // headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        // headerPanel.setBackground(new Color(70, 130, 180));

        // JLabel titleLabel = new JLabel("Race Simulation", SwingConstants.CENTER);
        // titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        // titleLabel.setForeground(Color.WHITE);
        // headerPanel.add(titleLabel, BorderLayout.CENTER);

        // add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Create visual race panel
        visualPanel = new RaceVisualizationPanel(betManager);
        contentPanel.add(visualPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> goBack());
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setRace(Race race) {
        this.currentRace = race;
        visualPanel.setRace(race);
    }

    public void startRace(Race race) {
        if (race != null) {
            // Start the visual race
            visualPanel.startRace();
            
            // Start a background thread to run the race logic
            new Thread(() -> {
                // Run the race logic
                race.startRace();
                
                // When race is complete, stop the visualization and show results
                SwingUtilities.invokeLater(() -> {
                    visualPanel.stopRace();
                    visualPanel.showRaceResults(race);
                });
            }).start();
        }
    }

    private void goBack() {
        CardLayout cardLayout = (CardLayout) getParent().getLayout();
        cardLayout.show(getParent(), "MAIN");
    }
} 
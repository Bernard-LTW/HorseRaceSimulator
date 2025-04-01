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
    private JTextArea resultsArea;
    private Race currentRace;
    private BetManager betManager;

    public RaceSimulationPanel(BetManager betManager) {
        setLayout(new BorderLayout(10, 20));
        setBackground(new Color(70, 130, 180));
        this.betManager = betManager;

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        headerPanel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("Race Simulation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Create visual race panel
        visualPanel = new RaceVisualizationPanel();
        contentPanel.add(visualPanel, BorderLayout.CENTER);

        // Create results area
        resultsArea = new JTextArea();
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultsArea.setEditable(false);
        resultsArea.setBackground(Color.WHITE);
        resultsArea.setForeground(Color.BLACK);
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);
        resultsArea.setBorder(BorderFactory.createTitledBorder("Race Results"));

        JScrollPane resultsScroll = new JScrollPane(resultsArea);
        resultsScroll.setPreferredSize(new Dimension(200, 150));
        contentPanel.add(resultsScroll, BorderLayout.SOUTH);

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
                
                // When race is complete, update results
                SwingUtilities.invokeLater(() -> {
                    visualPanel.stopRace();
                    updateResults();
                });
            }).start();
        }
    }

    private void updateResults() {
        StringBuilder results = new StringBuilder();
        results.append("=== RACE RESULTS ===\n\n");
        
        List<Horse> finishOrder = currentRace.getFinishOrder();
        for (int i = 0; i < finishOrder.size(); i++) {
            Horse horse = finishOrder.get(i);
            results.append(String.format("%d. %s (%c)\n", 
                i + 1, 
                horse.getName(), 
                horse.getSymbol()));
        }
        
        // Add fallen horses
        for (Horse horse : currentRace.getLanes()) {
            if (horse.hasFallen()) {
                results.append(String.format("DNF. %s (%c) - Fell at %d units\n",
                    horse.getName(),
                    horse.getSymbol(),
                    horse.getDistanceTravelled()));
            }
        }
        
        // Add betting results if available
        List<Bet> bets = betManager.getRaceBets(currentRace.getRaceID());
        if (!bets.isEmpty()) {
            results.append("\n=== BETTING RESULTS ===\n");
            for (Bet bet : bets) {
                results.append(String.format("%s (%c) - $%.2f ", 
                    bet.getHorseName(),
                    bet.getHorseSymbol(),
                    bet.getAmount()));
                if (bet.isWon()) {
                    results.append(String.format("WON: $%.2f\n", bet.getWinnings()));
                } else {
                    results.append("LOST\n");
                }
            }
        }
        
        resultsArea.setText(results.toString());
    }

    private void goBack() {
        CardLayout cardLayout = (CardLayout) getParent().getLayout();
        cardLayout.show(getParent(), "MAIN");
    }
} 
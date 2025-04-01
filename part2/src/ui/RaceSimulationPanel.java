package ui;

import models.Race;
import models.Horse;
import models.Bet;
import core.BetManager;
import models.Track;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class RaceSimulationPanel extends JPanel {
    private JTextArea raceDisplay;
    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;
    private StringBuilder currentOutput = new StringBuilder();
    private Timer updateTimer;
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

        // Create race display
        raceDisplay = new JTextArea();
        raceDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        raceDisplay.setEditable(false);
        raceDisplay.setBackground(Color.WHITE);
        raceDisplay.setForeground(Color.BLACK);
        raceDisplay.setLineWrap(true);
        raceDisplay.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(raceDisplay);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getViewport().setForeground(Color.BLACK);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBackground(Color.WHITE);

        JButton backButton = createStyledButton("Back to Main");
        backButton.addActionListener(e -> goBack());

        buttonsPanel.add(backButton);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);

        // Add component listener to handle cleanup when panel is hidden
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentHidden(java.awt.event.ComponentEvent e) {
                cleanup();
            }
        });
    }

    private void setupOutputRedirection() {
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
        System.setOut(printStream);
    }

    private void cleanup() {
        // Stop the update timer if it's running
        if (updateTimer != null && updateTimer.isRunning()) {
            updateTimer.stop();
        }
        
        // Restore original output stream
        if (originalOut != null) {
            System.setOut(originalOut);
        }
    }

    public void startRace(Race race) {
        // Clear previous output
        if (outputStream != null) {
            outputStream.reset();
        }
        raceDisplay.setText("");
        currentOutput.setLength(0);
        
        // Stop any existing timer
        if (updateTimer != null && updateTimer.isRunning()) {
            updateTimer.stop();
        }
        
        // Setup output redirection before starting the race
        setupOutputRedirection();
        
        // Run the race in a separate thread
        new Thread(() -> {
            try {
                // Start the race
                race.startRace();
                
                // After race is complete, update the display and show summary
                SwingUtilities.invokeLater(() -> {
                    raceDisplay.setText(outputStream.toString());
                    raceDisplay.setCaretPosition(raceDisplay.getDocument().getLength());
                    
                    // Process bets and show summary only after race is complete
                    betManager.processRaceResults(race);
                    showBetSummary(race);
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Clean up the output redirection
                cleanup();
            }
        }).start();

        // Start a timer to update the display every 100ms
        updateTimer = new Timer(100, e -> {
            try {
                if (outputStream != null) {
                    String newOutput = outputStream.toString();
                    if (!newOutput.equals(currentOutput.toString())) {
                        currentOutput.setLength(0);
                        currentOutput.append(newOutput);
                        raceDisplay.setText(currentOutput.toString());
                        raceDisplay.setCaretPosition(raceDisplay.getDocument().getLength());
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        updateTimer.start();
    }

    public void setRace(Race race) {
        this.currentRace = race;
        // Start the race immediately when received
        startRace(race);
    }

    private void showBetSummary(Race race) {
        List<Bet> raceBets = betManager.getRaceBets(race.getRaceID());
        if (raceBets.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No bets were placed on this race.",
                "Race Complete",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Calculate total bets and winnings
        double totalBets = 0.0;
        double totalWinnings = 0.0;
        StringBuilder summary = new StringBuilder();
        summary.append("<html><body style='width: 400px'>");
        summary.append("<h2>Race Summary</h2>");
        summary.append("<p>Race ID: ").append(race.getRaceID()).append("</p>");
        summary.append("<p>Winner: ").append(race.getFinishOrder().get(0).getName()).append("</p>");
        
        // Add confidence changes section
        summary.append("<br><h3>Confidence Changes:</h3>");
        summary.append("<table style='width:100%'>");
        summary.append("<tr><th>Horse</th><th>Change</th></tr>");
        
        Map<Horse, Double> confidenceChanges = race.getConfidenceChanges();
        for (Map.Entry<Horse, Double> entry : confidenceChanges.entrySet()) {
            Horse horse = entry.getKey();
            double change = entry.getValue();
            String changeStr = String.format("%.2f", change * 100);
            if (change > 0) {
                changeStr = "+" + changeStr;
            }
            
            summary.append("<tr>");
            summary.append("<td>").append(horse.getName()).append("</td>");
            summary.append("<td>").append(changeStr).append("%</td>");
            summary.append("</tr>");
        }
        summary.append("</table>");
        
        // Add betting summary section
        summary.append("<br><h3>Your Bets:</h3>");
        summary.append("<table style='width:100%'>");
        summary.append("<tr><th>Horse</th><th>Amount</th><th>Result</th><th>Winnings</th></tr>");
        
        for (Bet bet : raceBets) {
            totalBets += bet.getAmount();
            totalWinnings += bet.getWinnings();
            
            summary.append("<tr>");
            summary.append("<td>").append(bet.getHorseName()).append("</td>");
            summary.append("<td>$").append(String.format("%.2f", bet.getAmount())).append("</td>");
            summary.append("<td>").append(bet.isWon() ? "Won" : "Lost").append("</td>");
            summary.append("<td>$").append(String.format("%.2f", bet.getWinnings())).append("</td>");
            summary.append("</tr>");
        }
        
        summary.append("</table>");
        summary.append("<br><h3>Summary:</h3>");
        summary.append("<p>Total Bets: $").append(String.format("%.2f", totalBets)).append("</p>");
        summary.append("<p>Total Winnings: $").append(String.format("%.2f", totalWinnings)).append("</p>");
        summary.append("<p>Net Result: $").append(String.format("%.2f", totalWinnings - totalBets)).append("</p>");
        summary.append("</body></html>");
        
        JOptionPane.showMessageDialog(this,
            summary.toString(),
            "Race Complete - Summary",
            JOptionPane.INFORMATION_MESSAGE);
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
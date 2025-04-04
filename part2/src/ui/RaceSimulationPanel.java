package ui;

import models.Race;
import core.BetManager;

import javax.swing.*;
import java.awt.*;

import static ui.Button.createStyledButton;


public class RaceSimulationPanel extends JPanel {
    private RaceVisualizationPanel visualPanel;
    private Race currentRace;
    private BetManager betManager;

    public RaceSimulationPanel(BetManager betManager) {
        setLayout(new BorderLayout(10, 20));
        setBackground(new Color(70, 130, 180));
        this.betManager = betManager;

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        visualPanel = new RaceVisualizationPanel(betManager);
        contentPanel.add(visualPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton backButton = createStyledButton("Back to Main Menu");
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
            visualPanel.startRace();
            new Thread(() -> {
                race.startRace();
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
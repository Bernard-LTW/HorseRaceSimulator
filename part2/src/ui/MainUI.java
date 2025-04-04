package ui;

import javax.swing.*;
import java.awt.*;

import models.BetManager;

public class MainUI extends JFrame {
    
    public MainUI() {
        setTitle("Horse Race Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 600));
        
        JPanel mainContainer = new JPanel(new CardLayout());
        
        BetManager betManager = new BetManager();
        
        MainPanel mainPanel = new MainPanel();
        TransactionPanel transactionPanel = new TransactionPanel();
        NewRacePanel newRacePanel = new NewRacePanel();
        TrackManagementPanel trackManagementPanel = new TrackManagementPanel();
        BettingPanel bettingPanel = new BettingPanel(betManager);
        RaceSimulationPanel raceSimulationPanel = new RaceSimulationPanel(betManager);
        HorseCustomizerPanel horseCustomizerPanel = new HorseCustomizerPanel();
        StatisticsPanel statisticsPanel = new StatisticsPanel();
        
        mainContainer.add(mainPanel, "MAIN");
        mainContainer.add(transactionPanel, "TRANSACTIONS");
        mainContainer.add(newRacePanel, "RACE_SETUP");
        mainContainer.add(trackManagementPanel, "TRACK_MANAGEMENT");
        mainContainer.add(bettingPanel, "BETTING");
        mainContainer.add(raceSimulationPanel, "RACE_SIMULATION");
        mainContainer.add(horseCustomizerPanel, "HORSE_CUSTOMIZER");
        mainContainer.add(statisticsPanel, "STATISTICS");

        add(mainContainer);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        MainUI frame = new MainUI();
        frame.setVisible(true);
    }
}
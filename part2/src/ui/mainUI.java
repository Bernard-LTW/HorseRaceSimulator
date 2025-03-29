package ui;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class MainUI extends JFrame {
    
    public MainUI() {

        List<String> horses = Arrays.asList(
            "Thunderbolt",
            "Silver Wind",
            "Desert Storm",
            "Mountain Spirit",
            "Ocean Breeze"
        );
        
        List<String> tracks = Arrays.asList(
            "Dirt Track",
            "Turf Track",
            "Synthetic Track",
            "Muddy Track"
        );
        
        List<String> weatherConditions = Arrays.asList(
            "Sunny",
            "Rainy",
            "Cloudy",
            "Windy",
            "Stormy"
        );


        setTitle("Horse Race Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        
        // Create card layout for switching between panels
        JPanel mainContainer = new JPanel(new CardLayout());
        
        // Create panels
        MainPanel mainPanel = new MainPanel();
        TransactionPanel transactionPanel = new TransactionPanel();
        // TODO : implement the csv readings replace sample data
        NewRacePanel newRacePanel = new NewRacePanel(horses,tracks,weatherConditions);
        
        // Add panels to card layout
        mainContainer.add(mainPanel, "MAIN");
        mainContainer.add(transactionPanel, "TRANSACTIONS");
        mainContainer.add(newRacePanel, "RACE_SETUP");
        
        // Add container to frame
        add(mainContainer);
        
        // Center the frame on screen
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        // Run the GUI in the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainUI frame = new MainUI();
            frame.setVisible(true);
        });
    }
}
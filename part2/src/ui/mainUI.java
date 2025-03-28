package ui;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {
    
    public MainUI() {
        setTitle("Horse Race Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        
        // Create card layout for switching between panels
        JPanel mainContainer = new JPanel(new CardLayout());
        
        // Create panels
        MainPanel mainPanel = new MainPanel();
        TransactionPanel transactionPanel = new TransactionPanel();
        // TODO: Add other panels as they are created
        
        // Add panels to card layout
        mainContainer.add(mainPanel, "MAIN");
        mainContainer.add(transactionPanel, "TRANSACTIONS");
        
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
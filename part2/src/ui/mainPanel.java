package ui;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    public MainPanel() {
        // Set layout for the panel
        setLayout(new BorderLayout());

        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create menus
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");

        // Add menu items to the File menu
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(exitItem);

        // Add menu items to the Help menu
        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);

        // Add menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        // Add the menu bar to the panel
        add(menuBar, BorderLayout.NORTH);

        // Add a placeholder label in the center
        JLabel placeholder = new JLabel("Welcome to the Horse Race Simulator", SwingConstants.CENTER);
        add(placeholder, BorderLayout.CENTER);
    }
}

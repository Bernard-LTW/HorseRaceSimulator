package ui;

import javax.swing.*;
import java.awt.*;

public class MainUI {

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Horse Race Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Add the main panel to the frame
        frame.setContentPane(new MainPanel());

        // Make the frame visible
        frame.setVisible(true);
    }
}

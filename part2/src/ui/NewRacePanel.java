package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NewRacePanel extends JPanel {
    private JCheckBox[] horseCheckboxes;
    private JComboBox<String> trackSelector;
    private JComboBox<String> weatherSelector;

    public NewRacePanel(List<String> horses, List<String> tracks, List<String> weatherConditions) {
        setLayout(new BorderLayout(10, 20));
        setBackground(new Color(70, 130, 180)); // Match main panel color

        // Style the title panel
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        headerPanel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("Race Setup", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Style the selectors panel
        JPanel selectorsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        selectorsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "Race Conditions",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                new Color(70, 130, 180)));
        selectorsPanel.setBackground(Color.WHITE);

        // Style the labels and selectors
        JLabel trackLabel = new JLabel("Select Track: ");
        trackLabel.setFont(new Font("Arial", Font.BOLD, 14));
        trackSelector = new JComboBox<>(tracks.toArray(new String[0]));
        trackSelector.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel weatherLabel = new JLabel("Weather Condition: ");
        weatherLabel.setFont(new Font("Arial", Font.BOLD, 14));
        weatherSelector = new JComboBox<>(weatherConditions.toArray(new String[0]));
        weatherSelector.setFont(new Font("Arial", Font.PLAIN, 14));

        selectorsPanel.add(trackLabel);
        selectorsPanel.add(trackSelector);
        selectorsPanel.add(weatherLabel);
        selectorsPanel.add(weatherSelector);

        contentPanel.add(selectorsPanel, BorderLayout.NORTH);

        // Style the horses panel
        JPanel horsesPanel = new JPanel();
        horsesPanel.setLayout(new BoxLayout(horsesPanel, BoxLayout.Y_AXIS));
        horsesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "Select Horses",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                new Color(70, 130, 180)));
        horsesPanel.setBackground(Color.WHITE);

        horseCheckboxes = new JCheckBox[horses.size()];

        for (int i = 0; i < horses.size(); i++) {
            horseCheckboxes[i] = new JCheckBox(horses.get(i));
            horseCheckboxes[i].setFont(new Font("Arial", Font.PLAIN, 14));
            horseCheckboxes[i].setBackground(Color.WHITE);
            horsesPanel.add(horseCheckboxes[i]);
            horsesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        JScrollPane scrollPane = new JScrollPane(horsesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Style the buttons panel
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

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 35));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }

    private void startRace() {
        String selectedTrack = (String) trackSelector.getSelectedItem();
        String selectedWeather = (String) weatherSelector.getSelectedItem();
        String[] selectedHorses = getSelectedHorses();

        if (selectedHorses.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one horse!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // TODO: Implement race start logic
        System.out.println("Starting race with:");
        System.out.println("Horses: " + String.join(", ", selectedHorses));
        System.out.println("Track: " + selectedTrack);
        System.out.println("Weather: " + selectedWeather);
    }

    /**
     * Returns an array of selected horse names
     * @return String array containing the names of all selected horses
     */
    public String[] getSelectedHorses() {
        return java.util.Arrays.stream(horseCheckboxes)
                .filter(JCheckBox::isSelected)
                .map(JCheckBox::getText)
                .toArray(String[]::new);
    }

    private void goBack() {
        CardLayout cardLayout = (CardLayout) getParent().getLayout();
        cardLayout.show(getParent(), "MAIN");
    }
}

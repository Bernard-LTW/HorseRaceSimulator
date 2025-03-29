package ui;

import models.Track;
import models.Horse;
import models.Race;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import utils.FileIO;

public class NewRacePanel extends JPanel {
    private JCheckBox[] horseCheckboxes;
    private JComboBox<String> trackSelector;
    private JComboBox<String> weatherSelector;
    private Race currentRace;
    private JPanel horsesPanel;
    private JPanel selectorsPanel;

    public NewRacePanel() {
        setLayout(new BorderLayout(10, 20));
        setBackground(new Color(70, 130, 180));

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

        // Create selectors panel
        selectorsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        selectorsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "Race Conditions",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                new Color(70, 130, 180)));
        selectorsPanel.setBackground(Color.WHITE);

        // Create horses panel
        horsesPanel = new JPanel();
        horsesPanel.setLayout(new BoxLayout(horsesPanel, BoxLayout.Y_AXIS));
        horsesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "Select Horses",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                new Color(70, 130, 180)));
        horsesPanel.setBackground(Color.WHITE);

        // Style the buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBackground(Color.WHITE);

        JButton startRaceButton = createStyledButton("Start Race");
        JButton backButton = createStyledButton("Back");

        startRaceButton.addActionListener(e -> startRace());
        backButton.addActionListener(e -> goBack());

        buttonsPanel.add(startRaceButton);
        buttonsPanel.add(backButton);

        contentPanel.add(selectorsPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(horsesPanel), BorderLayout.CENTER);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);

        // Add component listener to refresh data when panel becomes visible
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshData();
            }
        });

        // Initial data load
        refreshData();
    }

    private void refreshData() {
        // Clear existing components
        selectorsPanel.removeAll();
        horsesPanel.removeAll();

        // Load tracks from CSV
        List<Track> tracks = FileIO.loadTracks();
        String[] trackNames = tracks.stream()
                .map(Track::getName)
                .toArray(String[]::new);

        // Get weather conditions from enum
        String[] weatherConditions = java.util.Arrays.stream(Track.TrackCondition.values())
                .map(Enum::name)
                .toArray(String[]::new);

        // Style the labels and selectors
        JLabel trackLabel = new JLabel("Select Track: ");
        trackLabel.setFont(new Font("Arial", Font.BOLD, 14));
        trackSelector = new JComboBox<>(trackNames);
        trackSelector.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel weatherLabel = new JLabel("Weather Condition: ");
        weatherLabel.setFont(new Font("Arial", Font.BOLD, 14));
        weatherSelector = new JComboBox<>(weatherConditions);
        weatherSelector.setFont(new Font("Arial", Font.PLAIN, 14));

        selectorsPanel.add(trackLabel);
        selectorsPanel.add(trackSelector);
        selectorsPanel.add(weatherLabel);
        selectorsPanel.add(weatherSelector);

        // Load horses from CSV
        Horse[] horses = FileIO.ingestHorses();
        horseCheckboxes = new JCheckBox[horses.length];

        for (int i = 0; i < horses.length; i++) {
            horseCheckboxes[i] = new JCheckBox(horses[i].getName());
            horseCheckboxes[i].setFont(new Font("Arial", Font.PLAIN, 14));
            horseCheckboxes[i].setBackground(Color.WHITE);
            horsesPanel.add(horseCheckboxes[i]);
            horsesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // Revalidate and repaint
        selectorsPanel.revalidate();
        selectorsPanel.repaint();
        horsesPanel.revalidate();
        horsesPanel.repaint();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 35));
        button.setBackground(new Color(41, 128, 185));  // Darker blue for better contrast
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
        button.setOpaque(true);
        button.setContentAreaFilled(true);  // Ensure the button background is filled

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));  // Lighter blue on hover
                button.setBorder(BorderFactory.createLineBorder(new Color(133, 193, 233), 2));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));  // Back to darker blue
                button.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
            }
        });

        return button;
    }

    private void startRace() {
        String selectedTrackName = (String) trackSelector.getSelectedItem();
        String selectedWeather = (String) weatherSelector.getSelectedItem();
        Track.TrackCondition condition = Track.TrackCondition.valueOf(selectedWeather.toUpperCase());

        // Get the selected track from CSV
        List<Track> tracks = FileIO.loadTracks();
        Track selectedTrack = tracks.stream()
                .filter(t -> t.getName().equals(selectedTrackName))
                .findFirst()
                .orElse(null);

        if (selectedTrack == null) {
            JOptionPane.showMessageDialog(this,
                    "Error: Selected track not found",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set the weather condition
        selectedTrack.setCondition(condition);

        String[] selectedHorseNames = getSelectedHorses();
        if (selectedHorseNames.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one horse!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create a new track with the correct number of lanes based on selected horses
        Track raceTrack = new Track(
            selectedTrack.getName(),
            selectedHorseNames.length,
            selectedTrack.getLength(),
            selectedTrack.getShape(),
            selectedTrack.getCondition()
        );

        // Create the race
        currentRace = new Race(raceTrack);
        
        // Load all horses from CSV
        Horse[] allHorses = FileIO.ingestHorses();
        
        // Add selected horses to the race
        int lane = 1;
        for (String horseName : selectedHorseNames) {
            // Find the horse in the loaded horses array
            Horse selectedHorse = null;
            for (Horse horse : allHorses) {
                if (horse.getName().equals(horseName)) {
                    selectedHorse = horse;
                    break;
                }
            }
            
            if (selectedHorse != null) {
                currentRace.addHorse(selectedHorse, lane++);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error: Horse " + horseName + " not found in database",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Get the parent container (CardLayout)
        Container parent = getParent();
        if (parent != null) {
            CardLayout cardLayout = (CardLayout) parent.getLayout();
            
            // Get the BettingPanel and pass the race
            BettingPanel bettingPanel = (BettingPanel) parent.getComponent(4); // Index 4 is BETTING panel
            bettingPanel.setRace(currentRace);
            
            // Switch to betting panel
            cardLayout.show(parent, "BETTING");
        }
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

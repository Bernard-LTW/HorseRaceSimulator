package ui;

import models.Track;
import models.Horse;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import utils.FileIO;

public class NewRacePanel extends JPanel {
    private JCheckBox[] horseCheckboxes;
    private JComboBox<String> trackSelector;
    private JComboBox<String> weatherSelector;

    public NewRacePanel(List<String> horses, List<String> weatherConditions) {
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

        // Load tracks from CSV
        List<Track> tracks = FileIO.loadTracks();
        String[] trackNames = tracks.stream()
                .map(Track::getName)
                .toArray(String[]::new);

        // Style the labels and selectors
        JLabel trackLabel = new JLabel("Select Track: ");
        trackLabel.setFont(new Font("Arial", Font.BOLD, 14));
        trackSelector = new JComboBox<>(trackNames);
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

        String[] selectedHorses = getSelectedHorses();
        if (selectedHorses.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one horse!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create a new track with the correct number of lanes based on selected horses
        Track raceTrack = new Track(
            selectedTrack.getName(),
            selectedHorses.length,
            selectedTrack.getLength(),
            selectedTrack.getShape(),
            selectedTrack.getCondition()
        );

        for (String horseName : selectedHorses) {
            Horse horse = new Horse('H', horseName, 1.0); // Default confidence
            horse.adjustPerformance(raceTrack);
            System.out.println(horse.getName() + " travelled: " + horse.getDistanceTravelled() + " units.");
        }

        System.out.println("Race setup complete with track: " + selectedTrackName);
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

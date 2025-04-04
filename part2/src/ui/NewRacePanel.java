package ui;

import models.Track;
import models.Horse;
import models.Race;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import utils.FileIO;
import static ui.Button.createStyledButton;

public class NewRacePanel extends JPanel {
    private JCheckBox[] horseCheckboxes;
    private JLabel[] confidenceLabels;
    private JComboBox<String> trackSelector;
    private JComboBox<String> weatherSelector;
    private Race currentRace;
    private JPanel horsesPanel;
    private JPanel selectorsPanel;
    private Horse[] horses;

    public NewRacePanel() {
        setLayout(new BorderLayout(10, 20));
        setBackground(new Color(70, 130, 180));

        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        headerPanel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("Race Setup", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        selectorsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        selectorsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "Race Conditions",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                new Color(70, 130, 180)));
        selectorsPanel.setBackground(Color.WHITE);

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

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBackground(Color.WHITE);

        JButton selectAllButton = createStyledButton("Select All Horses");
        JButton startRaceButton = createStyledButton("Start Race");
        JButton backButton = createStyledButton("Back");

        selectAllButton.addActionListener(e -> selectAllHorses());
        startRaceButton.addActionListener(e -> startRace());
        backButton.addActionListener(e -> goBack());

        buttonsPanel.add(selectAllButton);
        buttonsPanel.add(startRaceButton);
        buttonsPanel.add(backButton);

        contentPanel.add(selectorsPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(horsesPanel), BorderLayout.CENTER);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshData();
            }
        });

        refreshData();
    }

    private void refreshData() {
        selectorsPanel.removeAll();
        horsesPanel.removeAll();

        List<Track> tracks = FileIO.loadTracks();
        String[] trackNames = tracks.stream()
                .map(Track::getName)
                .toArray(String[]::new);

        String[] weatherConditions = java.util.Arrays.stream(Track.TrackCondition.values())
                .map(Enum::name)
                .toArray(String[]::new);

        JLabel trackLabel = new JLabel("Select Track: ");
        trackLabel.setFont(new Font("Arial", Font.BOLD, 14));
        trackSelector = new JComboBox<>(trackNames);
        trackSelector.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel weatherLabel = new JLabel("Weather Condition: ");
        weatherLabel.setFont(new Font("Arial", Font.BOLD, 14));
        weatherSelector = new JComboBox<>(weatherConditions);
        weatherSelector.setFont(new Font("Arial", Font.PLAIN, 14));
        weatherSelector.setSelectedItem("DRY");  // Set default to DRY

        selectorsPanel.add(trackLabel);
        selectorsPanel.add(trackSelector);
        selectorsPanel.add(weatherLabel);
        selectorsPanel.add(weatherSelector);

        horses = FileIO.ingestHorses();
        horseCheckboxes = new JCheckBox[horses.length];
        confidenceLabels = new JLabel[horses.length];
        for (int i = 0; i < horses.length; i++) {
            JPanel horsePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            horsePanel.setBackground(Color.WHITE);
            
            horseCheckboxes[i] = new JCheckBox(horses[i].getName());
            horseCheckboxes[i].setFont(new Font("Arial", Font.PLAIN, 14));
            
            confidenceLabels[i] = new JLabel(String.format("Confidence: %.2f", horses[i].getConfidence()));
            confidenceLabels[i].setFont(new Font("Arial", Font.PLAIN, 14));
            confidenceLabels[i].setForeground(new Color(70, 130, 180));
            
            horsePanel.add(horseCheckboxes[i]);
            horsePanel.add(confidenceLabels[i]);
            horsesPanel.add(horsePanel);
            horsesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        weatherSelector.addActionListener(e -> updateHorseConfidences());

        selectorsPanel.revalidate();
        selectorsPanel.repaint();
        horsesPanel.revalidate();
        horsesPanel.repaint();
    }

    private void updateHorseConfidences() {
        if (horses == null || confidenceLabels == null) return;

        String selectedWeather = (String) weatherSelector.getSelectedItem();
        Track.TrackCondition condition = Track.TrackCondition.valueOf(selectedWeather.toUpperCase());
        
        Track tempTrack = new Track("temp", 1, 100, Track.TrackShape.OVAL, condition);
        double speedModifier = tempTrack.getSpeedModifier();
        double fallRiskModifier = tempTrack.getFallRiskModifier();

        for (int i = 0; i < horses.length; i++) {
            double baseConfidence = horses[i].getConfidence();
            double adjustedConfidence = (baseConfidence * speedModifier) - fallRiskModifier;
            adjustedConfidence = Math.max(0.0, Math.min(1.0, adjustedConfidence));
            confidenceLabels[i].setText(String.format("Confidence: %.2f", adjustedConfidence));
        }
    }

    private void startRace() {
        String selectedTrackName = (String) trackSelector.getSelectedItem();
        String selectedWeather = (String) weatherSelector.getSelectedItem();
        Track.TrackCondition condition = Track.TrackCondition.valueOf(selectedWeather.toUpperCase());
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

        selectedTrack.setCondition(condition);

        String[] selectedHorseNames = getSelectedHorses();
        if (selectedHorseNames.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one horse!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Track raceTrack = new Track(
            selectedTrack.getName(),
            selectedHorseNames.length,
            selectedTrack.getLength(),
            selectedTrack.getShape(),
            selectedTrack.getCondition()
        );
        currentRace = new Race(raceTrack);
        Horse[] allHorses = FileIO.ingestHorses();
        
        int lane = 1;
        for (String horseName : selectedHorseNames) {
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

        Container parent = getParent();
        if (parent != null) {
            CardLayout cardLayout = (CardLayout) parent.getLayout();
            
            BettingPanel bettingPanel = (BettingPanel) parent.getComponent(4);
            bettingPanel.setRace(currentRace);
            cardLayout.show(parent, "BETTING");
        }
    }

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

    private void selectAllHorses() {
        if (horseCheckboxes != null) {
            for (JCheckBox checkbox : horseCheckboxes) {
                checkbox.setSelected(true);
            }
        }
    }
}

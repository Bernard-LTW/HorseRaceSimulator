package ui;

import models.Track;
import utils.FileIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class TrackManagementPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable trackTable;
    private List<Track> tracks;
    
    public TrackManagementPanel() {
        setLayout(new BorderLayout(10, 20));
        setBackground(new Color(70, 130, 180));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        headerPanel.setBackground(new Color(70, 130, 180));
        
        JLabel titleLabel = new JLabel("Track Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        // Create table
        String[] columns = {"Name", "Length", "Shape"};
        tableModel = new DefaultTableModel(columns, 0);
        trackTable = new JTable(tableModel);
        trackTable.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(trackTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Template Buttons Panel
        JPanel templatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        templatePanel.setBackground(Color.WHITE);
        templatePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Quick Add Templates",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)));

        JButton shortTrackBtn = createStyledButton("Short Track (100m)");
        JButton mediumTrackBtn = createStyledButton("Medium Track (400m)");
        JButton longTrackBtn = createStyledButton("Long Track (1000m)");
        JButton figureEightBtn = createStyledButton("Figure 8 Track (600m)");

        shortTrackBtn.addActionListener(e -> addTemplateTrack("Short Track", 100, Track.TrackShape.OVAL));
        mediumTrackBtn.addActionListener(e -> addTemplateTrack("Medium Track", 400, Track.TrackShape.OVAL));
        longTrackBtn.addActionListener(e -> addTemplateTrack("Long Track", 1000, Track.TrackShape.OVAL));
        figureEightBtn.addActionListener(e -> addTemplateTrack("Figure 8", 600, Track.TrackShape.FIGURE_EIGHT));

        templatePanel.add(shortTrackBtn);
        templatePanel.add(mediumTrackBtn);
        templatePanel.add(longTrackBtn);
        templatePanel.add(figureEightBtn);

        contentPanel.add(templatePanel, BorderLayout.NORTH);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton addButton = createStyledButton("Add Track");
        JButton deleteButton = createStyledButton("Delete Selected");
        JButton backButton = createStyledButton("Back");
        
        addButton.addActionListener(e -> showAddTrackDialog());
        deleteButton.addActionListener(e -> deleteSelectedTrack());
        backButton.addActionListener(e -> goBack());
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(backButton);
        
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // Load existing tracks
        loadTracks();
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
    
    private void loadTracks() {
        tracks = FileIO.loadTracks();
        updateTable();
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        for (Track track : tracks) {
            tableModel.addRow(new Object[]{
                track.getName(),
                track.getLength(),
                track.getShape()
            });
        }
    }
    
    private void showAddTrackDialog() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Add New Track", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setBackground(Color.WHITE);
        
        JTextField nameField = new JTextField();
        JSpinner lengthSpinner = new JSpinner(new SpinnerNumberModel(100, 50, 1000, 50));
        JComboBox<Track.TrackShape> shapeBox = new JComboBox<>(Track.TrackShape.values());
        
        dialog.add(new JLabel("Track Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Track Length:"));
        dialog.add(lengthSpinner);
        dialog.add(new JLabel("Track Shape:"));
        dialog.add(shapeBox);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a track name", 
                    "Input Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Track newTrack = new Track(
                nameField.getText().trim(),
                3,  // Default lane count
                (int)lengthSpinner.getValue(),
                (Track.TrackShape)shapeBox.getSelectedItem(),
                Track.TrackCondition.DRY  // Default condition
            );
            tracks.add(newTrack);
            FileIO.saveTracks(tracks);
            updateTable();
            dialog.dispose();
        });
        
        dialog.add(saveButton);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteSelectedTrack() {
        int selectedRow = trackTable.getSelectedRow();
        if (selectedRow >= 0) {
            tracks.remove(selectedRow);
            FileIO.saveTracks(tracks);
            updateTable();
        }
    }
    
    private void goBack() {
        CardLayout cardLayout = (CardLayout) getParent().getLayout();
        cardLayout.show(getParent(), "MAIN");
    }

    private void addTemplateTrack(String name, int length, Track.TrackShape shape) {
        Track newTrack = new Track(name, 3, length, shape, Track.TrackCondition.DRY);  // Default values
        tracks.add(newTrack);
        FileIO.saveTracks(tracks);
        updateTable();
    }
} 
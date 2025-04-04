package ui;

import models.Track;
import utils.FileIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import static ui.Button.createStyledButton;

public class TrackManagementPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable trackTable;
    private List<Track> tracks;
    
    public TrackManagementPanel() {
        setLayout(new BorderLayout(10, 20));
        setBackground(new Color(70, 130, 180));
        
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        headerPanel.setBackground(new Color(70, 130, 180));
        
        JLabel titleLabel = new JLabel("Track Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        String[] columns = {"Name", "Length", "Shape"};
        tableModel = new DefaultTableModel(columns, 0);
        trackTable = new JTable(tableModel);
        trackTable.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(trackTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
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
        
        loadTracks();
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
                3,
                (int)lengthSpinner.getValue(),
                (Track.TrackShape)shapeBox.getSelectedItem(),
                Track.TrackCondition.DRY
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
} 
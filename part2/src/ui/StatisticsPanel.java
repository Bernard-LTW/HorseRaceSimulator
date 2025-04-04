package ui;

import models.*;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import utils.HorseComparator;

import static ui.Button.createStyledButton;

public class StatisticsPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private JPanel horseStatsPanel;
    private JPanel trackRecordsPanel;
    private JPanel bettingStatsPanel;
    private JComboBox<String> horseSelector;
    private JButton compareHorsesButton;
    
    public StatisticsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(70, 130, 180));
        
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        headerPanel.setBackground(new Color(70, 130, 180));
        
        JLabel titleLabel = new JLabel("Race Statistics", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        horseStatsPanel = createHorseStatsPanel();
        trackRecordsPanel = createTrackRecordsPanel();
        bettingStatsPanel = createBettingStatsPanel();
        
        tabbedPane.addTab("Horse Statistics", horseStatsPanel);
        tabbedPane.addTab("Track Records", trackRecordsPanel);
        tabbedPane.addTab("Betting Analytics", bettingStatsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        
        compareHorsesButton = createStyledButton("Compare Horses");
        compareHorsesButton.addActionListener(e -> showHorseComparisonDialog());
        
        JButton backButton = createStyledButton("Back");
        backButton.addActionListener(e -> goBack());
        
        buttonPanel.add(compareHorsesButton);
        buttonPanel.add(backButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        refreshData();
    }
    
    private JPanel createHorseStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.setBackground(Color.WHITE);
        
        JLabel horseLabel = new JLabel("Select Horse: ");
        horseLabel.setFont(new Font("Arial", Font.BOLD, 14));
        horseSelector = new JComboBox<>();
        horseSelector.setFont(new Font("Arial", Font.PLAIN, 14));
        horseSelector.addActionListener(e -> updateHorseStats());
        
        selectorPanel.add(horseLabel);
        selectorPanel.add(horseSelector);
        
        JPanel statsPanel = new JPanel(new GridLayout(0, 2, 20, 10));
        statsPanel.setBackground(Color.WHITE);
        
        String[] statLabels = {
            "Total Races:", "Wins:", "Win Rate:", "Average Confidence:",
            "Average Speed:", "Best Speed:", "Worst Speed:"
        };
        
        for (String label : statLabels) {
            JLabel statLabel = new JLabel(label);
            statLabel.setFont(new Font("Arial", Font.BOLD, 14));
            statsPanel.add(statLabel);
            statsPanel.add(new JLabel("--"));
        }
        
        panel.add(selectorPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(statsPanel), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTrackRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] columns = {"Track Name", "Best Time (ms)", "Best Horse"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable recordsTable = new JTable(model);
        recordsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        recordsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        recordsTable.setRowHeight(25);
        recordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        recordsTable.getColumnModel().getColumn(0).setPreferredWidth(150);  // Track Name
        recordsTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Best Time
        recordsTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Best Horse
        
        JScrollPane scrollPane = new JScrollPane(recordsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBettingStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] statLabels = {
            "Total Bets:", "Winning Bets:", "Win Rate:", "Total Bet Amount:",
            "Total Winnings:", "Profit/Loss:", "ROI:"
        };
        
        for (String label : statLabels) {
            JLabel statLabel = new JLabel(label);
            statLabel.setFont(new Font("Arial", Font.BOLD, 14));
            panel.add(statLabel);
            panel.add(new JLabel("--"));
        }
        
        return panel;
    }
    
    private void refreshData() {
        Horse[] horses = utils.FileIO.ingestHorses();
        horseSelector.removeAllItems();
        for (Horse horse : horses) {
            horseSelector.addItem(horse.getName());
        }
        
        updateHorseStats();
        updateTrackRecords();
        updateBettingStats();
    }
    
    private void updateHorseStats() {
        String selectedHorse = (String) horseSelector.getSelectedItem();
        if (selectedHorse == null) return;
        
        Map<String, Object> stats = RaceStatistics.getHorseStats(selectedHorse);
        
        JPanel statsPanel = (JPanel) ((JScrollPane) horseStatsPanel.getComponent(1)).getViewport().getView();
        Component[] components = statsPanel.getComponents();
        
        for (int i = 0; i < components.length; i += 2) {
            JLabel valueLabel = (JLabel) components[i + 1];
            String label = ((JLabel) components[i]).getText();
            
            switch (label) {
                case "Total Races:":
                    valueLabel.setText(String.valueOf(stats.getOrDefault("totalRaces", 0)));
                    break;
                case "Wins:":
                    valueLabel.setText(String.valueOf(stats.getOrDefault("wins", 0)));
                    break;
                case "Win Rate:":
                    valueLabel.setText(String.format("%.1f%%", stats.getOrDefault("winRate", 0.0)));
                    break;
                case "Average Confidence:":
                    valueLabel.setText(String.format("%.2f", stats.getOrDefault("avgConfidence", 0.0)));
                    break;
                case "Average Speed:":
                    valueLabel.setText(String.format("%.2f units/s", stats.getOrDefault("avgSpeed", 0.0)));
                    break;
                case "Best Speed:":
                    valueLabel.setText(String.format("%.2f units/s", stats.getOrDefault("bestSpeed", 0.0)));
                    break;
                case "Worst Speed:":
                    valueLabel.setText(String.format("%.2f units/s", stats.getOrDefault("worstSpeed", 0.0)));
                    break;
            }
        }
    }
    
    private void updateTrackRecords() {
        JTable recordsTable = (JTable) ((JScrollPane) trackRecordsPanel.getComponent(0)).getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) recordsTable.getModel();
        
        model.setRowCount(0);
        
        List<Track> tracks = utils.FileIO.loadTracks();
        for (Track track : tracks) {
            model.addRow(new Object[]{
                track.getName(),
                String.format("%d", (long)track.getBestTime()),
                track.getBestHorse().isEmpty() ? "--" : track.getBestHorse()
            });
        }
    }
    
    private void updateBettingStats() {
        Map<String, Object> stats = RaceStatistics.getBettingStats();
        
        Component[] components = bettingStatsPanel.getComponents();
        
        for (int i = 0; i < components.length; i += 2) {
            JLabel valueLabel = (JLabel) components[i + 1];
            String label = ((JLabel) components[i]).getText();
            
            switch (label) {
                case "Total Bets:":
                    valueLabel.setText(String.valueOf(stats.getOrDefault("totalBets", 0)));
                    break;
                case "Winning Bets:":
                    valueLabel.setText(String.valueOf(stats.getOrDefault("winningBets", 0)));
                    break;
                case "Win Rate:":
                    valueLabel.setText(String.format("%.1f%%", stats.getOrDefault("winRate", 0.0)));
                    break;
                case "Total Bet Amount:":
                    valueLabel.setText(String.format("$%.2f", stats.getOrDefault("totalBetAmount", 0.0)));
                    break;
                case "Total Winnings:":
                    valueLabel.setText(String.format("$%.2f", stats.getOrDefault("totalWinnings", 0.0)));
                    break;
                case "Profit/Loss:":
                    valueLabel.setText(String.format("$%.2f", stats.getOrDefault("profitLoss", 0.0)));
                    break;
                case "ROI:":
                    valueLabel.setText(String.format("%.1f%%", stats.getOrDefault("roi", 0.0)));
                    break;
            }
        }
    }
    
    private void showHorseComparisonDialog() {
        Horse[] horses = utils.FileIO.ingestHorses();
        if (horses.length < 2) {
            JOptionPane.showMessageDialog(this,
                "There must be at least two horses to compare",
                "Not Enough Horses",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Compare Horses", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setBackground(Color.WHITE);
        dialog.setSize(500, 300);
        
        JPanel selectorsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        selectorsPanel.setBackground(Color.WHITE);
        selectorsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        DefaultComboBoxModel<String> horse1Model = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> horse2Model = new DefaultComboBoxModel<>();
        
        JComboBox<String> horse1Selector = new JComboBox<>(horse1Model);
        JComboBox<String> horse2Selector = new JComboBox<>(horse2Model);
        
        for (Horse horse : horses) {
            horse1Model.addElement(horse.getName());
        }
        
        updateHorse2Selector(horse2Model, horses, (String) horse1Selector.getSelectedItem());
        
        horse1Selector.addActionListener(e -> {
            String selected = (String) horse1Selector.getSelectedItem();
            updateHorse2Selector(horse2Model, horses, selected);
        });
        
        selectorsPanel.add(new JLabel("First Horse:"));
        selectorsPanel.add(horse1Selector);
        selectorsPanel.add(new JLabel("Second Horse:"));
        selectorsPanel.add(horse2Selector);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        JButton compareBtn = createStyledButton("Compare");
        JButton cancelBtn = createStyledButton("Cancel");
        
        compareBtn.addActionListener(e -> {
            String horse1Name = (String) horse1Selector.getSelectedItem();
            String horse2Name = (String) horse2Selector.getSelectedItem();
            
            Horse horse1 = null;
            Horse horse2 = null;
            
            for (Horse horse : horses) {
                if (horse.getName().equals(horse1Name)) horse1 = horse;
                if (horse.getName().equals(horse2Name)) horse2 = horse;
            }
            
            if (horse1 != null && horse2 != null) {
                Map<String, Object> comparison = HorseComparator.compareHorses(horse1, horse2);
                showComparisonResults(comparison);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(compareBtn);
        buttonPanel.add(cancelBtn);
        
        dialog.add(selectorsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void updateHorse2Selector(DefaultComboBoxModel<String> model, Horse[] horses, String excludeHorse) {
        model.removeAllElements();
        for (Horse horse : horses) {
            if (!horse.getName().equals(excludeHorse)) {
                model.addElement(horse.getName());
            }
        }
        if (model.getSize() > 0) {
            model.setSelectedItem(model.getElementAt(0));
        }
    }
    
    private void showComparisonResults(Map<String, Object> comparison) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Horse Comparison Results", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setBackground(Color.WHITE);
        dialog.setSize(600, 400);
        
        JTextArea resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultsArea.setBackground(Color.WHITE);
        resultsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        resultsArea.setText(HorseComparator.formatComparison(comparison));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        JButton closeBtn = createStyledButton("Close");
        buttonPanel.add(closeBtn);
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void goBack() {
        CardLayout cardLayout = (CardLayout) getParent().getLayout();
        cardLayout.show(getParent(), "MAIN");
    }
} 
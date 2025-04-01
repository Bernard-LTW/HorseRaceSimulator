package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import models.Race;
import models.Horse;
import models.Bet;
import models.Transaction;
import core.BetManager;
import utils.FileIO;
import utils.BettingOdds;

public class BettingPanel extends JPanel {
    private Race currentRace;
    private BetManager betManager;
    private JLabel balanceLabel;
    private double currentBalance;
    private JTable selectedHorsesTable;
    private JTable currentBetsTable;
    private DefaultTableModel selectedHorsesModel;
    private DefaultTableModel currentBetsModel;
    private JTextField betAmountField;
    private JButton addBetButton;
    private JButton removeBetButton;
    private JButton startRaceButton;
    private JButton backButton;
    
    public BettingPanel(BetManager betManager) {
        setLayout(new BorderLayout());
        this.betManager = betManager;
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Place Your Bets");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(balanceLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main content panel with three columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Left panel - Selected Horses
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Selected Horses"));
        
        // Create table model for selected horses
        String[] selectedColumns = {"Horse", "Symbol", "Odds", "Bet Amount"};
        selectedHorsesModel = new DefaultTableModel(selectedColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only bet amount column is editable
            }
        };
        selectedHorsesTable = new JTable(selectedHorsesModel);
        selectedHorsesTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        selectedHorsesTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        selectedHorsesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        selectedHorsesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        leftPanel.add(new JScrollPane(selectedHorsesTable), BorderLayout.CENTER);
        
        // Middle panel - Bet Controls
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setBorder(BorderFactory.createTitledBorder("Bet Controls"));
        
        betAmountField = new JTextField(10);
        betAmountField.setMaximumSize(new Dimension(150, 30));
        JLabel betAmountLabel = new JLabel("Bet Amount:");
        
        addBetButton = new JButton("Add Bet →");
        removeBetButton = new JButton("← Remove Bet");
        
        middlePanel.add(Box.createVerticalStrut(20));
        middlePanel.add(betAmountLabel);
        middlePanel.add(betAmountField);
        middlePanel.add(Box.createVerticalStrut(10));
        middlePanel.add(addBetButton);
        middlePanel.add(Box.createVerticalStrut(10));
        middlePanel.add(removeBetButton);
        middlePanel.add(Box.createVerticalGlue());
        
        // Right panel - Current Bets
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Current Bets"));
        
        // Create table model for current bets
        String[] betColumns = {"Horse", "Symbol", "Odds", "Bet Amount", "Potential Win"};
        currentBetsModel = new DefaultTableModel(betColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No cells are editable
            }
        };
        currentBetsTable = new JTable(currentBetsModel);
        currentBetsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        currentBetsTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        currentBetsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        currentBetsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        currentBetsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        rightPanel.add(new JScrollPane(currentBetsTable), BorderLayout.CENTER);
        
        contentPanel.add(leftPanel);
        contentPanel.add(middlePanel);
        contentPanel.add(rightPanel);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        startRaceButton = new JButton("Start Race");
        backButton = new JButton("Back");
        
        buttonPanel.add(backButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(startRaceButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        addBetButton.addActionListener(e -> addSelectedBets());
        removeBetButton.addActionListener(e -> removeSelectedBets());
        startRaceButton.addActionListener(e -> startRace());
        backButton.addActionListener(e -> goBack());
        
        // Initialize balance
        updateBalanceFromTransactions();
    }
    
    public void setRace(Race race) {
        this.currentRace = race;
        selectedHorsesModel.setRowCount(0);
        currentBetsModel.setRowCount(0);
        
        // Add horses to selected horses table with odds
        for (Horse horse : race.getLanes()) {
            if (horse != null) {
                double odds = BettingOdds.calculateOdds(horse, race.getTrack());
                selectedHorsesModel.addRow(new Object[]{
                    horse.getName(),
                    String.valueOf(horse.getSymbol()),
                    BettingOdds.formatOdds(odds),
                    ""
                });
            }
        }
    }
    
    private void addSelectedBets() {
        String betAmountStr = betAmountField.getText().trim();
        if (betAmountStr.isEmpty()) {
            showError("Please enter a bet amount");
            return;
        }
        
        try {
            double betAmount = Double.parseDouble(betAmountStr);
            if (betAmount <= 0) {
                showError("Bet amount must be greater than 0");
                return;
            }
            
            if (betAmount > currentBalance) {
                showError("Insufficient funds");
                return;
            }
            
            // Get selected rows
            int[] selectedRows = selectedHorsesTable.getSelectedRows();
            if (selectedRows.length == 0) {
                showError("Please select at least one horse");
                return;
            }
            
            // Move selected horses to current bets
            for (int row : selectedRows) {
                String horseName = (String) selectedHorsesModel.getValueAt(row, 0);
                String symbol = (String) selectedHorsesModel.getValueAt(row, 1);
                String oddsStr = (String) selectedHorsesModel.getValueAt(row, 2);
                
                // Find the horse object
                Horse horse = null;
                for (Horse h : currentRace.getLanes()) {
                    if (h.getName().equals(horseName) && String.valueOf(h.getSymbol()).equals(symbol)) {
                        horse = h;
                        break;
                    }
                }
                
                if (horse != null) {
                    // Calculate odds and potential winnings
                    double odds = BettingOdds.calculateOdds(horse, currentRace.getTrack());
                    double potentialWin = BettingOdds.calculatePotentialWinnings(betAmount, odds);
                    
                    // Add to current bets
                    currentBetsModel.addRow(new Object[]{
                        horseName,
                        symbol,
                        BettingOdds.formatOdds(odds),
                        String.format("$%.2f", betAmount),
                        String.format("$%.2f", potentialWin)
                    });
                    
                    // Remove from selected horses
                    selectedHorsesModel.removeRow(row);
                }
            }
            
        } catch (NumberFormatException e) {
            showError("Invalid bet amount");
        }
    }
    
    private void removeSelectedBets() {
        // Get selected rows
        int[] selectedRows = currentBetsTable.getSelectedRows();
        if (selectedRows.length == 0) {
            showError("Please select at least one bet to remove");
            return;
        }
        
        // Move selected bets back to selected horses
        for (int row : selectedRows) {
            String horseName = (String) currentBetsModel.getValueAt(row, 0);
            String symbol = (String) currentBetsModel.getValueAt(row, 1);
            String oddsStr = (String) currentBetsModel.getValueAt(row, 2);
            
            // Find the horse object
            Horse horse = null;
            for (Horse h : currentRace.getLanes()) {
                if (h.getName().equals(horseName) && String.valueOf(h.getSymbol()).equals(symbol)) {
                    horse = h;
                    break;
                }
            }
            
            if (horse != null) {
                // Add back to selected horses
                selectedHorsesModel.addRow(new Object[]{
                    horseName,
                    symbol,
                    oddsStr,
                    ""
                });
                
                // Remove from current bets
                currentBetsModel.removeRow(row);
            }
        }
    }
    
    private void startRace() {
        if (currentBetsModel.getRowCount() == 0) {
            showError("Please place at least one bet before starting the race");
            return;
        }
        
        // Calculate total bet amount
        double totalBetAmount = 0.0;
        for (int row = 0; row < currentBetsModel.getRowCount(); row++) {
            String amountStr = ((String) currentBetsModel.getValueAt(row, 2))
                .replace("$", "")  // Remove dollar sign
                .replace(",", ""); // Remove commas
            try {
                totalBetAmount += Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                showError("Invalid bet amount");
                return;
            }
        }
        
        // Update balance immediately
        currentBalance -= totalBetAmount;
        updateBalanceLabel();
        
        // Place all current bets
        for (int row = 0; row < currentBetsModel.getRowCount(); row++) {
            String horseName = (String) currentBetsModel.getValueAt(row, 0);
            String symbol = (String) currentBetsModel.getValueAt(row, 1);
            String amountStr = ((String) currentBetsModel.getValueAt(row, 2))
                .replace("$", "")  // Remove dollar sign
                .replace(",", ""); // Remove commas
            
            // Find the horse object
            Horse horse = null;
            for (Horse h : currentRace.getLanes()) {
                if (h.getName().equals(horseName) && String.valueOf(h.getSymbol()).equals(symbol)) {
                    horse = h;
                    break;
                }
            }
            
            if (horse != null) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (amount > 0) {
                        if (!betManager.placeBet(currentRace, horse, amount)) {
                            showError("Failed to place bet on " + horse.getName());
                            return;
                        }
                    }
                } catch (NumberFormatException e) {
                    showError("Invalid bet amount for " + horse.getName());
                    return;
                }
            }
        }
        
        // Switch to race simulation panel
        CardLayout cardLayout = (CardLayout) getParent().getLayout();
        RaceSimulationPanel raceSimPanel = (RaceSimulationPanel) getParent().getComponent(5); // Index of RACE_SIMULATION panel
        raceSimPanel.setRace(currentRace);
        raceSimPanel.startRace(currentRace);
        cardLayout.show(getParent(), "RACE_SIMULATION");
    }
    
    private void goBack() {
        CardLayout cardLayout = (CardLayout) getParent().getLayout();
        cardLayout.show(getParent(), "RACE_SETUP");
    }
    
    private void updateBalanceFromTransactions() {
        List<Transaction> transactions = FileIO.loadTransactions();
        if (transactions != null) {
            currentBalance = transactions.stream()
                .mapToDouble(t -> t.getType() == Transaction.TransactionType.DEPOSIT ? t.getAmount() : -t.getAmount())
                .sum();
            updateBalanceLabel();
        }
    }
    
    private void updateBalanceLabel() {
        balanceLabel.setText(String.format("Balance: $%.2f", currentBalance));
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
} 
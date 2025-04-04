package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


import models.Race;
import models.Horse;
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
        
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Selected Horses"));
        
        String[] selectedColumns = {"Horse", "Symbol", "Odds", "Bet Amount"};
        selectedHorsesModel = new DefaultTableModel(selectedColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        selectedHorsesTable = new JTable(selectedHorsesModel);
        selectedHorsesTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        selectedHorsesTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        selectedHorsesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        selectedHorsesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        leftPanel.add(new JScrollPane(selectedHorsesTable), BorderLayout.CENTER);
        
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
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Current Bets"));
        
        String[] betColumns = {"Horse", "Symbol", "Odds", "Bet Amount", "Potential Win"};
        currentBetsModel = new DefaultTableModel(betColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        startRaceButton = new JButton("Start Race");
        backButton = new JButton("Back");
        
        buttonPanel.add(backButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(startRaceButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        addBetButton.addActionListener(e -> addSelectedBets());
        removeBetButton.addActionListener(e -> removeSelectedBets());
        startRaceButton.addActionListener(e -> startRace());
        backButton.addActionListener(e -> goBack());
        
        updateBalanceFromTransactions();
    }
    
    public void setRace(Race race) {
        this.currentRace = race;
        selectedHorsesModel.setRowCount(0);
        currentBetsModel.setRowCount(0);
        
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
            
            int[] selectedRows = selectedHorsesTable.getSelectedRows();
            if (selectedRows.length == 0) {
                showError("Please select at least one horse");
                return;
            }
            
            for (int row : selectedRows) {
                String horseName = (String) selectedHorsesModel.getValueAt(row, 0);
                String symbol = (String) selectedHorsesModel.getValueAt(row, 1);

                Horse horse = null;
                for (Horse h : currentRace.getLanes()) {
                    if (h.getName().equals(horseName) && String.valueOf(h.getSymbol()).equals(symbol)) {
                        horse = h;
                        break;
                    }
                }
                
                if (horse != null) {
                    double odds = BettingOdds.calculateOdds(horse, currentRace.getTrack());
                    double potentialWin = BettingOdds.calculatePotentialWinnings(betAmount, odds);
                    
                    currentBetsModel.addRow(new Object[]{
                        horseName,
                        symbol,
                        BettingOdds.formatOdds(odds),
                        String.format("$%.2f", betAmount),
                        String.format("$%.2f", potentialWin)
                    });
                    
                    selectedHorsesModel.removeRow(row);
                }
            }
            
        } catch (NumberFormatException e) {
            showError("Invalid bet amount");
        }
    }
    
    private void removeSelectedBets() {
        int[] selectedRows = currentBetsTable.getSelectedRows();
        if (selectedRows.length == 0) {
            showError("Please select at least one bet to remove");
            return;
        }
        
        for (int row : selectedRows) {
            String horseName = (String) currentBetsModel.getValueAt(row, 0);
            String symbol = (String) currentBetsModel.getValueAt(row, 1);
            String oddsStr = (String) currentBetsModel.getValueAt(row, 2);
            
            Horse horse = null;
            for (Horse h : currentRace.getLanes()) {
                if (h.getName().equals(horseName) && String.valueOf(h.getSymbol()).equals(symbol)) {
                    horse = h;
                    break;
                }
            }
            
            if (horse != null) {
                selectedHorsesModel.addRow(new Object[]{
                    horseName,
                    symbol,
                    oddsStr,
                    ""
                });
                
                currentBetsModel.removeRow(row);
            }
        }
    }
    
    private void startRace() {
        double totalBetAmount = 0.0;
        for (int row = 0; row < currentBetsModel.getRowCount(); row++) {
            String amountStr = ((String) currentBetsModel.getValueAt(row, 3))
                .replace("$", "")
                .replace(",", "");
            try {
                totalBetAmount += Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                showError("Invalid bet amount");
                return;
            }
        }
        
        if (totalBetAmount > 0) {
            currentBalance -= totalBetAmount;
            updateBalanceLabel();
        }
        
        for (int row = 0; row < currentBetsModel.getRowCount(); row++) {
            String horseName = (String) currentBetsModel.getValueAt(row, 0);
            String symbol = (String) currentBetsModel.getValueAt(row, 1);
            String amountStr = ((String) currentBetsModel.getValueAt(row, 3))
                .replace("$", "")
                .replace(",", "");
            
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
        
        CardLayout cardLayout = (CardLayout) getParent().getLayout();
        RaceSimulationPanel raceSimPanel = (RaceSimulationPanel) getParent().getComponent(5);
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
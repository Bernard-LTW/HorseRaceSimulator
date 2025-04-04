package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

import models.Race;
import models.Horse;
import models.Transaction;
import models.BetManager;
import utils.FileIO;
import utils.BettingOdds;

public class BettingPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 250);
    private static final Color HEADER_COLOR = new Color(60, 60, 70);
    private static final Color ACCENT_COLOR = new Color(100, 149, 237);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 28);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final int PADDING = 20;

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
        this.betManager = betManager;
        setLayout(new BorderLayout(PADDING, PADDING));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        setupActionListeners();
        
        updateBalanceFromTransactions();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(PADDING, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, PADDING, 0));
        
        JLabel titleLabel = new JLabel("Place Your Bets");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(HEADER_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setFont(HEADER_FONT);
        balanceLabel.setForeground(HEADER_COLOR);
        headerPanel.add(balanceLabel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, PADDING, 0));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel leftPanel = createTablePanel("Selected Horses", selectedHorsesModel = createSelectedHorsesModel());
        selectedHorsesTable = new JTable(selectedHorsesModel);
        styleTable(selectedHorsesTable);
        leftPanel.add(new JScrollPane(selectedHorsesTable), BorderLayout.CENTER);
        
        JPanel middlePanel = createBetControlsPanel();
        
        JPanel rightPanel = createTablePanel("Current Bets", currentBetsModel = createCurrentBetsModel());
        currentBetsTable = new JTable(currentBetsModel);
        styleTable(currentBetsTable);
        rightPanel.add(new JScrollPane(currentBetsTable), BorderLayout.CENTER);
        
        contentPanel.add(leftPanel);
        contentPanel.add(middlePanel);
        contentPanel.add(rightPanel);
        
        return contentPanel;
    }
    
    private JPanel createTablePanel(String title, DefaultTableModel model) {
        JPanel panel = new JPanel(new BorderLayout(0, PADDING));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(PADDING, PADDING, PADDING, PADDING)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(HEADER_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createBetControlsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(PADDING, PADDING, PADDING, PADDING)
        ));
        
        JLabel titleLabel = new JLabel("Bet Controls");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(HEADER_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        
        panel.add(Box.createVerticalStrut(PADDING));
        
        JLabel betAmountLabel = new JLabel("Bet Amount:");
        betAmountLabel.setFont(REGULAR_FONT);
        betAmountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(betAmountLabel);
        
        panel.add(Box.createVerticalStrut(5));
        
        betAmountField = new JTextField(10);
        betAmountField.setMaximumSize(new Dimension(150, 30));
        betAmountField.setFont(REGULAR_FONT);
        betAmountField.setHorizontalAlignment(JTextField.CENTER);
        panel.add(betAmountField);
        
        panel.add(Box.createVerticalStrut(PADDING));
        
        addBetButton = Button.createStyledButton("Add Bet →");
        removeBetButton = Button.createStyledButton("← Remove Bet");
        
        panel.add(addBetButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(removeBetButton);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, PADDING, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(PADDING, 0, 0, 0));
        
        backButton = Button.createStyledButton("Back");
        startRaceButton = Button.createStyledButton("Start Race");
        
        buttonPanel.add(backButton);
        buttonPanel.add(startRaceButton);
        
        return buttonPanel;
    }
    
    private void styleTable(JTable table) {
        table.setFont(REGULAR_FONT);
        table.setRowHeight(25);
        table.getTableHeader().setFont(REGULAR_FONT);
        table.getTableHeader().setBackground(ACCENT_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(230, 230, 250));
        table.setSelectionForeground(HEADER_COLOR);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
    }
    
    private DefaultTableModel createSelectedHorsesModel() {
        String[] columns = {"Horse", "Symbol", "Odds", "Bet Amount"};
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
    }
    
    private DefaultTableModel createCurrentBetsModel() {
        String[] columns = {"Horse", "Symbol", "Odds", "Bet Amount", "Potential Win"};
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
    
    private void setupActionListeners() {
        addBetButton.addActionListener(e -> addSelectedBets());
        removeBetButton.addActionListener(e -> removeSelectedBets());
        startRaceButton.addActionListener(e -> startRace());
        backButton.addActionListener(e -> goBack());
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
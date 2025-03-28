package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import models.Transaction;
import utils.FileIO;

/**
 * Panel for displaying transaction history and making deposits
 */
public class TransactionPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable transactionTable;
    private double currentBalance = 0.0;
    private JLabel balanceLabel;
    private List<Transaction> transactions = new ArrayList<>();
    
    public TransactionPanel() {
        setLayout(new BorderLayout());
        
        // Create header panel with title and current balance
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Transaction History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        balanceLabel = new JLabel("Current Balance: $0.00", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(balanceLabel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Create table to display transactions
        String[] columns = {"Date", "Time", "Type", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        transactionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create deposit panel
        JPanel depositPanel = new JPanel(new FlowLayout());
        JLabel depositLabel = new JLabel("Deposit Amount: $");
        JTextField depositField = new JTextField(10);
        JButton depositButton = new JButton("Make Deposit");
        
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double amount = Double.parseDouble(depositField.getText());
                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(TransactionPanel.this, 
                                "Please enter a positive amount", 
                                "Invalid Input", 
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Add new transaction and update file
                    addNewTransaction(amount);
                    depositField.setText("");
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(TransactionPanel.this, 
                            "Please enter a valid number", 
                            "Invalid Input", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        depositPanel.add(depositLabel);
        depositPanel.add(depositField);
        depositPanel.add(depositButton);
        
        add(depositPanel, BorderLayout.SOUTH);
        
        // Load transaction data
        loadTransactions();
    }
    
    /**
     * Load transactions from CSV file
     */
    private void loadTransactions() {
        tableModel.setRowCount(0);
        currentBalance = 0.0;
        
        List<Transaction> loadedTransactions = FileIO.loadTransactions();
        if (loadedTransactions == null) {
            JOptionPane.showMessageDialog(this,
                "Error loading transaction data",
                "File Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        transactions = loadedTransactions;
        for (Transaction transaction : transactions) {
            tableModel.addRow(transaction.toTableRow());
            if (transaction.getType() == Transaction.TransactionType.DEPOSIT) {
                currentBalance += transaction.getAmount();
            } else {
                currentBalance -= transaction.getAmount();
            }
        }
        
        updateBalanceLabel();
    }
    
    /**
     * Add a new transaction to the system
     * @param amount Transaction amount
     */
    private void addNewTransaction(double amount) {
        Transaction transaction = new Transaction(Transaction.TransactionType.DEPOSIT, amount);
        transactions.add(transaction);
        tableModel.addRow(transaction.toTableRow());
        
        // Update balance
        currentBalance += amount;
        updateBalanceLabel();
        
        // Save to file
        if (!FileIO.saveTransactions(transactions)) {
            JOptionPane.showMessageDialog(this,
                "Error saving transaction data",
                "File Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Update the balance display label
     */
    private void updateBalanceLabel() {
        balanceLabel.setText(String.format("Current Balance: $%.2f", currentBalance));
    }
}
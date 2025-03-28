package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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
        setLayout(new BorderLayout(10, 20));
        
        // Create header panel with title and current balance
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        headerPanel.setBackground(new Color(70, 130, 180)); // Match main panel color
        
        // Style the title
        JLabel titleLabel = new JLabel("Transaction History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        
        // Style the balance label
        balanceLabel = new JLabel("Current Balance: $0.00", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 24));
        balanceLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(balanceLabel, BorderLayout.SOUTH);
        
        // Style the table
        String[] columns = {"Date", "Time", "Type", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transactionTable = new JTable(tableModel);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 14));
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        transactionTable.getTableHeader().setBackground(new Color(70, 130, 180));
        transactionTable.getTableHeader().setForeground(Color.WHITE);
        
        // Add zebra striping to table
        transactionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 250));
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Create deposit panel with modern styling
        JPanel depositPanel = new JPanel();
        depositPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));
        depositPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        
        JLabel depositLabel = new JLabel("Deposit Amount: $");
        depositLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JTextField depositField = new JTextField(10);
        depositField.setFont(new Font("Arial", Font.PLAIN, 16));
        depositField.setPreferredSize(new Dimension(150, 35));
        
        JButton depositButton = createStyledButton("Make Deposit");
        JButton backButton = createStyledButton("Back to Main Menu");
        
        // Add action listener for back button
        backButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) getParent().getLayout();
            cardLayout.show(getParent(), "MAIN");
        });
        
        // Keep existing deposit button action listener
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double amount = Double.parseDouble(depositField.getText());
                    if (amount <= 0) {
                        showErrorMessage("Please enter a positive amount", "Invalid Input");
                        return;
                    }
                    
                    addNewTransaction(amount);
                    depositField.setText("");
                    
                } catch (NumberFormatException ex) {
                    showErrorMessage("Please enter a valid number", "Invalid Input");
                }
            }
        });
        
        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(depositButton);
        buttonPanel.add(backButton);
        
        depositPanel.add(depositLabel);
        depositPanel.add(depositField);
        depositPanel.add(buttonPanel);
        
        // Add all components to the main panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(depositPanel, BorderLayout.SOUTH);
        
        // Load transaction data
        loadTransactions();
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 35));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        
        return button;
    }
    
    private void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, 
                message, 
                title, 
                JOptionPane.ERROR_MESSAGE);
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
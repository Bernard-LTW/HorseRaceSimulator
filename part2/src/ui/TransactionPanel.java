package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

import models.Transaction;
import utils.FileIO;

import static ui.Button.createStyledButton;


public class TransactionPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable transactionTable;
    private double currentBalance = 0.0;
    private JLabel balanceLabel;
    private List<Transaction> transactions = new ArrayList<>();
    
    public TransactionPanel() {
        setLayout(new BorderLayout(10, 20));
        
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        headerPanel.setBackground(new Color(70, 130, 180)); // Match main panel color
        
        JLabel titleLabel = new JLabel("Transaction History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        
        balanceLabel = new JLabel("Current Balance: $0.00", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 24));
        balanceLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(balanceLabel, BorderLayout.SOUTH);
        
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
        
        backButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) getParent().getLayout();
            cardLayout.show(getParent(), "MAIN");
        });
        
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
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(depositButton);
        buttonPanel.add(backButton);
        
        depositPanel.add(depositLabel);
        depositPanel.add(depositField);
        depositPanel.add(buttonPanel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(depositPanel, BorderLayout.SOUTH);
        
        loadTransactions();
    }

    private void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }

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

    private void addNewTransaction(double amount) {
        Transaction transaction = new Transaction(Transaction.TransactionType.DEPOSIT, amount);
        transactions.add(transaction);
        tableModel.addRow(transaction.toTableRow());
        
        currentBalance += amount;
        updateBalanceLabel();
        
        if (!FileIO.saveTransactions(transactions)) {
            JOptionPane.showMessageDialog(this,
                "Error saving transaction data",
                "File Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBalanceLabel() {
        balanceLabel.setText(String.format("Current Balance: $%.2f", currentBalance));
    }
}
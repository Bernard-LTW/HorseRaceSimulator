package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private LocalDateTime dateTime;
    private TransactionType type;
    private double amount;
    
    // Constants for date/time formatting
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL
    }
    
    // Constructor for new transactions
    public Transaction(TransactionType type, double amount) {
        this.dateTime = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
    }
    
    // Constructor for loading from CSV
    public Transaction(String date, String time, String type, double amount) {
        this.dateTime = LocalDateTime.parse(date + " " + time, 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.type = TransactionType.valueOf(type.toUpperCase());
        this.amount = amount;
    }
    
    // Getters
    public String getDate() {
        return dateTime.format(DATE_FORMATTER);
    }
    
    public String getTime() {
        return dateTime.format(TIME_FORMATTER);
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public double getAmount() {
        return amount;
    }
    
    // Convert to array for table display
    public Object[] toTableRow() {
        return new Object[]{
            getDate(),
            getTime(),
            type.toString(),
            amount
        };
    }
    
    // Convert to CSV format
    public String toCsvString() {
        return String.format("%s,%s,%s,%.2f",
            getDate(),
            getTime(),
            type.toString(),
            amount
        );
    }
}
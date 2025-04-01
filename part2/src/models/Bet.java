package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Bet {
    private String raceId;
    private String horseName;
    private char horseSymbol;
    private double amount;
    private LocalDateTime dateTime;
    private boolean won;
    private double winnings;
    
    // Constants for date/time formatting
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public Bet(String raceId, Horse horse, double amount) {
        this.raceId = raceId;
        this.horseName = horse.getName();
        this.horseSymbol = horse.getSymbol();
        this.amount = amount;
        this.dateTime = LocalDateTime.now();
        this.won = false;
        this.winnings = 0.0;
    }
    
    // Getters
    public String getRaceId() { return raceId; }
    public String getHorseName() { return horseName; }
    public char getHorseSymbol() { return horseSymbol; }
    public double getAmount() { return amount; }
    public String getDate() { return dateTime.format(DATE_FORMATTER); }
    public String getTime() { return dateTime.format(TIME_FORMATTER); }
    public boolean isWon() { return won; }
    public double getWinnings() { return winnings; }
    
    // Setters
    public void setWon(boolean won) { this.won = won; }
    public void setWinnings(double winnings) { this.winnings = winnings; }
    
    // Calculate potential winnings based on odds
    public double calculatePotentialWinnings(double odds) {
        return amount * odds;
    }
    
    // Convert to array for table display
    public Object[] toTableRow() {
        return new Object[]{
            getDate(),
            getTime(),
            raceId,
            horseName,
            amount,
            won ? "Yes" : "No",
            winnings
        };
    }
    
    // Convert to CSV format
    public String toCsvString() {
        return String.format("%s,%s,%s,%s,%c,%.2f,%b,%.2f",
            getDate(),
            getTime(),
            raceId,
            horseName,
            horseSymbol,
            amount,
            won,
            winnings
        );
    }
} 
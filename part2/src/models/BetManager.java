package models;

import utils.FileIO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class BetManager {
    private List<Bet> bets;
    private Map<String, List<Bet>> raceBets;
    
    public BetManager() {
        bets = new ArrayList<>();
        raceBets = new HashMap<>();
        loadBets();
    }
    
    /**
     * Place a bet on a horse in a race
     * @param race The race to bet on
     * @param horse The horse to bet on
     * @param amount The amount to bet
     * @return true if bet was placed successfully, false otherwise
     */
    public boolean placeBet(Race race, Horse horse, double amount) {
        // Create new bet
        Bet bet = new Bet(race.getRaceID(), horse, amount);
        
        // Add to lists
        bets.add(bet);
        raceBets.computeIfAbsent(race.getRaceID(), k -> new ArrayList<>()).add(bet);
        
        // Create transaction for the bet
        Transaction transaction = new Transaction(Transaction.TransactionType.BET, amount);
        
        // Save both bet and transaction
        return FileIO.saveBets(bets) && FileIO.saveTransactions(List.of(transaction));
    }
    
    /**
     * Process race results and update bets
     * @param race The completed race
     */
    public void processRaceResults(Race race) {
        List<Bet> raceBetsList = raceBets.get(race.getRaceID());
        if (raceBetsList == null || raceBetsList.isEmpty()) {
            return; // No bets to process
        }
        
        // Get winning horse
        Horse winner = race.getFinishOrder().get(0);
        
        // Calculate odds based on number of horses
        double odds = race.getLanes().length;
        
        // Process each bet
        for (Bet bet : raceBetsList) {
            // Check if bet was on the winning horse by matching name and symbol
            if (bet.getHorseName().equals(winner.getName()) && bet.getHorseSymbol() == winner.getSymbol()) {
                // Calculate winnings
                double winnings = bet.calculatePotentialWinnings(odds);
                bet.setWon(true);
                bet.setWinnings(winnings);
                
                // Create transaction for winnings
                Transaction transaction = new Transaction(Transaction.TransactionType.WIN, winnings);
                FileIO.saveTransactions(List.of(transaction));
            }
        }
        
        // Save updated bets
        FileIO.saveBets(bets);
    }
    
    /**
     * Get all bets for a specific race
     * @param raceId The ID of the race
     * @return List of bets for the race
     */
    public List<Bet> getRaceBets(String raceId) {
        return raceBets.getOrDefault(raceId, new ArrayList<>());
    }
    
    /**
     * Get total amount bet on a race
     * @param raceId The ID of the race
     * @return Total amount bet
     */
    public double getTotalRaceBets(String raceId) {
        return raceBets.getOrDefault(raceId, new ArrayList<>())
                      .stream()
                      .mapToDouble(Bet::getAmount)
                      .sum();
    }
    
    /**
     * Load bets from CSV file
     */
    private void loadBets() {
        bets = FileIO.loadBets();
        // Rebuild raceBets map
        raceBets.clear();
        for (Bet bet : bets) {
            raceBets.computeIfAbsent(bet.getRaceId(), k -> new ArrayList<>()).add(bet);
        }
    }
}

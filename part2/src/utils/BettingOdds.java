package utils;

import models.Horse;
import models.Track;
import models.RaceStatistics;
import java.util.Map;

public class BettingOdds {
    
    /**
     * Calculate betting odds for a horse based on its confidence and track conditions.
     * Returns odds in decimal format (e.g., 2.5 means 2.5x return on bet)
     */
    public static double calculateOdds(Horse horse, Track track) {
        // Base odds calculation using horse confidence
        // Lower confidence = higher odds
        double baseOdds = 2.0 + ((1.0 - horse.getConfidence()) * 8.0);
        
        // Adjust odds based on track conditions
        double speedModifier = track.getSpeedModifier();
        double fallRiskModifier = track.getFallRiskModifier();
        
        // Modify odds based on track conditions
        double adjustedOdds = baseOdds;
        adjustedOdds *= (2.0 - speedModifier); // Lower speed = higher odds
        adjustedOdds *= (1.0 + fallRiskModifier); // Higher fall risk = higher odds
        
        // Ensure minimum odds of 1.1
        return Math.max(1.1, adjustedOdds);
    }
    
    /**
     * Format odds as a string in traditional format (e.g., "2.5 to 1")
     */
    public static String formatOdds(double decimalOdds) {
        return String.format("%.1f to 1", decimalOdds - 1.0);
    }
    
    /**
     * Calculate potential winnings for a bet amount at given odds
     */
    public static double calculatePotentialWinnings(double betAmount, double odds) {
        return betAmount * odds;
    }
} 
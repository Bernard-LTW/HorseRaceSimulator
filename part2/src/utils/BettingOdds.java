package utils;

import models.Horse;
import models.Track;

public class BettingOdds {

    public static double calculateOdds(Horse horse, Track track) {

        double adjustedOdds = 2.0 + ((1.0 - horse.getConfidence()) * 8.0);
        adjustedOdds *= (2.0 - track.getSpeedModifier());
        adjustedOdds *= (1.0 + track.getFallRiskModifier());

        return Math.max(1.1, adjustedOdds);
    }

    public static String formatOdds(double decimalOdds) {
        return String.format("%.1f to 1", decimalOdds - 1.0);
    }

    public static double calculatePotentialWinnings(double betAmount, double odds) {
        return betAmount * odds;
    }
} 
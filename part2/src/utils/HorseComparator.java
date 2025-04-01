package utils;

import models.Horse;
import models.HorseItem;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class for comparing two horses based on various attributes
 */
public class HorseComparator {
    
    /**
     * Compare two horses and return a detailed comparison map
     * @param horse1 First horse to compare
     * @param horse2 Second horse to compare
     * @return Map containing comparison results
     */
    public static Map<String, Object> compareHorses(Horse horse1, Horse horse2) {
        Map<String, Object> comparison = new HashMap<>();
        
        // Basic attributes comparison
        comparison.put("name1", horse1.getName());
        comparison.put("name2", horse2.getName());
        comparison.put("symbol1", horse1.getSymbol());
        comparison.put("symbol2", horse2.getSymbol());
        comparison.put("breed1", horse1.getBreed());
        comparison.put("breed2", horse2.getBreed());
        comparison.put("coatColor1", horse1.getCoatColor());
        comparison.put("coatColor2", horse2.getCoatColor());
        
        // Performance attributes
        comparison.put("confidence1", horse1.getConfidence());
        comparison.put("confidence2", horse2.getConfidence());
        comparison.put("confidenceDifference", horse1.getConfidence() - horse2.getConfidence());
        
        // Equipment comparison
        comparison.put("equipment1", horse1.getEquipment());
        comparison.put("equipment2", horse2.getEquipment());
        
        // Accessories comparison
        comparison.put("accessories1", horse1.getAccessories());
        comparison.put("accessories2", horse2.getAccessories());
        
        // Calculate total speed modifier
        double speedMod1 = calculateTotalSpeedModifier(horse1);
        double speedMod2 = calculateTotalSpeedModifier(horse2);
        comparison.put("speedModifier1", speedMod1);
        comparison.put("speedModifier2", speedMod2);
        comparison.put("speedModifierDifference", speedMod1 - speedMod2);
        
        // Calculate total endurance modifier
        double enduranceMod1 = calculateTotalEnduranceModifier(horse1);
        double enduranceMod2 = calculateTotalEnduranceModifier(horse2);
        comparison.put("enduranceModifier1", enduranceMod1);
        comparison.put("enduranceModifier2", enduranceMod2);
        comparison.put("enduranceModifierDifference", enduranceMod1 - enduranceMod2);
        
        return comparison;
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
    
    /**
     * Format the comparison results as a readable string
     * @param comparison Map containing comparison results
     * @return Formatted string with comparison details
     */
    public static String formatComparison(Map<String, Object> comparison) {
        StringBuilder sb = new StringBuilder();
        
        // Header
        sb.append("=== Horse Comparison ===\n\n");
        
        // Basic Information
        sb.append("Horse 1: ").append(comparison.get("name1"))
          .append(" (").append(comparison.get("symbol1")).append(")\n");
        sb.append("Breed: ").append(comparison.get("breed1"))
          .append(", Coat Color: ").append(comparison.get("coatColor1")).append("\n");
        
        sb.append("\nHorse 2: ").append(comparison.get("name2"))
          .append(" (").append(comparison.get("symbol2")).append(")\n");
        sb.append("Breed: ").append(comparison.get("breed2"))
          .append(", Coat Color: ").append(comparison.get("coatColor2")).append("\n\n");
        
        // Performance Metrics
        sb.append("=== Performance Metrics ===\n");
        sb.append(String.format("Confidence: %.2f vs %.2f (Difference: %.2f)\n",
            comparison.get("confidence1"), comparison.get("confidence2"),
            comparison.get("confidenceDifference")));
        
        sb.append(String.format("Speed Modifier: %.2f vs %.2f (Difference: %.2f)\n",
            comparison.get("speedModifier1"), comparison.get("speedModifier2"),
            comparison.get("speedModifierDifference")));
        
        sb.append(String.format("Endurance Modifier: %.2f vs %.2f (Difference: %.2f)\n",
            comparison.get("enduranceModifier1"), comparison.get("enduranceModifier2"),
            comparison.get("enduranceModifierDifference")));
        
        // Equipment
        sb.append("\n=== Equipment ===\n");
        sb.append("Horse 1: ").append(formatItemList(comparison.get("equipment1"))).append("\n");
        sb.append("Horse 2: ").append(formatItemList(comparison.get("equipment2"))).append("\n");
        
        // Accessories
        sb.append("\n=== Accessories ===\n");
        sb.append("Horse 1: ").append(formatItemList(comparison.get("accessories1"))).append("\n");
        sb.append("Horse 2: ").append(formatItemList(comparison.get("accessories2"))).append("\n");
        
        return sb.toString();
    }
    
    private static double calculateTotalSpeedModifier(Horse horse) {
        return horse.getEquipment().stream()
            .mapToDouble(item -> item.getSpeedModifier())
            .reduce(1.0, (a, b) -> a * b);
    }
    
    private static double calculateTotalEnduranceModifier(Horse horse) {
        return horse.getEquipment().stream()
            .mapToDouble(item -> item.getEnduranceModifier())
            .reduce(1.0, (a, b) -> a * b);
    }
    
    private static String formatItemList(Object items) {
        if (items == null) return "None";
        if (items instanceof List<?>) {
            List<?> itemList = (List<?>) items;
            if (itemList.isEmpty()) return "None";
            if (itemList.get(0) instanceof HorseItem) {
                return itemList.stream()
                    .map(item -> ((HorseItem) item).getName())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("None");
            }
        }
        return "None";
    }
} 
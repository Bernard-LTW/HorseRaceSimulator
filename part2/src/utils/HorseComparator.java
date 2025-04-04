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
    

    public static Map<String, Object> compareHorses(Horse horse1, Horse horse2) {
        Map<String, Object> comparison = new HashMap<>();
        
        comparison.put("name1", horse1.getName());
        comparison.put("name2", horse2.getName());
        comparison.put("symbol1", horse1.getSymbol());
        comparison.put("symbol2", horse2.getSymbol());
        comparison.put("breed1", horse1.getBreed());
        comparison.put("breed2", horse2.getBreed());
        comparison.put("coatColor1", horse1.getCoatColor());
        comparison.put("coatColor2", horse2.getCoatColor());
        
        comparison.put("confidence1", horse1.getConfidence());
        comparison.put("confidence2", horse2.getConfidence());
        comparison.put("confidenceDifference", horse1.getConfidence() - horse2.getConfidence());
        
        comparison.put("equipment1", horse1.getEquipment());
        comparison.put("equipment2", horse2.getEquipment());
        
        comparison.put("accessories1", horse1.getAccessories());
        comparison.put("accessories2", horse2.getAccessories());
        
        double speedMod1 = horse1.calculateTotalSpeedModifier();
        double speedMod2 = horse2.calculateTotalSpeedModifier();
        comparison.put("speedModifier1", speedMod1);
        comparison.put("speedModifier2", speedMod2);
        comparison.put("speedModifierDifference", speedMod1 - speedMod2);
        
        double enduranceMod1 = horse1.calculateTotalEnduranceModifier();
        double enduranceMod2 = horse2.calculateTotalEnduranceModifier();
        comparison.put("enduranceModifier1", enduranceMod1);
        comparison.put("enduranceModifier2", enduranceMod2);
        comparison.put("enduranceModifierDifference", enduranceMod1 - enduranceMod2);
        
        return comparison;
    }



    public static String formatComparison(Map<String, Object> comparison) {

        // Header

        String s = "=== Horse Comparison ===\n\n" +

                "Horse 1: " + comparison.get("name1") +
                " (" + comparison.get("symbol1") + ")\n" +
                "Breed: " + comparison.get("breed1") +
                ", Coat Color: " + comparison.get("coatColor1") + "\n" +
                "\nHorse 2: " + comparison.get("name2") +
                " (" + comparison.get("symbol2") + ")\n" +
                "Breed: " + comparison.get("breed2") +
                ", Coat Color: " + comparison.get("coatColor2") + "\n\n" +

                "=== Performance Metrics ===\n" +
                String.format("Confidence: %.2f vs %.2f (Difference: %.2f)\n",
                        comparison.get("confidence1"), comparison.get("confidence2"),
                        comparison.get("confidenceDifference")) +
                String.format("Speed Modifier: %.2f vs %.2f (Difference: %.2f)\n",
                        comparison.get("speedModifier1"), comparison.get("speedModifier2"),
                        comparison.get("speedModifierDifference")) +
                String.format("Endurance Modifier: %.2f vs %.2f (Difference: %.2f)\n",
                        comparison.get("enduranceModifier1"), comparison.get("enduranceModifier2"),
                        comparison.get("enduranceModifierDifference")) +

                "\n=== Equipment ===\n" +
                "Horse 1: " + formatItemList(comparison.get("equipment1")) + "\n" +
                "Horse 2: " + formatItemList(comparison.get("equipment2")) + "\n" +

                "\n=== Accessories ===\n" +
                "Horse 1: " + formatItemList(comparison.get("accessories1")) + "\n" +
                "Horse 2: " + formatItemList(comparison.get("accessories2")) + "\n";
        
        return s;
    }


    private static String formatItemList(Object items) {
        if (items == null) return "None";
        if (!(items instanceof List)) return "None";

        List<?> itemList = (List<?>) items;
        if (itemList.isEmpty()) return "None";
        if (!(itemList.get(0) instanceof HorseItem)) return "None";

        StringBuilder result = new StringBuilder();
        for (Object item : itemList) {
            HorseItem horseItem = (HorseItem) item;
            if (!result.isEmpty()) {
                result.append(", ");
            }
            result.append(horseItem.getName());
        }
        return result.toString();
    }
} 
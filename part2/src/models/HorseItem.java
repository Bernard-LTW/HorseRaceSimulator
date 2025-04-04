package models;

public class HorseItem {
    private final String name;
    private final String type; // "Equipment" or "Accessory"
    private final double speedModifier;
    private final double enduranceModifier;
    private final double confidenceModifier;
    private final String description;

    //Equipment Constructor
    public HorseItem(String name, String type, double speedModifier, 
                    double enduranceModifier, double confidenceModifier, 
                    String description) {
        this.name = name;
        this.type = type;
        this.speedModifier = speedModifier;
        this.enduranceModifier = enduranceModifier;
        this.confidenceModifier = confidenceModifier;
        this.description = description;
    }

    //Accessories Constructor(All modifiers set to 1 -> no effect)
    public HorseItem(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.speedModifier = 1.0;
        this.enduranceModifier = 1.0;
        this.confidenceModifier = 1.0;
        this.description = description;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public double getSpeedModifier() { return speedModifier; }
    public double getEnduranceModifier() { return enduranceModifier; }
    public double getConfidenceModifier() { return confidenceModifier; }
    public String getDescription() { return description; }
} 
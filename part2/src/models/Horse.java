package models;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a Horse which aims to represent a horse within a horse race. 
 * Each object stores the name, Unicode symbol, distance travelled, confidence and boolean of if a horse has fallen.
 * 
 * @author Tsz Wo Bernard Lee
 * @version 2025-03-16
 */
public class Horse
{
    //Fields of class Horse
    private final String horseName;
    private char horseSymbol;
    private int distanceTravelled=0;
    private double horseConfidence=0.0;
    private boolean fallen=false;
    
    // New fields for customization
    private String breed;
    private String coatColor;
    private double baseSpeed;
    private double baseEndurance;
    private List<HorseItem> equipment;
    private List<HorseItem> accessories;
    
    //Constructor of class Horse
    /**
     * Constructor for objects of class Horse
     */
    public Horse(char horseSymbol, String horseName, double horseConfidence, 
                String breed, String coatColor, double baseSpeed, double baseEndurance)
    {
       this.horseName=horseName;
       this.horseSymbol=horseSymbol;
       this.setConfidence(horseConfidence);
       this.breed = breed;
       this.coatColor = coatColor;
       this.baseSpeed = baseSpeed;
       this.baseEndurance = baseEndurance;
       this.equipment = new ArrayList<>();
       this.accessories = new ArrayList<>();
    }
    
    
    
    //Other methods of class Horse
    public double getConfidence()
    {
        return this.horseConfidence;
    }
    
    public int getDistanceTravelled()
    {
        return this.distanceTravelled;
    }
    
    public String getName()
    {
        return this.horseName;
    }
    
    public char getSymbol()
    {
        return this.horseSymbol;
    }

    public boolean hasFallen()
    {
        return this.fallen;
    }

    public void fall()
    {
        this.fallen=true;
    }

    public void goBackToStart()
    {
        this.distanceTravelled=0;
    }


    public void moveForward()
    { this.distanceTravelled++;}

    public void setConfidence(double newConfidence)
    {
        if(newConfidence>=0.0 && newConfidence<=1.0){
            this.horseConfidence=newConfidence;
            utils.FileIO.updateHorseConfidence(this.horseName,this.horseSymbol,newConfidence);
        }
    }
    
    public void setSymbol(char newSymbol)
    {
        this.horseSymbol=newSymbol;
    }
    
    public void adjustPerformance(Track track) {
        double speedModifier = track.getSpeedModifier() * calculateTotalSpeedModifier();
        double fallRiskModifier = track.getFallRiskModifier();
        double shapeAdjustment = track.getShapeSpeedAdjustment(this.distanceTravelled);
        double confidenceModifier = calculateTotalConfidenceModifier();
        double enduranceModifier = calculateTotalEnduranceModifier();

        // Adjust confidence based on fall risk and modifiers
        this.horseConfidence = (this.horseConfidence * confidenceModifier) - fallRiskModifier;
        if (this.horseConfidence < 0) {
            this.horseConfidence = 0;
        }

        // Simulate falling
        if (Math.random() < fallRiskModifier) {
            this.fallen = true;
        }

        // Adjust distance travelled based on all modifiers
        if (!this.fallen) {
            double moveAmount = 1 * speedModifier * shapeAdjustment * enduranceModifier;
            this.distanceTravelled += (int) moveAmount;
        }
    }

    // New methods for customization
    public void addEquipment(HorseItem item) {
        if (item.getType().equals("Equipment")) {
            equipment.add(item);
        }
    }

    public void addAccessory(HorseItem item) {
        if (item.getType().equals("Accessory")) {
            accessories.add(item);
        }
    }

    public void removeEquipment(String itemName) {
        equipment.removeIf(item -> item.getName().equals(itemName));
    }

    public void removeAccessory(String itemName) {
        accessories.removeIf(item -> item.getName().equals(itemName));
    }

    public List<HorseItem> getEquipment() {
        return new ArrayList<>(equipment);
    }

    public List<HorseItem> getAccessories() {
        return new ArrayList<>(accessories);
    }

    public String getBreed() {
        return breed;
    }

    public String getCoatColor() {
        return coatColor;
    }

    public double getBaseSpeed() {
        return baseSpeed;
    }

    public double getBaseEndurance() {
        return baseEndurance;
    }

    private double calculateTotalSpeedModifier() {
        double totalModifier = baseSpeed;
        for (HorseItem item : equipment) {
            totalModifier *= item.getSpeedModifier();
        }
        return totalModifier;
    }

    private double calculateTotalEnduranceModifier() {
        double totalModifier = baseEndurance;
        for (HorseItem item : equipment) {
            totalModifier *= item.getEnduranceModifier();
        }
        return totalModifier;
    }

    private double calculateTotalConfidenceModifier() {
        double totalModifier = 1.0;
        for (HorseItem item : equipment) {
            totalModifier *= item.getConfidenceModifier();
        }
        return totalModifier;
    }
}

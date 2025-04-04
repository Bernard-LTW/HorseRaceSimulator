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
    private final String horseName;
    private char horseSymbol;
    private int distanceTravelled=0;
    private double horseConfidence=0.0;
    private boolean fallen=false;
    private String breed;
    private String coatColor;
    private List<HorseItem> equipment;
    private List<HorseItem> accessories;
    

    public Horse(char horseSymbol, String horseName, double horseConfidence, 
                String breed, String coatColor)
    {
       this.horseName = horseName;
       this.horseSymbol = horseSymbol;
       this.horseConfidence = horseConfidence;
       this.breed = breed;
       this.coatColor = coatColor;
       this.equipment = new ArrayList<>();
       this.accessories = new ArrayList<>();
    }

    
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

    public void moveForward(){ this.distanceTravelled++;}

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

    public void removeEquipment(String itemName) {
        equipment.removeIf(item -> item.getName().equals(itemName));
    }

    public void removeAccessory(String itemName) {
        accessories.removeIf(item -> item.getName().equals(itemName));
    }

    public List<HorseItem> getEquipment() {
        return equipment;
    }

    public List<HorseItem> getAccessories() {
        return accessories;
    }

    public String getBreed() {
        return breed;
    }

    public String getCoatColor() {
        return coatColor;
    }

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

    public double calculateTotalSpeedModifier() {
        double totalModifier = 1.0;
        for (HorseItem item : equipment) {
            totalModifier *= item.getSpeedModifier();
        }
        return totalModifier;
    }

    public double calculateTotalEnduranceModifier() {
        double totalModifier = 1.0;
        for (HorseItem item : equipment) {
            totalModifier *= item.getEnduranceModifier();
        }
        return totalModifier;
    }

    public double calculateTotalConfidenceModifier() {
        double totalModifier = 1.0;
        for (HorseItem item : equipment) {
            totalModifier *= item.getConfidenceModifier();
        }
        return totalModifier;
    }
}


/**
 * This is a Horse which aims to represent a horse within a horse race. 
 * Each object stores the name, unicode symbol, distance travelled, confidence and boolean of if a horse has fallen.
 * 
 * @author Tsz Wo Bernard Lee
 * @version 2025-03-16
 */
public class Horse
{
    //Fields of class Horse
    private String horseName;
    private char horseSymbol;
    private int distanceTravelled;
    private double horseConfidence;
    private boolean fallen=false;

    
      
    //Constructor of class Horse
    /**
     * Constructor for objects of class Horse
     */
    public Horse(char horseSymbol, String horseName, double horseConfidence)
    {
       this.horseName=horseName;
       this.horseSymbol=horseSymbol;
       this.setConfidence(horseConfidence);
    }
    
    
    
    //Other methods of class Horse
    public void fall()
    {
        this.fallen=true;
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
    
    public void goBackToStart()
    {
        this.distanceTravelled=0;
    }
    
    public boolean hasFallen()
    {
        return this.fallen;
    }

    public void moveForward()
    {
        if(!this.fallen){
            this.distanceTravelled++;
        }
    }

    public void setConfidence(double newConfidence)
    {
        if(newConfidence>=0.0 && newConfidence<=1.0){
            this.horseConfidence=newConfidence;
        }
    }
    
    public void setSymbol(char newSymbol)
    {
        this.horseSymbol=newSymbol;
    }
    
}

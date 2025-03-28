import java.util.concurrent.TimeUnit;
import java.lang.Math;

/**
 * A three-horse race, each horse running in its own lane
 * for a given distance
 * 
 * @author McRaceface
 * @version 1.0
 */
public class Race
{
    private int raceLength;
    private Horse[] lanes;

    /**
     * Constructor for objects of class Race
     * Initially there are no horses in the lanes
     * 
     * @param distance the length of the racetrack (in metres/yards...)
     * @param numberOfLanes the number of lanes in the race
     */
    public Race(int distance, int numberOfLanes)
    {
        // initialise instance variables
        raceLength = distance;
        lanes = new Horse[numberOfLanes];
    }
    
    /**
     * Adds a horse to the race in a given lane
     * 
     * @param theHorse the horse to be added to the race
     * @param laneNumber the lane that the horse will be added to
     */
    public void addHorse(Horse theHorse, int laneNumber)
    {
        if (laneNumber < 1 || laneNumber > lanes.length)
        {
            System.out.println("Cannot add horse to lane " + laneNumber + " because there is no such lane");
            return;
        }
        lanes[laneNumber - 1] = theHorse;
    }
    
    /**
     * Start the race
     * The horse are brought to the start and
     * then repeatedly moved forward until the 
     * race is finished
     */
    public void startRace()
    {
        // Check if any lane is empty
        for (Horse horse : lanes)
        {
            if (horse == null)
            {
                System.out.println("Cannot start race: Not all lanes have horses assigned");
                return;
            }
        }
        
        boolean finished = false;
        
        // Reset all horses
        for (Horse horse : lanes)
        {
            horse.goBackToStart();
        }
        
        while (!finished)
        {
            // Move each horse
            for (Horse horse : lanes)
            {
                moveHorse(horse);
            }
            
            printRace();
            
            // Check for winners
            for (Horse horse : lanes)
            {
                if (raceWonBy(horse))
                {
                    System.out.println("Horse " + horse.getSymbol() + " has won the race!");
                    finished = true;
                    break;
                }
            }
            
            try { 
                TimeUnit.MILLISECONDS.sleep(100);
            } catch(Exception e) {}
        }
    }
    
    /**
     * Randomly make a horse move forward or fall depending
     * on its confidence rating
     * A fallen horse cannot move
     * 
     * @param theHorse the horse to be moved
     */
    private void moveHorse(Horse theHorse)
    {
        //if the horse has fallen it cannot move, 
        //so only run if it has not fallen
        if  (!theHorse.hasFallen())
        {
            //the probability that the horse will move forward depends on the confidence;
            if (Math.random() < theHorse.getConfidence())
            {
               theHorse.moveForward();
            }
            
            //the probability that the horse will fall is very small (max is 0.1)
            //but will also will depends exponentially on confidence 
            //so if you double the confidence, the probability that it will fall is *2
            if (Math.random() < (0.1*theHorse.getConfidence()*theHorse.getConfidence()))
            {
                theHorse.fall();
            }
        }
    }
        
    /** 
     * Determines if a horse has won the race
     *
     * @param theHorse The horse we are testing
     * @return true if the horse has won, false otherwise.
     */
    private boolean raceWonBy(Horse theHorse)
    {
        return theHorse.getDistanceTravelled() >= raceLength;
    }
    
    /***
     * Print the race on the terminal
     */
    private void printRace()
    {
        System.out.print('\u000C');  //clear the terminal window
        
        multiplePrint('=',raceLength+3); //top edge of track
        System.out.println();
        
        for (Horse horse : lanes)
        {
            printLane(horse);
            System.out.println();
        }
        
        multiplePrint('=',raceLength+3); //bottom edge of track
        System.out.println();    
    }
    
    /**
     * print a horse's lane during the race
     * for example
     * |           X                      |
     * to show how far the horse has run
     */
    private void printLane(Horse theHorse)
    {
        //calculate how many spaces are needed before
        //and after the horse
        int spacesBefore = theHorse.getDistanceTravelled();
        int spacesAfter = raceLength - theHorse.getDistanceTravelled();
        
        //print a | for the beginning of the lane
        System.out.print('|');
        
        //print the spaces before the horse
        multiplePrint(' ',spacesBefore);
        
        //if the horse has fallen then print dead
        //else print the horse's symbol
        if(theHorse.hasFallen())
        {
            System.out.print('\u2322');
        }
        else
        {
            System.out.print(theHorse.getSymbol());
        }
        
        //print the spaces after the horse
        multiplePrint(' ',spacesAfter);
        
        //print the | for the end of the track
        System.out.print('|');
    }
        
    
    /***
     * print a character a given number of times.
     * e.g. printmany('x',5) will print: xxxxx
     * 
     * @param aChar the character to Print
     */
    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }
}

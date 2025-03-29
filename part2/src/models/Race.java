package models;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.ArrayList;

/**
 * A three-horse race, each horse running in its own lane
 * for a given distance
 * 
 * @author McRaceface
 * @version 1.0
 */
public class Race {
    private Track track;
    private Horse[] lanes;
    private List<Horse> finishOrder;
    final static double fallProbability = 0.01;

    /**
     * Constructor for objects of class Race
     * Initially there are no horses in the lanes
     * 
     * @param distance the length of the racetrack (in metres/yards...)
     * @param numberOfLanes the number of lanes in the race
     */
    public Race(Track track) {
        this.track = track;
        this.lanes = new Horse[track.getLaneCount()];
        this.finishOrder = new ArrayList<>();
    }
    
    /**
     * Adds a horse to the race in a given lane
     * 
     * @param theHorse the horse to be added to the race
     * @param laneNumber the lane that the horse will be added to
     */
    public void addHorse(Horse theHorse, int laneNumber) {
        if (laneNumber < 1 || laneNumber > lanes.length) {
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
    public void startRace() {
        for (Horse horse : lanes) {
            if (horse == null) {
                System.out.println("Cannot start race: Not all lanes have horses assigned");
                return;
            }
        }
        
        boolean raceComplete = false;
        finishOrder.clear();
        
        for (Horse horse : lanes) {
            horse.goBackToStart();
        }
        
        while (!raceComplete) {
            for (Horse horse : lanes) {
                if (!raceWonBy(horse) && !horse.hasFallen()) {
                    moveHorse(horse);
                    if (raceWonBy(horse) && !finishOrder.contains(horse)) {
                        finishOrder.add(horse);
                    }
                }
            }
            
            printRace();
            
            raceComplete = true;
            for (Horse horse : lanes) {
                if (!raceWonBy(horse) && !horse.hasFallen()) {
                    raceComplete = false;
                    break;
                }
            }
            
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception e) {
            }
        }
        
        System.out.println("\n=== RACE RESULTS ===");
        
        for (int i = 0; i < finishOrder.size(); i++) {
            Horse horse = finishOrder.get(i);
            System.out.println((i + 1) + ". Horse " + horse.getSymbol() + " - FINISHED (" + horse.getDistanceTravelled() + " units)");
        }
        
        List<Horse> fallenHorses = new ArrayList<>();
        for (Horse horse : lanes) {
            if (horse.hasFallen()) {
                fallenHorses.add(horse);
            }
        }
        Collections.sort(fallenHorses, (h1, h2) -> Double.compare(h2.getDistanceTravelled(), h1.getDistanceTravelled()));
        
        for (Horse horse : fallenHorses) {
            System.out.println((finishOrder.size() + fallenHorses.indexOf(horse) + 1) + ". Horse " + horse.getSymbol() + " - FELL at " + horse.getDistanceTravelled() + " units");
        }
        
        if (!finishOrder.isEmpty()) {
            System.out.println("\nWINNER: Horse " + finishOrder.get(0).getSymbol() + "!");
        } else {
            System.out.println("\nNo winners - all horses fell!");
        }
    }
    
    /**
     * Randomly make a horse move forward or fall depending
     * on its confidence rating
     * A fallen horse cannot move
     * 
     * @param theHorse the horse to be moved
     */
    private void moveHorse(Horse theHorse) {
        if (!theHorse.hasFallen()) {
            if (Math.random() < theHorse.getConfidence()) {
                theHorse.moveForward();
            }

            if (Math.random() < (fallProbability * theHorse.getConfidence() * theHorse.getConfidence())) {
                theHorse.fall();
            }
        }
    }
    
    /***
     * Determines if a horse has won the race
     *
     * @param theHorse The horse we are testing
     * @return true if the horse has won, false otherwise.
     */
    private boolean raceWonBy(Horse theHorse) {
        return theHorse.getDistanceTravelled() >= track.getLength();
    }
    
    /***
     * Print the race on the terminal
     */
    private void printRace() {
        System.out.print("\033[H\033[2J");
        
        multiplePrint('=', track.getLength() + 3);
        System.out.println();
        
        for (Horse horse : lanes) {
            printLane(horse);
            System.out.println();
        }
        
        multiplePrint('=', track.getLength() + 3);
        System.out.println();    
    }
    
    /**
     * print a horse's lane during the race
     * for example
     * |           X                      |
     * to show how far the horse has run
     */
    private void printLane(Horse theHorse) {
        int spacesBefore = theHorse.getDistanceTravelled();
        int spacesAfter = track.getLength() - theHorse.getDistanceTravelled();
        
        System.out.print('|');
        
        multiplePrint(' ', spacesBefore);
        
        if (theHorse.hasFallen()) {
            System.out.print('X');
        } else {
            System.out.print(theHorse.getSymbol());
        }
        
        multiplePrint(' ', spacesAfter);
        
        System.out.print('|');
    }
        
    /***
     * print a character a given number of times.
     * e.g. printmany('x',5) will print: xxxxx
     * 
     * @param aChar the character to Print
     */
    private void multiplePrint(char aChar, int times) {
        int i = 0;
        while (i < times) {
            System.out.print(aChar);
            i = i + 1;
        }
    }
}

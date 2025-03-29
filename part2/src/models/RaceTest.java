package models;

import utils.FileIO;
import models.Race;

public class RaceTest {
    public static void main(String[] args) {

        Horse[] horses = FileIO.ingestHorses();
        // Create a test track
        Track testTrack = new Track("Test Track", horses.length, 30, Track.TrackShape.OVAL, Track.TrackCondition.DRY);
        
        // Create a race with the test track
        Race testRace = new Race(testTrack);
        
        // Load horses from CSV
        
        
        // Add horses to the race
        int lane = 1;
        for(Horse horse : horses) {
            testRace.addHorse(horse, lane++);
        }
        
        // Start the race
        testRace.startRace();
    }
}

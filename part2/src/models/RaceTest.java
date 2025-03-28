package models;


import utils.FileIO;
import models.Race;

public class RaceTest {
    public static void main(String[] args) {
        Horse[] horses = FileIO.ingestHorses();
        //for every horse, add it to a race and run the race
        int lane = 1;
        Race testRace = new Race(30, horses.length);
        for(Horse horse : horses) {
            testRace.addHorse(horse, lane++);
        }
        testRace.startRace();
    }
}

package models;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import utils.FileIO;

public class Race {

    private String raceID = UUID.randomUUID().toString();
    private Track track;
    private Horse[] lanes;
    private List<Horse> finishOrder;
    private long startTime;
    private Map<Horse, Long> finishTimes;
    final static double fallProbability = 0.01;
    private Map<Horse, Double> confidenceChanges = new HashMap<>();


    public Race(Track track) {
        this.track = track;
        this.lanes = new Horse[track.getLaneCount()];
        this.finishOrder = new ArrayList<>();
        this.finishTimes = new HashMap<>();
    }

    public String getRaceID() {
        return this.raceID;
    }


    public Track getTrack() {
        return this.track;
    }

    public void addHorse(Horse theHorse, int laneNumber) {
        if (laneNumber < 1 || laneNumber > lanes.length) {
            System.out.println("Cannot add horse to lane " + laneNumber + " because there is no such lane");
            return;
        }
        lanes[laneNumber - 1] = theHorse;
    }

    public void startRace() {
        for (Horse horse : lanes) {
            if (horse == null) {
                System.out.println("Cannot start race: Not all lanes have horses assigned");
                return;
            }
        }

        boolean raceComplete = false;
        finishOrder.clear();
        finishTimes.clear();
        startTime = System.currentTimeMillis();

        for (Horse horse : lanes) {
            horse.goBackToStart();
        }

        while (!raceComplete) {
            System.out.flush();

            for (Horse horse : lanes) {
                if (!raceWonBy(horse) && !horse.hasFallen()) {
                    moveHorse(horse);
                    if (raceWonBy(horse) && !finishOrder.contains(horse)) {
                        finishOrder.add(horse);
                        finishTimes.put(horse, System.currentTimeMillis() - startTime);
                    }
                }
            }

            //For debugging:
            //printRace();

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

        System.out.println("Race ID:" + this.getRaceID());

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

        // Store race results after the race ends
        storeRaceResults();
    }

    private void moveHorse(Horse theHorse) {
        if (!theHorse.hasFallen()) {
            // Get modifiers
            double speedModifier = track.getSpeedModifier();
            double fallRiskModifier = track.getFallRiskModifier();
            double shapeAdjustment = track.getShapeSpeedAdjustment(theHorse.getDistanceTravelled());
            double equipmentSpeedMod = theHorse.calculateTotalSpeedModifier();
            double equipmentEnduranceMod = theHorse.calculateTotalEnduranceModifier();
            double equipmentConfidenceMod = theHorse.calculateTotalConfidenceModifier();

            double effectiveConfidence = theHorse.getConfidence() * equipmentConfidenceMod;
            effectiveConfidence = Math.min(1.0, effectiveConfidence);
            double moveChance = effectiveConfidence * speedModifier * shapeAdjustment * equipmentSpeedMod;

            double raceProgress = (double) theHorse.getDistanceTravelled() / track.getLength();
            double enduranceEffect = 1.0 - (raceProgress * (1.0 - equipmentEnduranceMod));
            moveChance *= enduranceEffect;

            if (Math.random() < moveChance) {
                theHorse.moveForward();
            }

            double fallChance = (fallProbability * effectiveConfidence * effectiveConfidence + fallRiskModifier) / equipmentConfidenceMod;

            if (Math.random() < fallChance) {
                theHorse.fall();
            }
        }
    }

    private boolean raceWonBy(Horse theHorse) {
        return theHorse.getDistanceTravelled() >= track.getLength();
    }

    public Map<Horse, Double> getConfidenceChanges() {
        return confidenceChanges;
    }

    public List<Horse> getFinishOrder() {
        return finishOrder;
    }

    public Horse[] getLanes() {
        return lanes;
    }

    private void printRace() {
        // Print top border
        multiplePrint('=', track.getLength() + 3);
        System.out.println();

        // Print each lane
        for (Horse horse : lanes) {
            printLane(horse);
            System.out.println();
        }

        // Print bottom border
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

    /**
     * Adjusts a horse's confidence based on their race performance
     * - Winner gets a small confidence boost
     * - Other finishers get a tiny boost based on position
     * - Fallen horses get a small confidence penalty
     * Maximum adjustment is 7% per race
     */
    private void adjustHorseConfidence(Horse horse, int position, boolean hasFallen) {
        double currentConfidence = horse.getConfidence();
        double adjustment = 0.0;

        if (hasFallen) {
            adjustment = -0.03;
        } else if (position == 1) {
            adjustment = 0.07;
        } else if (position <= 3) {
            adjustment = 0.02;
        } else {
            adjustment = 0.01;
        }

        double newConfidence = Math.max(0.0, Math.min(1.0, currentConfidence + adjustment));
        horse.setConfidence(newConfidence);
    }

    /**
     * Stores the race results in the CSV file
     */

    private void storeRaceResults() {
        // Create a list to store all horses in finish order
        List<Horse> allHorses = new ArrayList<>(finishOrder);
        confidenceChanges.clear();

        List<Horse> fallenHorses = new ArrayList<>();
        for (Horse horse : lanes) {
            if (horse.hasFallen()) {
                fallenHorses.add(horse);
            }
        }
        fallenHorses.sort((h1, h2) -> Double.compare(h2.getDistanceTravelled(), h1.getDistanceTravelled()));
        allHorses.addAll(fallenHorses);

        for (int i = 0; i < allHorses.size(); i++) {
            Horse horse = allHorses.get(i);
            long finishTime = finishTimes.getOrDefault(horse, -1L); // -1 for fallen horses
            double oldConfidence = horse.getConfidence();

//            FileIO.storeRaceResult(raceID, horse.getName(), horse.getSymbol(),
//                    horse.getConfidence(), horse.getDistanceTravelled(), i + 1, finishTime);

            RaceStatistics.storeRaceStats(raceID, horse.getName(), horse.getConfidence(),
                    horse.getDistanceTravelled(), i + 1, finishTime, track.getName(), track.getCondition());

            adjustHorseConfidence(horse, i + 1, horse.hasFallen());

            //for ui summary
            double change = horse.getConfidence() - oldConfidence;
            confidenceChanges.put(horse, change);
        }

        if (!finishOrder.isEmpty()) {
            Horse winner = finishOrder.get(0);
            long winnerTime = finishTimes.get(winner);
            RaceStatistics.updateTrackRecords(track.getName(), winnerTime, winner.getName());
        }
    }
}

package models;

import utils.FileIO;

import java.util.*;

public class RaceStatistics {

    public static Map<String, Object> getHorseStats(String horseName) { return FileIO.getHorseStats(horseName); }

    public static Map<String, Object> getBettingStats() { return FileIO.getBettingStats(); }
    

    public static void storeRaceStats(String raceID, String horseName, double confidence, 
            int distanceTravelled, int position, long raceDuration, String trackName, 
            Track.TrackCondition trackCondition) {
        FileIO.storeRaceStats(raceID, horseName, confidence, distanceTravelled, position, 
                            raceDuration, trackName, trackCondition);
    }

    public static void updateTrackRecords(String trackName,
            long raceDuration, String horseName) {
        List<Track> tracks = FileIO.loadTracks();
        Track targetTrack = null;
        
        for (Track track : tracks) {
            if (track.getName().equals(trackName)) {
                targetTrack = track;
                break;
            }
        }
        
        if (targetTrack != null) {
            if (targetTrack.getBestTime() == 0 || raceDuration < targetTrack.getBestTime()) {
                targetTrack.setBestTime(raceDuration);
                targetTrack.setBestHorse(horseName);
                FileIO.saveTracks(tracks);
            }
        }
    }


} 
package models;

import utils.FileIO;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.stream.Collectors;

public class RaceStatistics {
    private static final String RACES_CSV_FILE = "part2/src/data/races.csv";
    private static final String BETS_CSV_FILE = "part2/src/data/bets/bets.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Track records data structure
    private static Map<String, Map<Track.TrackCondition, Double>> trackRecords = new HashMap<>();
    
    /**
     * Loads track records from races.csv file
     */
    public static void loadTrackRecords() {
        try (BufferedReader reader = new BufferedReader(new FileReader(RACES_CSV_FILE))) {
            String line;
            reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals("RECORD")) {  // Only process track records
                    try {
                        String trackName = parts[1];  // Track name is in position 1
                        String conditionStr = parts[2].trim().toUpperCase();  // Condition is in position 2
                        
                        // Skip invalid records
                        if (!conditionStr.equals("MUDDY") && !conditionStr.equals("DRY") && !conditionStr.equals("ICY")) {
                            System.err.println("Invalid track condition: " + conditionStr + " in record: " + line);
                            continue;
                        }
                        
                        Track.TrackCondition condition = Track.TrackCondition.valueOf(conditionStr);
                        long raceDuration = Long.parseLong(parts[6]);  // Duration is in position 6
                        double timeInSeconds = raceDuration / 1000.0;
                        
                        trackRecords.computeIfAbsent(trackName, k -> new HashMap<>());
                        trackRecords.get(trackName).put(condition, timeInSeconds);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error processing track record: " + line);
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading track records: " + e.getMessage());
        }
    }
    
    /**
     * Stores race statistics in CSV format
     */
    public static void storeRaceStats(String raceID, String horseName, double confidence, 
            int distanceTravelled, int position, long raceDuration, String trackName, 
            Track.TrackCondition trackCondition) {
        try {
            File statsFile = new File(RACES_CSV_FILE);
            boolean fileExists = statsFile.exists();
            
            try (FileWriter fw = new FileWriter(statsFile, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                
                if (!fileExists) {
                    out.println("raceID,name,symbol,confidence,distanceTravelled,position,time,trackName,trackCondition");
                }
                
                out.printf("%s,%s,%c,%.2f,%d,%d,%d,%s,%s%n",
                    raceID, horseName, horseName.charAt(0), confidence, distanceTravelled, position, 
                    raceDuration, trackName, trackCondition);
            }
        } catch (IOException e) {
            System.err.println("Error storing race statistics: " + e.getMessage());
        }
    }
    
    /**
     * Updates track records if the current race time is better
     */
    public static void updateTrackRecords(String trackName, Track.TrackCondition condition, 
            long raceDuration, String horseName) {
        // Store time in milliseconds
        double currentTime = raceDuration;
        
        // Load current track records
        List<Track> tracks = FileIO.loadTracks();
        Track targetTrack = null;
        
        // Find the target track
        for (Track track : tracks) {
            if (track.getName().equals(trackName)) {
                targetTrack = track;
                break;
            }
        }
        
        if (targetTrack != null) {
            // Update best time and horse if current time is better
            if (targetTrack.getBestTime() == 0 || currentTime < targetTrack.getBestTime()) {
                targetTrack.setBestTime(currentTime);
                targetTrack.setBestHorse(horseName);
                
                // Save updated track records
                FileIO.saveTracks(tracks);
            }
        }
    }
    
    /**
     * Gets horse statistics including performance metrics
     */
    public static Map<String, Object> getHorseStats(String horseName) {
        Map<String, Object> stats = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(RACES_CSV_FILE))) {
            String line;
            reader.readLine(); // Skip header
            
            int totalRaces = 0;
            int wins = 0;
            double totalConfidence = 0;
            List<Double> speeds = new ArrayList<>();
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[1].equals(horseName)) {
                    totalRaces++;
                    if (Math.round(Double.parseDouble(parts[5])) == 1) wins++;
                    
                    // Handle non-numeric confidence values
                    double confidence;
                    try {
                        confidence = Double.parseDouble(parts[3]);
                    } catch (NumberFormatException e) {
                        // If confidence is not a number, use a default value
                        confidence = 0.5; // Default to middle confidence
                    }
                    totalConfidence += confidence;
                    
                    // Calculate speed (distance/raceDuration) only for valid race times
                    int distance = Integer.parseInt(parts[4]);
                    long duration = Long.parseLong(parts[6]);
                    if (duration != -1) {  // Only calculate speed if the horse didn't fall
                        double speed = (double) distance / (duration / 1000.0); // units per second
                        speeds.add(speed);
                    }
                }
            }
            
            if (totalRaces > 0) {
                stats.put("totalRaces", totalRaces);
                stats.put("wins", wins);
                stats.put("winRate", (double) wins / totalRaces * 100);
                stats.put("avgConfidence", totalConfidence / totalRaces);
                stats.put("avgSpeed", speeds.stream().mapToDouble(Double::doubleValue).average().orElse(0));
                stats.put("bestSpeed", speeds.stream().mapToDouble(Double::doubleValue).max().orElse(0));
                stats.put("worstSpeed", speeds.stream().mapToDouble(Double::doubleValue).min().orElse(0));
            }
        } catch (IOException e) {
            System.err.println("Error getting horse statistics: " + e.getMessage());
        }
        return stats;
    }
    
    /**
     * Gets track records for a specific track
     */
    public static Map<Track.TrackCondition, Double> getTrackRecords(String trackName) {
        Map<Track.TrackCondition, Double> records = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(RACES_CSV_FILE))) {
            String line;
            reader.readLine(); // Skip header
            
            Map<Track.TrackCondition, List<Double>> timesByCondition = new HashMap<>();
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[6].equals(trackName)) {
                    Track.TrackCondition condition = Track.TrackCondition.valueOf(parts[7]);
                    long raceDuration = Long.parseLong(parts[5]);
                    double timeInSeconds = raceDuration / 1000.0;
                    
                    timesByCondition.computeIfAbsent(condition, k -> new ArrayList<>())
                                  .add(timeInSeconds);
                }
            }
            
            // Find best time for each condition
            for (Map.Entry<Track.TrackCondition, List<Double>> entry : timesByCondition.entrySet()) {
                double bestTime = entry.getValue().stream()
                    .mapToDouble(Double::doubleValue)
                    .min()
                    .orElse(0);
                records.put(entry.getKey(), bestTime);
            }
        } catch (IOException e) {
            System.err.println("Error getting track records: " + e.getMessage());
        }
        return records;
    }
    
    /**
     * Gets betting statistics
     */
    public static Map<String, Object> getBettingStats() {
        Map<String, Object> stats = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BETS_CSV_FILE))) {
            String line;
            reader.readLine(); // Skip header
            
            int totalBets = 0;
            int winningBets = 0;
            double totalBetAmount = 0;
            double totalWinnings = 0;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                totalBets++;
                totalBetAmount += Double.parseDouble(parts[5]);
                if (Boolean.parseBoolean(parts[6])) {
                    winningBets++;
                    totalWinnings += Double.parseDouble(parts[7]);
                }
            }
            
            if (totalBets > 0) {
                stats.put("totalBets", totalBets);
                stats.put("winningBets", winningBets);
                stats.put("winRate", (double) winningBets / totalBets * 100);
                stats.put("totalBetAmount", totalBetAmount);
                stats.put("totalWinnings", totalWinnings);
                stats.put("profitLoss", totalWinnings - totalBetAmount);
                stats.put("roi", ((totalWinnings - totalBetAmount) / totalBetAmount) * 100);
            }
        } catch (IOException e) {
            System.err.println("Error getting betting statistics: " + e.getMessage());
        }
        return stats;
    }
    
    /**
     * Gets recent race history for a horse
     */
    public static List<Map<String, Object>> getHorseRaceHistory(String horseName, int limit) {
        List<Map<String, Object>> history = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(RACES_CSV_FILE))) {
            String line;
            reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[1].equals(horseName)) {
                    Map<String, Object> race = new HashMap<>();
                    race.put("date", LocalDateTime.now().format(DATE_FORMATTER)); // Since we don't store date in CSV
                    race.put("track", "Unknown"); // Since we don't store track in CSV
                    race.put("condition", Track.TrackCondition.DRY); // Default condition
                    race.put("position", Math.round(Double.parseDouble(parts[5])));
                    race.put("distance", Integer.parseInt(parts[4]));
                    race.put("duration", Long.parseLong(parts[6]));
                    
                    // Handle non-numeric confidence values
                    double confidence;
                    try {
                        confidence = Double.parseDouble(parts[3]);
                    } catch (NumberFormatException e) {
                        confidence = 0.5; // Default to middle confidence
                    }
                    race.put("confidence", confidence);
                    
                    history.add(race);
                }
            }
            
            // Sort by date descending and limit results
            history.sort((a, b) -> b.get("date").toString().compareTo(a.get("date").toString()));
            if (limit > 0) {
                history = history.subList(0, Math.min(limit, history.size()));
            }
        } catch (IOException e) {
            System.err.println("Error getting horse race history: " + e.getMessage());
        }
        return history;
    }
    
    /**
     * Gets track performance statistics
     */
    public static Map<String, Object> getTrackStats(String trackName) {
        Map<String, Object> stats = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(RACES_CSV_FILE))) {
            String line;
            reader.readLine(); // Skip header
            
            Map<Track.TrackCondition, List<Double>> timesByCondition = new HashMap<>();
            int totalRaces = 0;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[6].equals(trackName)) {
                    totalRaces++;
                    Track.TrackCondition condition = Track.TrackCondition.valueOf(parts[7]);
                    long raceDuration = Long.parseLong(parts[5]);
                    double timeInSeconds = raceDuration / 1000.0;
                    
                    timesByCondition.computeIfAbsent(condition, k -> new ArrayList<>())
                                  .add(timeInSeconds);
                }
            }
            
            if (totalRaces > 0) {
                stats.put("totalRaces", totalRaces);
                
                // Calculate stats for each condition
                Map<Track.TrackCondition, Map<String, Double>> conditionStats = new HashMap<>();
                for (Map.Entry<Track.TrackCondition, List<Double>> entry : timesByCondition.entrySet()) {
                    List<Double> times = entry.getValue();
                    Map<String, Double> conditionStat = new HashMap<>();
                    conditionStat.put("bestTime", times.stream().mapToDouble(Double::doubleValue).min().orElse(0));
                    conditionStat.put("avgTime", times.stream().mapToDouble(Double::doubleValue).average().orElse(0));
                    conditionStat.put("worstTime", times.stream().mapToDouble(Double::doubleValue).max().orElse(0));
                    conditionStats.put(entry.getKey(), conditionStat);
                }
                stats.put("conditionStats", conditionStats);
            }
        } catch (IOException e) {
            System.err.println("Error getting track statistics: " + e.getMessage());
        }
        return stats;
    }
} 
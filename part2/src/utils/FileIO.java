package utils;

import models.Horse;
import models.Track;
import models.HorseItem;
import models.Transaction;
import models.Bet;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileIO {
    private static final String BASE_PATH = "src/data/";
    private static final String HORSE_CSV_FILE = BASE_PATH + "horses/horses.csv";
    private static final String TRANSACTION_CSV_FILE = BASE_PATH + "bets/transaction.csv";
    private static final String TRACKS_CSV_FILE = BASE_PATH + "tracks/tracks.csv";
    private static final String BREEDS_CSV_FILE = BASE_PATH + "horses/breeds.csv";
    private static final String COAT_COLORS_CSV_FILE = BASE_PATH + "horses/coat_colors.csv";
    private static final String EQUIPMENT_CSV_FILE = BASE_PATH + "horses/equipment.csv";
    private static final String ACCESSORIES_CSV_FILE = BASE_PATH + "horses/accessories.csv";
    private static final String RACES_CSV_FILE = BASE_PATH + "races.csv";
    private static final String BETS_CSV_FILE = BASE_PATH + "bets/bets.csv";
    
    private static final int BUFFER_SIZE = 8192;


    private static List<String[]> readCsvFile(String filePath, boolean skipHeader) {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath), BUFFER_SIZE)) {
            String line;
            if (skipHeader) br.readLine();
            while ((line = br.readLine()) != null) {
                records.add(parseCSVLine(line));
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
        }
        return records;
    }

    private static boolean writeCsvFile(String filePath, List<String> lines, String header) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, 
                                                               StandardOpenOption.TRUNCATE_EXISTING)) {
                if (header != null) {
                    writer.write(header);
                    writer.newLine();
                }
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error writing to " + filePath + ": " + e.getMessage());
            return false;
        }
    }

    private static String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : line.toCharArray()) {
            if (c == '"' || c == '\'') {
                inQuotes = !inQuotes;
                currentValue.append(c);
            } else if (c == ',' && !inQuotes) {
                values.add(currentValue.toString().trim());
                currentValue.setLength(0);
            } else {
                currentValue.append(c);
            }
        }
        values.add(currentValue.toString().trim());
        return values.toArray(new String[0]);
    }

    public static Horse[] ingestHorses() {
        List<String[]> horseData = readCsvFile(HORSE_CSV_FILE, true);
        List<Horse> horses = new ArrayList<>();
        
        for (String[] data : horseData) {
            if (data.length < 5) continue;
            
            String name = data[0].replace("\"", "");
            char symbol = data[1].charAt(1);
            double confidence = Double.parseDouble(data[2]);
            Horse horse = new Horse(symbol, name, confidence, data[3], data[4]);
            
            // Add equipment
            if (data.length > 5 && !data[5].isEmpty()) {
                String[] equipment = data[5].split(";");
                for (String itemName : equipment) {
                    HorseItem item = findEquipmentByName(itemName);
                    if (item != null) {
                        horse.addEquipment(item);
                    }
                }
            }
            
            // Add accessories
            if (data.length > 6 && !data[6].isEmpty()) {
                String[] accessories = data[6].split(";");
                for (String itemName : accessories) {
                    HorseItem item = findAccessoryByName(itemName);
                    if (item != null) {
                        horse.addAccessory(item);
                    }
                }
            }
            
            horses.add(horse);
        }
        
        return horses.toArray(new Horse[0]);
    }

    public static void updateHorseConfidence(String name, char symbol, double newConfidence) {
        List<String[]> horseData = readCsvFile(HORSE_CSV_FILE, true);
        List<String> updatedLines = new ArrayList<>();
        updatedLines.add("name,symbol,confidence,breed,coatColor,equipment,accessories");

        for (String[] data : horseData) {
            String horseName = data[0].replace("\"", "");
            char horseSymbol = data[1].charAt(1);
            
            if (horseName.equals(name) && horseSymbol == symbol) {
                data[2] = String.format("%.2f", newConfidence);
            }
            updatedLines.add(String.join(",", data));
        }
        writeCsvFile(HORSE_CSV_FILE, updatedLines, null);
    }

    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        List<String[]> data = readCsvFile(TRANSACTION_CSV_FILE, true);
        
        for (String[] parts : data) {
            if (parts.length == 4) {
                transactions.add(new Transaction(
                    parts[0],
                    parts[1],
                    parts[2],
                    Double.parseDouble(parts[3])
                ));
            }
        }
        return transactions;
    }

    public static boolean saveTransactions(List<Transaction> transactions) {
        List<String> lines = new ArrayList<>();
        for (Transaction transaction : transactions) {
            lines.add(transaction.toCsvString());
        }
        return writeCsvFile(TRANSACTION_CSV_FILE, lines, "Date,Time,Type,Amount");
    }

    public static List<Track> loadTracks() {
        List<Track> tracks = new ArrayList<>();
        List<String[]> data = readCsvFile(TRACKS_CSV_FILE, true);
        
        for (String[] parts : data) {
            if (parts.length >= 3) {
                Track track = new Track(
                    parts[0],
                    3,
                    Integer.parseInt(parts[1]),
                    Track.TrackShape.valueOf(parts[2]),
                    Track.TrackCondition.DRY
                );
                
                if (parts.length >= 4 && !parts[3].isEmpty()) {
                    track.setBestTime(Double.parseDouble(parts[3]));
                }
                if (parts.length >= 5 && !parts[4].isEmpty()) {
                    track.setBestHorse(parts[4]);
                }
                
                tracks.add(track);
            }
        }
        return tracks;
    }

    public static boolean saveTracks(List<Track> tracks) {
        List<String> lines = new ArrayList<>();
        for (Track track : tracks) {
            lines.add(String.format("%s,%d,%s,%.0f,%s",
                track.getName(),
                track.getLength(),
                track.getShape().name(),
                track.getBestTime(),
                track.getBestHorse()));
        }
        return writeCsvFile(TRACKS_CSV_FILE, lines, "Name,Length,Shape,BestTime,BestHorse");
    }

    public static List<String> loadBreeds() {
        List<String> breeds = new ArrayList<>();
        List<String[]> data = readCsvFile(BREEDS_CSV_FILE, false);
        if (!data.isEmpty()) {
            breeds.addAll(Arrays.asList(data.get(0)));
        }
        return breeds;
    }

    public static List<String> loadCoatColors() {
        List<String> colors = new ArrayList<>();
        List<String[]> data = readCsvFile(COAT_COLORS_CSV_FILE, true);
        for (String[] parts : data) {
            if (parts.length > 0) {
                colors.add(parts[0]);
            }
        }
        return colors;
    }

    public static List<HorseItem> loadEquipment() {
        List<HorseItem> equipment = new ArrayList<>();
        List<String[]> data = readCsvFile(EQUIPMENT_CSV_FILE, true);
        
        for (String[] parts : data) {
            if (parts.length >= 6) {
                equipment.add(new HorseItem(
                    parts[0],
                    parts[1],
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3]),
                    Double.parseDouble(parts[4]),
                    parts[5]
                ));
            }
        }
        return equipment;
    }

    public static List<HorseItem> loadAccessories() {
        List<HorseItem> accessories = new ArrayList<>();
        List<String[]> data = readCsvFile(ACCESSORIES_CSV_FILE, true);
        
        for (String[] parts : data) {
            if (parts.length >= 3) {
                accessories.add(new HorseItem(
                    parts[0],
                    parts[1],
                    parts[2]
                ));
            }
        }
        return accessories;
    }

    private static HorseItem findEquipmentByName(String name) {
        List<HorseItem> equipment = loadEquipment();
        for (HorseItem item : equipment) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }
    
    private static HorseItem findAccessoryByName(String name) {
        List<HorseItem> accessories = loadAccessories();
        for (HorseItem item : accessories) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public static void saveHorses(Horse[] horses) {
        List<String> lines = new ArrayList<>();
        for (Horse horse : horses) {
            String equipmentStr = String.join(";", horse.getEquipment().stream()
                .map(HorseItem::getName)
                .toArray(String[]::new));
            String accessoriesStr = String.join(";", horse.getAccessories().stream()
                .map(HorseItem::getName)
                .toArray(String[]::new));

            lines.add(String.format("%s,%s,%.2f,%s,%s,%s,%s",
                horse.getName(),
                horse.getSymbol(),
                horse.getConfidence(),
                horse.getBreed(),
                horse.getCoatColor(),
                equipmentStr,
                accessoriesStr
            ));
        }
        writeCsvFile(HORSE_CSV_FILE, lines, "name,symbol,confidence,breed,coatColor,equipment,accessories");
    }

    public static void storeRaceResult(String raceID, String horseName, char symbol, 
            double confidence, int distanceTravelled, int position, long raceDuration) {
        List<String> lines = new ArrayList<>();
        lines.add(String.format("%s,%s,%c,%.2f,%d,%d,%d", 
            raceID, horseName, symbol, confidence, distanceTravelled, position, raceDuration));
        
        Path path = Paths.get(RACES_CSV_FILE);
        if (!Files.exists(path)) {
            writeCsvFile(RACES_CSV_FILE, lines, "raceID,name,symbol,confidence,distanceTravelled,position,raceDuration");
        } else {
            try {
                Files.write(path, lines, StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println("Error storing race result: " + e.getMessage());
            }
        }
    }


    public static List<Bet> loadBets() {
        List<Bet> bets = new ArrayList<>();
        List<String[]> data = readCsvFile(BETS_CSV_FILE, true);
        
        for (String[] parts : data) {
            if (parts.length == 8) {
                Horse tempHorse = new Horse(
                    parts[4].charAt(0),  // horse symbol
                    parts[3],            // horse name
                    1.0,                 // default confidence
                    "",                  // empty breed
                    ""                   // empty coat color
                );

                Bet bet = new Bet(
                    parts[2],  // raceId
                    tempHorse,
                    Double.parseDouble(parts[5]) // amount
                );
                bet.setWon(Boolean.parseBoolean(parts[6]));
                bet.setWinnings(Double.parseDouble(parts[7]));
                
                bets.add(bet);
            }
        }
        return bets;
    }


    public static boolean saveBets(List<Bet> bets) {
        List<String> lines = new ArrayList<>();
        for (Bet bet : bets) {
            lines.add(bet.toCsvString());
        }
        return writeCsvFile(BETS_CSV_FILE, lines, "Date,Time,RaceID,HorseName,HorseSymbol,Amount,Won,Winnings");
    }


    public static Map<String, Object> getHorseStats(String horseName) {
        Map<String, Object> stats = new HashMap<>();
        List<String[]> data = readCsvFile(RACES_CSV_FILE, true);
        
        int totalRaces = 0;
        int wins = 0;
        double totalConfidence = 0;
        List<Double> speeds = new ArrayList<>();
        
        for (String[] parts : data) {
            if (parts[1].equals(horseName)) {
                totalRaces++;
                if (Math.round(Double.parseDouble(parts[5])) == 1) wins++;
                
                double confidence;
                try {
                    confidence = Double.parseDouble(parts[3]);
                } catch (NumberFormatException e) {
                    confidence = 0.5;
                }
                totalConfidence += confidence;
                
                int distance = Integer.parseInt(parts[4]);
                long duration = Long.parseLong(parts[6]);
                if (duration != -1) {
                    double speed = (double) distance / (duration / 1000.0);
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
        return stats;
    }

    public static void storeRaceStats(String raceID, String horseName, double confidence, 
            int distanceTravelled, int position, long raceDuration, String trackName, 
            Track.TrackCondition trackCondition) {
        List<String> lines = new ArrayList<>();
        lines.add(String.format("%s,%s,%c,%.2f,%d,%d,%d,%s,%s",
            raceID, horseName, horseName.charAt(0), confidence, distanceTravelled, position, 
            raceDuration, trackName, trackCondition));
        
        Path path = Paths.get(RACES_CSV_FILE);
        if (!Files.exists(path)) {
            writeCsvFile(RACES_CSV_FILE, lines, 
                "raceID,name,symbol,confidence,distanceTravelled,position,time,trackName,trackCondition");
        } else {
            try {
                Files.write(path, lines, StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println("Error storing race statistics: " + e.getMessage());
            }
        }
    }


    public static Map<String, Object> getBettingStats() {
        Map<String, Object> stats = new HashMap<>();
        List<String[]> data = readCsvFile(BETS_CSV_FILE, true);
        
        int totalBets = 0;
        int winningBets = 0;
        double totalBetAmount = 0;
        double totalWinnings = 0;
        
        for (String[] parts : data) {
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
        return stats;
    }
}

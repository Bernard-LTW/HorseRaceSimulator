package utils;

import models.Horse;
import models.Track;
import models.HorseItem;
import models.Transaction;
import models.Bet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.File;
import java.io.PrintWriter;

/**
 * Utility class for file input/output operations
 */
public class FileIO {  // Class names should start with capital letters
    // File paths
    private static final String HORSE_CSV_FILE = "part2/src/data/horses/horses.csv";
    private static final String TRANSACTION_CSV_FILE = "part2/src/data/bets/transaction.csv";
    private static final String TRACKS_CSV_FILE = "part2/src/data/tracks/tracks.csv";
    private static final String BREEDS_CSV_FILE = "part2/src/data/horses/breeds.csv";
    private static final String COAT_COLORS_CSV_FILE = "part2/src/data/horses/coat_colors.csv";
    private static final String EQUIPMENT_CSV_FILE = "part2/src/data/horses/equipment.csv";
    private static final String ACCESSORIES_CSV_FILE = "part2/src/data/horses/accessories.csv";
    private static final String RACES_CSV_FILE = "part2/src/data/races.csv";
    private static final String BETS_CSV_FILE = "part2/src/data/bets/bets.csv";


    /**
     * Reads horse data from CSV file and returns array of Horse objects
     * @return Array of Horse objects
     */
    public static Horse[] ingestHorses() {
        String line;
        List<Horse> horses = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(HORSE_CSV_FILE))) {
            // Skip the header line
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                // Split the CSV line but handle quoted values correctly
                String[] horseData = parseCSVLine(line);
                
                if (horseData.length < 5) {
                    System.err.println("Invalid horse data: " + line);
                    continue;
                }
                
                // Extract data
                String name = horseData[0].replace("\"", ""); // Remove quotes from name
                char symbol = horseData[1].charAt(1); // Get the character at index 1 (after the opening quote)
                double confidence = Double.parseDouble(horseData[2]);
                String breed = horseData[3];
                String coatColor = horseData[4];
                
                // Create Horse object
                Horse horse = new Horse(symbol, name, confidence, breed, coatColor);
                
                // Parse equipment and accessories if they exist
                if (horseData.length > 5 && !horseData[5].isEmpty()) {
                    String[] equipment = horseData[5].split(";");
                    for (String itemName : equipment) {
                        HorseItem item = findEquipmentByName(itemName);
                        if (item != null) {
                            horse.addEquipment(item);
                        }
                    }
                }
                
                if (horseData.length > 6 && !horseData[6].isEmpty()) {
                    String[] accessories = horseData[6].split(";");
                    for (String itemName : accessories) {
                        HorseItem item = findAccessoryByName(itemName);
                        if (item != null) {
                            horse.addAccessory(item);
                        }
                    }
                }
                
                // Set race statistics if they exist
                if (horseData.length > 7) {
                    horse.setRacesRun(Integer.parseInt(horseData[7]));
                }
                if (horseData.length > 8) {
                    horse.setRacesFinished(Integer.parseInt(horseData[8]));
                }
                if (horseData.length > 9) {
                    horse.setRacesWon(Integer.parseInt(horseData[9]));
                }
                
                horses.add(horse);
            }
        } catch (IOException e) {
            System.err.println("Error reading horse data: " + e.getMessage());
        }
        
        // Convert List to Array
        Horse[] result = new Horse[horses.size()];
        return horses.toArray(result);
    }
    
    /**
     * Updates a horse's confidence value in the CSV file
     * @param name The name of the horse
     * @param symbol The symbol of the horse
     * @param newConfidence The new confidence value (should be between 0.0 and 1.0)
     */
    public static void updateHorseConfidence(String name, char symbol, double newConfidence){
        if (newConfidence < 0.0 || newConfidence > 1.0) {
            System.err.println("Invalid confidence value. Must be between 0.0 and 1.0");
            return;
        }
        
        List<String> lines = new ArrayList<>();
        boolean horseFound = false;
        
        // Read all lines from the CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(HORSE_CSV_FILE))) {
            String line;
            // Add header line
            lines.add(br.readLine());
            
            while ((line = br.readLine()) != null) {
                String[] horseDdata = parseCSVLine(line);
                String horseName = horseDdata[0].replace("\"", "");
                char horseSymbol = horseDdata[1].charAt(1);
                
                // Check if this is the horse we want to update
                if (horseName.equals(name) && horseSymbol == symbol) {
                    // Format the new line with updated confidence
                    String updatedLine = "\"" + name + "\"," + horseDdata[1] + "," + newConfidence;
                    lines.add(updatedLine);
                    horseFound = true;
                } else {
                    lines.add(line);
                }
            }
            
            if (!horseFound) {
                System.err.println("Horse not found: " + name + " with symbol " + symbol);
                return;
            }
            
            // Write back to the file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(HORSE_CSV_FILE))) {
                for (String updatedLine : lines) {
                    bw.write(updatedLine);
                    bw.newLine();
                }
                System.out.println("Horse confidence updated: " + name + " -> " + newConfidence);
            }
            
        } catch (IOException e) {
            System.err.println("Error updating horse confidence: " + e.getMessage());
        }
    }
    
    /**
     * Parse CSV line handling quoted values
     * @param line CSV line to parse
     * @return Array of values
     */
    private static String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentValue = new StringBuilder();
        
        for (char c : line.toCharArray()) {
            if (c == '\"' || c == '\'') {
                inQuotes = !inQuotes;
                currentValue.append(c);
            } else if (c == ',' && !inQuotes) {
                values.add(currentValue.toString().trim());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        
        // Add the last value
        values.add(currentValue.toString().trim());
        
        return values.toArray(new String[0]);
    }

    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_CSV_FILE))) {
            String line;
            // Skip header line
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    transactions.add(new Transaction(
                        data[0],          // date
                        data[1],          // time
                        data[2],          // type
                        Double.parseDouble(data[3]) // amount
                    ));
                }
            }
        } catch (IOException e) {
            return null;
        }
        
        return transactions;
    }

    public static boolean saveTransactions(List<Transaction> transactions) {
        try {
            // Create the file if it doesn't exist
            File file = new File(TRANSACTION_CSV_FILE);
            boolean fileExists = file.exists();
            
            // Append the transactions to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                // Write header if file is new
                if (!fileExists) {
                    writer.write("Date,Time,Type,Amount");
                    writer.newLine();
                }
                
                // Write transactions
                for (Transaction transaction : transactions) {
                    writer.write(transaction.toCsvString());
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads track configurations from the CSV file.
     * @return List of Track objects.
     */
    public static List<Track> loadTracks() {
        List<Track> tracks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TRACKS_CSV_FILE))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {  // Skip header
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length == 3) {
                    String name = data[0];
                    int length = Integer.parseInt(data[1]);
                    Track.TrackShape shape = Track.TrackShape.valueOf(data[2].toUpperCase());
                    tracks.add(new Track(name, 3, length, shape, Track.TrackCondition.DRY));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading tracks: " + e.getMessage());
        }
        return tracks;
    }

    /**
     * Saves a track configuration to the CSV file.
     * @param track The Track object to save.
     */
    public static void saveTrack(Track track) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRACKS_CSV_FILE, true))) {
            writer.write(track.getLaneCount() + "," +
                         track.getLength() + "," +
                         track.getShape().name() + "," +
                         track.getCondition().name());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving track: " + e.getMessage());
        }
    }

    /**
     * Saves multiple track configurations to the CSV file.
     * @param tracks List of Track objects to save.
     * @return boolean indicating success
     */
    public static boolean saveTracks(List<Track> tracks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRACKS_CSV_FILE))) {
            // Write header
            writer.write("Name,Length,Shape");
            writer.newLine();
            
            for (Track track : tracks) {
                writer.write(String.format("%s,%d,%s",
                    track.getName(),
                    track.getLength(),
                    track.getShape().name()));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving tracks: " + e.getMessage());
            return false;
        }
    }

    public static List<String> loadBreeds() {
        List<String> breeds = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BREEDS_CSV_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                breeds.addAll(Arrays.asList(parts));
            }
        } catch (IOException e) {
            System.err.println("Error loading breeds: " + e.getMessage());
        }
        return breeds;
    }

    public static List<String> loadCoatColors() {
        List<String> colors = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(COAT_COLORS_CSV_FILE))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length > 0) {
                    colors.add(parts[0]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading coat colors: " + e.getMessage());
        }
        return colors;
    }

    public static List<HorseItem> loadEquipment() {
        List<HorseItem> equipment = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(EQUIPMENT_CSV_FILE))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length >= 6) {
                    equipment.add(new HorseItem(
                        parts[0], // name
                        parts[1], // type
                        Double.parseDouble(parts[2]), // speed modifier
                        Double.parseDouble(parts[3]), // endurance modifier
                        Double.parseDouble(parts[4]), // confidence modifier
                        parts[5]  // description
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading equipment: " + e.getMessage());
        }
        return equipment;
    }

    public static List<HorseItem> loadAccessories() {
        List<HorseItem> accessories = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCESSORIES_CSV_FILE))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length >= 3) {
                    accessories.add(new HorseItem(
                        parts[0], // name
                        parts[1], // type
                        parts[2]  // description
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading accessories: " + e.getMessage());
        }
        return accessories;
    }

    public static void saveHorses(Horse[] horses) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HORSE_CSV_FILE))) {
            writer.write("name,symbol,confidence,breed,coatColor,equipment,accessories,racesRun,racesFinished,racesWon");
            writer.newLine();
            for (Horse horse : horses) {
                // Get equipment and accessories as comma-separated lists
                String equipmentList = String.join(";", horse.getEquipment().stream()
                    .map(HorseItem::getName)
                    .toArray(String[]::new));
                String accessoriesList = String.join(";", horse.getAccessories().stream()
                    .map(HorseItem::getName)
                    .toArray(String[]::new));
                
                writer.write(String.format("\"%s\",'%c',%.2f,%s,%s,%s,%s,%d,%d,%d%n",
                    horse.getName(),
                    horse.getSymbol(),
                    horse.getConfidence(),
                    horse.getBreed(),
                    horse.getCoatColor(),
                    equipmentList,
                    accessoriesList,
                    horse.getRacesRun(),
                    horse.getRacesFinished(),
                    horse.getRacesWon()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error saving horses: " + e.getMessage());
        }
    }

    private static HorseItem findEquipmentByName(String name) {
        List<HorseItem> equipment = loadEquipment();
        return equipment.stream()
            .filter(item -> item.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
    
    private static HorseItem findAccessoryByName(String name) {
        List<HorseItem> accessories = loadAccessories();
        return accessories.stream()
            .filter(item -> item.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    public static void storeRaceResult(String raceID, String horseName, char symbol, 
            double confidence, int distanceTravelled, int position, long raceDuration) {
        try {
            // Create the races.csv file if it doesn't exist
            File racesFile = new File(RACES_CSV_FILE);
            boolean fileExists = racesFile.exists();
            
            // Append the race result to the file
            try (FileWriter fw = new FileWriter(racesFile, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                
                // Write header if file is new
                if (!fileExists) {
                    out.println("raceID,name,symbol,confidence,distanceTravelled,position,raceDuration");
                }
                
                // Write the race result
                out.printf("%s,%s,%c,%.2f,%d,%d,%d%n", 
                    raceID, horseName, symbol, confidence, distanceTravelled, position, raceDuration);
            }
        } catch (IOException e) {
            System.err.println("Error storing race result: " + e.getMessage());
        }
    }

    /**
     * Load bets from CSV file
     * @return List of Bet objects
     */
    public static List<Bet> loadBets() {
        List<Bet> bets = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(BETS_CSV_FILE))) {
            String line;
            // Skip header line
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] data = parseCSVLine(line);
                if (data.length == 8) {
                    // Create a temporary Horse object to pass to Bet constructor
                    Horse tempHorse = new Horse(
                        data[4].charAt(0),  // horse symbol
                        data[3],            // horse name
                        1.0,                // default confidence
                        "",                 // empty breed
                        ""                  // empty coat color
                    );
                    
                    Bet bet = new Bet(
                        data[2],  // raceId
                        tempHorse,
                        Double.parseDouble(data[5]) // amount
                    );
                    bet.setWon(Boolean.parseBoolean(data[6]));
                    bet.setWinnings(Double.parseDouble(data[7]));
                    
                    bets.add(bet);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading bets: " + e.getMessage());
        }
        
        return bets;
    }
    
    /**
     * Save bets to CSV file
     * @param bets List of Bet objects to save
     * @return true if successful, false otherwise
     */
    public static boolean saveBets(List<Bet> bets) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BETS_CSV_FILE))) {
            // Write header
            writer.write("Date,Time,RaceID,HorseName,HorseSymbol,Amount,Won,Winnings");
            writer.newLine();
            
            // Write bets
            for (Bet bet : bets) {
                writer.write(bet.toCsvString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving bets: " + e.getMessage());
            return false;
        }
    }
}

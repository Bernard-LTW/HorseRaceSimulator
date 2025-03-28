package utils;

import models.Horse;  // Importing Horse class from models package
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import models.Transaction;

/**
 * Utility class for file input/output operations
 */
public class FileIO {  // Class names should start with capital letters
    // File paths
    private static final String HORSE_CSV_FILE = "part2/src/data/horses.csv";
    private static final String TRANSACTION_CSV_FILE = "part2/src/data/transaction.csv";


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
                String[] horseDdata = parseCSVLine(line);
                
                // Extract data
                String name = horseDdata[0].replace("\"", ""); // Remove quotes from name
                char symbol = horseDdata[1].charAt(1); // Get the character at index 1 (after the opening quote)
                double confidence = Double.parseDouble(horseDdata[2]);
                
                // Create Horse object and add to list
                Horse horse = new Horse(symbol, name, confidence);
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTION_CSV_FILE))) {
            // Write header
            writer.write("Date,Time,Type,Amount");
            writer.newLine();
            
            // Write transactions
            for (Transaction transaction : transactions) {
                writer.write(transaction.toCsvString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

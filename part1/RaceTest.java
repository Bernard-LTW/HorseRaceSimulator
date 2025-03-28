public class RaceTest {
    public static void main(String[] args) {
        // Create horse objects with different symbols, names, and confidence levels
        Horse horse1 = new Horse('♞', "Thunder", 0.5);
        Horse horse2 = new Horse('♘', "Lightning", 0.6);
        Horse horse3 = new Horse('⏩', "Speedy", 0.7);
        Horse horse4 = new Horse('0', "Yooooo", 0.1);

        // Create a race with a specific length (e.g., 30 units)
        Race horseRace = new Race(30,4);
        
        // Add horses to lanes
        horseRace.addHorse(horse1, 1);
        horseRace.addHorse(horse2, 2);
        horseRace.addHorse(horse3, 3);
        horseRace.addHorse(horse4, 4);


        
        // Start the race
        horseRace.startRace();
    }
}
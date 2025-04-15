public class RaceTest {
    public static void main(String[] args) {
        Horse horse1 = new Horse('♞', "Thunder", 0.5);
        Horse horse2 = new Horse('♘', "Lightning", 0.6);
        Horse horse3 = new Horse('⏩', "Speedy", 0.7);
        Horse horse4 = new Horse('0', "Yooooo", 0.1);

        Race horseRace = new Race(10,4);
        
        horseRace.addHorse(horse1, 1);
        horseRace.addHorse(horse2, 2);
        horseRace.addHorse(horse3, 3);
        horseRace.addHorse(horse4, 4);


        horseRace.startRace();
    }
}
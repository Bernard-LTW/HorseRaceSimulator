public class HorseTest {
    public static void main(String[] args) {
        // Test 1: Basic initialization
        System.out.println("Test 1: Basic initialization");
        Horse horse1 = new Horse('♞', "Thunder", 0.8);
        System.out.println("Symbol: " + horse1.getSymbol() + " (Expected: ♞)");
        System.out.println("Name: " + horse1.getName() + " (Expected: Thunder)");
        System.out.println("Confidence: " + horse1.getConfidence() + " (Expected: 0.8)");
        System.out.println("Position: " + horse1.getDistanceTravelled() + " (Expected: 0)");
        System.out.println("Has fallen: " + horse1.hasFallen() + " (Expected: false)");
        System.out.println();

        // Test 2: Invalid confidence values
        System.out.println("Test 2: Invalid confidence values");
        Horse tooHighConfidence = new Horse('♘', "Overconfident", 1.5);
        System.out.println("Too high confidence: " + tooHighConfidence.getConfidence() + " (Expected: 1.0)");
        
        Horse tooLowConfidence = new Horse('♘', "Underconfident", -0.5);
        System.out.println("Too low confidence: " + tooLowConfidence.getConfidence() + " (Expected: 0.0)");
        System.out.println();

        // Test 3: Moving the horse
        System.out.println("Test 3: Moving the horse");
        Horse horse2 = new Horse('♘', "Lightning", 1.0); // 100% confidence to ensure it moves
        System.out.println("Initial position: " + horse2.getDistanceTravelled());
        horse2.moveForward();
        System.out.println("After move 1: " + horse2.getDistanceTravelled() + " (Expected: 1)");
        horse2.moveForward();
        System.out.println("After move 2: " + horse2.getDistanceTravelled() + " (Expected: 2)");
        System.out.println();

        // Test 4: Horse falling
        System.out.println("Test 4: Horse falling");
        Horse horse3 = new Horse('⏩', "Clumsy", 0.0); 
        System.out.println("Initial fallen state: " + horse3.hasFallen() + " (Expected: false)");
        horse3.moveForward();
        horse3.moveForward();
        horse3.fall();
        System.out.println("After running fall(), fallen state: " + horse3.hasFallen() + " (Expected: true)");
        System.out.println("After fallen, distance travelled is: " + horse3.getDistanceTravelled() + " (Expected: 2)");
        horse3.moveForward();
        horse3.moveForward();
        horse3.moveForward(); // Should not move when fallen
        System.out.println("Position after attempted move while fallen: " + horse3.getDistanceTravelled() + 
                          " (Expected: 2)");
        System.out.println();
    }
}
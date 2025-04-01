# PART ONE

## Setup Instructions
To run:
```bash
javac *.java

java -cp part1 HorseTest

java -cp part1 RaceTest
```


## Part 1 Testing Output Example
```
Test 1: Basic initialization
Symbol: ♞ (Expected: ♞)
Name: Thunder (Expected: Thunder)
Confidence: 0.8 (Expected: 0.8)
Position: 0 (Expected: 0)
Has fallen: false (Expected: false)

Test 2: Invalid confidence values
Too high confidence: 0.0 (Expected: 1.0)
Too low confidence: 0.0 (Expected: 0.0)

Test 3: Moving the horse
Initial position: 0
After move 1: 1 (Expected: 1)
After move 2: 2 (Expected: 2)

Test 4: Horse falling
Initial fallen state: false (Expected: false)
After running fall(), fallen state: true (Expected: true)
After fallen, distance travelled is: 2 (Expected: 2)
Position after attempted move while fallen: 2 (Expected: 2)
```

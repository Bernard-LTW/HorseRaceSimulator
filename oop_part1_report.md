# OOP Horse Racing Simulator Part 1 Report

Author: Tsz Wo Bernard Lee(240340049)

## Task 1: Write the Horse Class according to specifications

### Encapsulation Explaination

Encapsulation refers a concept in Object-Oriented Programming where the data and the methods that manipulate them are binded to together in a single object. It's a way to hide the implementation details of an obejct from the user, while still allowing access to its necessary functionallity. 

In Java, all fields of a class is accessible from outside the object itself unless otherwise specified. To achieve this, we use the `private` keyword to signify that the fields of a class cannot be accessed from outside methods. We use this to prefix all our field definitions in the `Horse` class:

```java
private final String horseName;
private char horseSymbol;
private int distanceTravelled=0;
private double horseConfidence=0.0;
private boolean fallen=false;
```

This makes sure that the value of all fields can only be accessed and modiffied from method specificially defined inside the class. To access and modify the data, we need getter and setter methods. Getter methods refer to methods that return a value of a field of a class. For example, let's say if we want to see if a horse has fallen or not. Instead of leaving the field `public` and using `objectName.fallen` to access the field directly from outside of the class, we create a method within the `Horse` class called `hasFallen()` to  return a boolean value of whether from the `fallen` field of the object. The getter methods in my `Horse` class include:

```java
//Accesses confidence number of Horse object
public double getConfidence() { return this.horseConfidence; }

//Accesses distanced travelled value of Horse object
public int getDistanceTravelled() { return this.distanceTravelled; }

//Accesses name of Horse object
public String getName() { return this.horseName; }

//Accesses symbol of Horse object
public char getSymbol() { return this.horseSymbol; }

//Accesses fallen value of Horse object
public boolean hasFallen() { return this.fallen; }

```

In the same way, setter method provide an interface where the user can manipulate the values stored in fields of the object. They often take in a value as parameter and have a return type `void` as Java uses pass-by-reference for objects. The setter methods in my `Horse` class include:

```java
//Increments the distanceTravelled value by 1
public void moveForward() { this.distanceTravelled++;}

//Sets a new confidence level value
public void setConfidence(double newConfidence){ ... }
    
//Sets a new symbol for the Horse Object
public void setSymbol(char newSymbol) { this.horseSymbol=newSymbol; }
```

Using setter methods has an advantage over letting the user modify the data in the object using the dot notation as it allows for constraints and input validation to be implemented and forces the user to follow these rules to interact with the object. One example of this can be seen in the `setConfidence(double newConfidence)` method, where according to the design specifications the value cannot exceed the allowed bounds(0 to 1). This can be easily implemented with an if statement:

```java
public void setConfidence(double newConfidence)
{
    if(newConfidence>=0.0 && newConfidence<=1.0){
        this.horseConfidence=newConfidence;
    }
}
```

This ensures that the value that is set would never exceed the allowed bounds(i.e no Horse would ever have a confidence higher than 1 or lower than 0)

Essentially, encapsulation allows for more secure and robust code as the how the other parts of the program interacts with this obejct is strictly defined by the developer of this objects and these boundaries must be obeyed for the user to manipulate with the object itself, reducing errors. It also improves modularity and code reusability of the code as it is self-contianed and doesn't need to rely on external dependencies.

### Testing Evidence

Include screenshots of your tests with detailed explanations. Describe the tests you conducted to verify the correctness of each method, such as testing the moveForward() and fall() methods, or ensuring that confidence cannot exceed the allowed bounds (0 to 1).



## Task 2: Improve the Race class

• List identified issues and explain them.

• Provide updated code for your Race class with explanations of changes.
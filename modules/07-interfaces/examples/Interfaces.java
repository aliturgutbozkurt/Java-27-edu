// An interface is a contract: a list of what a type can do, with no statement
// about what it is.
//
// A class may extend exactly ONE class and implement ANY NUMBER of interfaces.
// That asymmetry is the main reason interfaces exist.

public class Interfaces {

    public static void main(String[] args) {
        // Each of these is several things at once.
        Duck duck = new Duck();
        Aeroplane plane = new Aeroplane();
        Penguin penguin = new Penguin();

        // A method can ask for the CAPABILITY it needs rather than a class.
        // launch() works on anything that flies, now or in the future.
        launch(duck);
        launch(plane);
        // launch(penguin);   // will not compile: Penguin does not implement Flies

        System.out.println();

        // The same objects, viewed through a different capability.
        makeNoise(duck);
        makeNoise(penguin);

        System.out.println();

        // A duck satisfies three unrelated contracts. With single inheritance
        // alone this would be impossible: you would have to choose one parent
        // and simulate the rest.
        System.out.println("duck flies:  " + (duck instanceof Flies));
        System.out.println("duck swims:  " + (duck instanceof Swims));
        System.out.println("duck speaks: " + (duck instanceof Speaks));
    }

    // Accept the narrowest capability that does the job. This method does not
    // care what the object IS, only that it can fly.
    private static void launch(Flies thing) {
        System.out.println("launching: " + thing.fly());
    }

    private static void makeNoise(Speaks thing) {
        System.out.println("noise: " + thing.speak());
    }
}

// Methods in an interface are implicitly public and abstract. Writing those
// keywords is legal and redundant, so nobody does.
interface Flies {
    String fly();
}

interface Swims {
    String swim();
}

interface Speaks {
    String speak();
}

class Duck implements Flies, Swims, Speaks {

    @Override
    public String fly() {
        return "duck flapping";
    }

    @Override
    public String swim() {
        return "duck paddling";
    }

    @Override
    public String speak() {
        return "quack";
    }
}

class Aeroplane implements Flies {

    @Override
    public String fly() {
        return "jet engines";
    }
}

class Penguin implements Swims, Speaks {

    @Override
    public String swim() {
        return "penguin torpedoing";
    }

    @Override
    public String speak() {
        return "squawk";
    }
}

// INTERFACE OR ABSTRACT CLASS?
//
//                          interface        abstract class
//   how many per class     any number       exactly one
//   instance fields        no               yes
//   constructors           no               yes
//   state                  no               yes
//
// Ask what you are modelling:
//
//   "can do"  ->  interface.   Flies, Comparable, AutoCloseable.
//   "is a"    ->  abstract class, and only when subclasses genuinely share
//                 state and construction logic.
//
// When both fit, prefer the interface. It leaves the implementer's single
// inheritance slot free, and that slot is spent easily and regretted later.

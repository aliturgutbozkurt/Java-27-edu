// Constructors: how an object comes into existence in a valid state.

public class Constructors {

    public static void main(String[] args) {
        // Every constructor, doing the same job with different inputs.
        System.out.println(new Temperature());
        System.out.println(new Temperature(25.0));
        System.out.println(Temperature.fromFahrenheit(77.0));

        // Field defaults, which exist only for fields and never for locals.
        System.out.println(new Defaults());
    }
}

class Temperature {

    private final double celsius;

    // THE NO-ARGUMENT CONSTRUCTOR.
    //
    // Java writes one for you only if you declare NO constructors at all. The
    // moment you write any constructor, the free one disappears. That is why
    // adding a constructor to an existing class can break code that called
    // `new Thing()`.
    //
    // this(...) delegates to another constructor. It must be the first
    // statement, or near enough: see FlexibleConstructorBodies.java for what
    // JDK 25 relaxed.
    Temperature() {
        this(0.0);
    }

    Temperature(double celsius) {
        // `this.celsius` is the field, plain `celsius` is the parameter. When
        // the names collide, `this.` is how you tell them apart, and shadowing
        // the field on purpose like this is the normal convention.
        this.celsius = celsius;
    }

    // A STATIC FACTORY METHOD. Often better than a constructor because it has a
    // name, so two factories taking a double can be told apart:
    //
    //     Temperature.fromFahrenheit(77.0)
    //     Temperature.fromKelvin(298.15)
    //
    // Two constructors both taking a single double could not coexist at all.
    // A factory can also return a cached instance or a subclass; a constructor
    // always builds something new.
    static Temperature fromFahrenheit(double f) {
        return new Temperature((f - 32) * 5 / 9);
    }

    @Override
    public String toString() {
        return String.format("%.1f°C", celsius);
    }
}

class Defaults {

    // FIELDS get default values automatically.
    private int number;           // 0
    private boolean flag;         // false
    private String text;          // null
    private double amount;        // 0.0

    // LOCAL VARIABLES do not. This would not compile:
    //
    //     void method() {
    //         int x;
    //         System.out.println(x);   // error: variable x might not have been initialized
    //     }
    //
    // The asymmetry is deliberate. A field might legitimately be set later by
    // some other method, so the compiler cannot prove anything. A local variable
    // is used a few lines from where it is declared, so the compiler can check,
    // and does.

    @Override
    public String toString() {
        return "number=" + number + " flag=" + flag + " text=" + text + " amount=" + amount;
    }
}

// `sealed` is the middle ground between `final` and wide open.
//
//   final       nobody may extend this
//   sealed      only these named types may extend this
//   (neither)   anybody, anywhere, forever
//
// Module 06 argued for making classes final by default, because a class not
// designed for extension usually cannot be extended safely. Sealed types are
// the answer when you DO want subtypes, but a known, fixed set of them.

public class SealedTypes {

    public static void main(String[] args) {
        Shape[] shapes = {
            new Circle(2),
            new Rectangle(3, 4),
            new Triangle(3, 4, 5)
        };

        for (Shape s : shapes) {
            System.out.printf("%-12s area %6.2f%n", s.getClass().getSimpleName(), area(s));
        }

        System.out.println();
        System.out.println("total: " + String.format("%.2f", total(shapes)));
    }

    // NO DEFAULT BRANCH, and this compiles.
    //
    // The compiler knows Shape has exactly three permitted subtypes, so it can
    // prove this switch covers everything. That proof is the entire payoff of
    // sealing.
    //
    // Add a fourth shape and this method stops compiling, naming the case you
    // did not handle. A `default` branch would have quietly routed the new
    // shape to a fallback and produced a wrong number instead of an error.
    static double area(Shape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.width() * r.height();
            case Triangle t -> {
                double s = (t.a() + t.b() + t.c()) / 2;
                yield Math.sqrt(s * (s - t.a()) * (s - t.b()) * (s - t.c()));
            }
        };
    }

    static double total(Shape[] shapes) {
        double sum = 0;
        for (Shape s : shapes) {
            sum += area(s);
        }
        return sum;
    }
}

// `permits` lists exactly who may implement this. Anyone else gets:
//
//     error: class is not allowed to extend sealed class: Shape
//            (as it is not listed in its 'permits' clause)
//
// If the permitted types live in the same file, you may omit `permits`
// entirely and the compiler infers it. Being explicit is clearer.
sealed interface Shape permits Circle, Rectangle, Triangle {
}

// EVERY PERMITTED SUBTYPE MUST DECLARE ITS OWN INTENT. One of:
//
//   final        the hierarchy stops here
//   sealed       it continues, but only to a named set
//   non-sealed   it is open again from this point down
//
// Leaving all three off is a compile error:
//
//     error: sealed, non-sealed or final modifiers expected
//
// Records are implicitly final, which is why these three need no modifier.
// That pairing is deliberate: records plus sealed interfaces is the shape most
// modern Java data modelling takes.
record Circle(double radius) implements Shape {
}

record Rectangle(double width, double height) implements Shape {
}

record Triangle(double a, double b, double c) implements Shape {
}

// WHEN TO SEAL:
//
// When the set of cases is part of the design, not an extension point. A
// payment can be a card, a transfer or a voucher. An HTTP response is a
// success, a redirect or a failure. A parse either succeeded or it did not.
//
// In all of those, "someone might add another one later" is not a feature you
// want, it is a risk you want the compiler to catch.
//
// WHEN NOT TO:
//
// When you are writing a library and third parties SHOULD be able to plug in
// their own implementations. Sealing a plugin interface is how you make a
// library nobody can extend.

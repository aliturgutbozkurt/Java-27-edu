// Record patterns take the components apart in the pattern itself.
//
// This is where sealed interfaces, records and switch stop being three separate
// features and start being one way of writing code.

public class RecordPatterns {

    public static void main(String[] args) {
        Shape[] shapes = {
            new Circle(new Point(0, 0), 5),
            new Rectangle(new Point(0, 0), new Point(4, 4)),
            new Rectangle(new Point(0, 0), new Point(6, 3))
        };

        for (Shape s : shapes) {
            System.out.println(describe(s));
        }

        System.out.println();

        // Nested patterns go as deep as the data does.
        Object event = new Click(new Point(12, 40), Button.RIGHT);
        System.out.println(handle(event));
        System.out.println(handle(new Click(new Point(0, 0), Button.LEFT)));
        System.out.println(handle("not an event"));
    }

    static String describe(Shape shape) {
        return switch (shape) {
            // DECONSTRUCTION. `Circle(Point(var x, var y), var r)` matches the
            // type AND pulls out the pieces, two levels down, in one line.
            //
            // Compare with what this replaces:
            //
            //     case Circle c -> {
            //         Point centre = c.centre();
            //         double x = centre.x();
            //         ...
            //     }
            case Circle(Point(var x, var y), var r) when x == 0 && y == 0 ->
                    "circle of radius " + r + " at the origin";

            case Circle(Point p, var r) ->
                    "circle of radius " + r + " centred on " + p;

            // A guard comparing two destructured values. Detecting a square
            // needs no method on Rectangle at all.
            case Rectangle(Point(var x1, var y1), Point(var x2, var y2))
                    when (x2 - x1) == (y2 - y1) ->
                    "a square of side " + (x2 - x1);

            case Rectangle(Point(var x1, var y1), Point(var x2, var y2)) ->
                    "a " + (x2 - x1) + " by " + (y2 - y1) + " rectangle";
        };
    }

    static String handle(Object event) {
        return switch (event) {
            // `var` infers each component's type. You may also write the type
            // explicitly, which reads better when it is not obvious.
            case Click(Point(int x, int y), Button b) when b == Button.RIGHT ->
                    "context menu at " + x + "," + y;

            case Click(Point p, Button b) ->
                    b + " click at " + p;

            case null -> "no event";

            default -> "unrecognised: " + event;
        };
    }
}

sealed interface Shape permits Circle, Rectangle {
}

record Point(int x, int y) {
}

record Circle(Point centre, double radius) implements Shape {
}

record Rectangle(Point topLeft, Point bottomRight) implements Shape {
}

enum Button { LEFT, RIGHT }

record Click(Point at, Button button) {
}

// WHY THIS COMBINATION MATTERS:
//
// Sealed interface + records + exhaustive switch gives you, in ordinary Java,
// what other languages call algebraic data types and pattern matching.
//
//   the sealed interface says   these are all the cases
//   the records say             this is the data each case carries
//   the exhaustive switch says  and here is what to do with each
//
// The compiler checks all three fit together. Add a case, and every switch that
// needs updating fails to build and tells you where.

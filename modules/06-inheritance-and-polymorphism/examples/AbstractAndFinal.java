// abstract says "subclasses must fill this in".
// final says "nobody may change this".
//
// They are opposite instructions and both are about controlling what others can
// do to your design.

public class AbstractAndFinal {

    public static void main(String[] args) {
        // You cannot construct an abstract class:
        //
        //     new Shape();
        //     error: Shape is abstract; cannot be instantiated
        //
        // Which is the point. A Shape with no particular shape has no sensible
        // area, so the class refuses to exist on its own.

        Shape[] shapes = { new Circle(2.0), new Rectangle(3.0, 4.0) };

        for (Shape s : shapes) {
            System.out.printf("%-10s area %.2f   %s%n", s.name(), s.area(), s.summary());
        }

        System.out.println();
        System.out.println("Total area: " + String.format("%.2f", totalArea(shapes)));
    }

    // This method works on any Shape, including ones written years from now.
    // It needs no knowledge of circles or rectangles at all.
    private static double totalArea(Shape[] shapes) {
        double total = 0;
        for (Shape s : shapes) {
            total += s.area();
        }
        return total;
    }
}

abstract class Shape {

    // An abstract method has no body. Every concrete subclass must supply one,
    // and the compiler enforces it. Forget one and the subclass will not build.
    abstract double area();

    abstract String name();

    // An abstract class CAN have ordinary methods, which is the main thing
    // separating it from an interface. This one is written once and inherited
    // by everything.
    String summary() {
        return "a " + name() + " covering " + String.format("%.2f", area()) + " square units";
    }

    // `final` on a method means no subclass may override it.
    //
    // Use it when the method's behaviour is part of a guarantee the class makes.
    // Here: however a shape computes its area, "is it bigger than that one" must
    // always mean the same thing.
    final boolean isLargerThan(Shape other) {
        return this.area() > other.area();
    }
}

// `final` on a class means nobody may extend it at all.
//
// String is final. So is Integer. That is not the library being unhelpful: if
// anyone could subclass String and override equals, no code anywhere could
// trust a String again.
final class Circle extends Shape {

    private final double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }

    @Override
    String name() {
        return "circle";
    }
}

final class Rectangle extends Shape {

    private final double width;
    private final double height;

    Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    double area() {
        return width * height;
    }

    @Override
    String name() {
        return "rectangle";
    }
}

// WHEN TO MAKE A CLASS final:
//
// More often than you would think. A class that was never designed to be
// extended usually cannot be extended safely, because a subclass can override
// any method and break invariants the class depends on.
//
// Module 09 introduces `sealed`, which is the middle ground: a fixed list of
// permitted subclasses rather than all or nothing.

// Reference solution for Homework 09.
//
// An expression evaluator as a sealed hierarchy of records, with every
// operation written as one exhaustive switch and no default branch anywhere.

public class Calculator {

    public static void main(String[] args) {
        // (3 + 4) * -(2)
        Expr expression = new Multiply(
                new Add(new Literal(3), new Literal(4)),
                new Negate(new Literal(2)));

        System.out.println("expression: " + print(expression));
        System.out.println("evaluates:  " + evaluate(expression));
        System.out.println("nodes:      " + countNodes(expression));
        System.out.println("simplified: " + print(simplify(expression)));

        System.out.println();
        System.out.println("--- simplification rules firing ---");

        Expr[] cases = {
            new Add(new Literal(0), new Literal(7)),          // 0 + x  -> x
            new Add(new Literal(7), new Literal(0)),          // x + 0  -> x
            new Multiply(new Literal(1), new Literal(9)),     // 1 * x  -> x
            new Multiply(new Literal(0), new Literal(9)),     // 0 * x  -> 0
            new Negate(new Negate(new Literal(5))),           // --x    -> x
            new Add(new Literal(2), new Literal(3))           // folds to 5
        };

        for (Expr e : cases) {
            System.out.printf("  %-14s -> %-8s = %s%n", print(e), print(simplify(e)), evaluate(e));
        }

        System.out.println();
        System.out.println("--- division by zero ---");
        System.out.println("  " + describeResult(evaluateSafely(
                new Divide(new Literal(1), new Literal(0)))));
        System.out.println("  " + describeResult(evaluateSafely(
                new Divide(new Literal(10), new Literal(4)))));
    }

    // ---------------------------------------------------------------- evaluate

    // No default branch. If someone adds a Power node to the permits clause,
    // this method stops compiling and names Power as the missing pattern. That
    // is the entire reason the hierarchy is sealed.
    static int evaluate(Expr expr) {
        return switch (expr) {
            case Literal(int value) -> value;
            case Add(Expr left, Expr right) -> evaluate(left) + evaluate(right);
            case Multiply(Expr left, Expr right) -> evaluate(left) * evaluate(right);
            case Negate(Expr inner) -> -evaluate(inner);
            case Divide(Expr left, Expr right) -> evaluate(left) / evaluate(right);
        };
    }

    // ---------------------------------------------------------------- print

    static String print(Expr expr) {
        return switch (expr) {
            case Literal(int value) -> String.valueOf(value);
            case Add(Expr left, Expr right) -> "(" + print(left) + " + " + print(right) + ")";
            case Multiply(Expr left, Expr right) -> "(" + print(left) + " * " + print(right) + ")";
            case Negate(Expr inner) -> "-" + print(inner);
            case Divide(Expr left, Expr right) -> "(" + print(left) + " / " + print(right) + ")";
        };
    }

    // ---------------------------------------------------------------- count

    static int countNodes(Expr expr) {
        return switch (expr) {
            case Literal ignored -> 1;
            case Negate(Expr inner) -> 1 + countNodes(inner);
            case Add(Expr left, Expr right) -> 1 + countNodes(left) + countNodes(right);
            case Multiply(Expr left, Expr right) -> 1 + countNodes(left) + countNodes(right);
            case Divide(Expr left, Expr right) -> 1 + countNodes(left) + countNodes(right);
        };
    }

    // ---------------------------------------------------------------- simplify

    // Guards do the work here. Each rule is one case label, and the order
    // matters: every guarded case must come before the unguarded case for the
    // same type, or the unguarded one swallows it.
    static Expr simplify(Expr expr) {
        return switch (expr) {
            case Literal l -> l;

            // --x -> x, seen by destructuring two levels in one pattern.
            case Negate(Negate(Expr inner)) -> simplify(inner);
            case Negate(Expr inner) -> new Negate(simplify(inner));

            // Identity and absorbing elements, matched on the literal's value.
            case Add(Literal(int zero), Expr right) when zero == 0 -> simplify(right);
            case Add(Expr left, Literal(int zero)) when zero == 0 -> simplify(left);

            // Constant folding.
            case Add(Literal(int a), Literal(int b)) -> new Literal(a + b);
            case Add(Expr left, Expr right) -> new Add(simplify(left), simplify(right));

            case Multiply(Literal(int zero), Expr ignored) when zero == 0 -> new Literal(0);
            case Multiply(Expr ignored, Literal(int zero)) when zero == 0 -> new Literal(0);
            case Multiply(Literal(int one), Expr right) when one == 1 -> simplify(right);
            case Multiply(Expr left, Literal(int one)) when one == 1 -> simplify(left);
            case Multiply(Literal(int a), Literal(int b)) -> new Literal(a * b);
            case Multiply(Expr left, Expr right) -> new Multiply(simplify(left), simplify(right));

            case Divide(Expr left, Expr right) -> new Divide(simplify(left), simplify(right));
        };
    }

    // ---------------------------------------------------------------- results

    // A second sealed hierarchy, this time modelling success or failure without
    // exceptions. Module 10 covers exceptions properly; this shows the
    // alternative that sealed types make comfortable.
    static Result evaluateSafely(Expr expr) {
        return switch (expr) {
            case Divide(Expr ignored, Literal(int zero)) when zero == 0 ->
                    new Failure("division by zero");
            case Divide(Expr left, Expr right) -> {
                Result l = evaluateSafely(left);
                Result r = evaluateSafely(right);
                yield switch (l) {
                    case Failure f -> f;
                    case Success(int a) -> switch (r) {
                        case Failure f -> f;
                        case Success(int b) when b == 0 -> new Failure("division by zero");
                        case Success(int b) -> new Success(a / b);
                    };
                };
            }
            default -> new Success(evaluate(expr));
        };
    }

    static String describeResult(Result result) {
        return switch (result) {
            case Success(int value) -> "ok: " + value;
            case Failure(String reason) -> "failed: " + reason;
        };
    }
}

// ------------------------------------------------------------------ the model

// The sealed interface states the complete set of node shapes. Every record is
// implicitly final, so none of them needs an explicit modifier.
sealed interface Expr permits Literal, Add, Multiply, Negate, Divide {
}

record Literal(int value) implements Expr {
}

record Add(Expr left, Expr right) implements Expr {
}

record Multiply(Expr left, Expr right) implements Expr {
}

record Negate(Expr inner) implements Expr {
}

record Divide(Expr left, Expr right) implements Expr {
}

sealed interface Result permits Success, Failure {
}

record Success(int value) implements Result {
}

record Failure(String reason) implements Result {
}

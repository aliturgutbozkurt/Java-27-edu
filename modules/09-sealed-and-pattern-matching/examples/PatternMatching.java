// Pattern matching removes the oldest piece of noise in Java: test the type,
// cast to it, name it, use it.

public class PatternMatching {

    public static void main(String[] args) {
        Object[] things = { 42, "hello", 3.14, new int[]{1, 2, 3}, null, "" };

        for (Object o : things) {
            System.out.printf("%-12s -> %s%n", show(o), describe(o));
        }

        System.out.println();
        System.out.println("--- the same logic, written the old way ---");
        for (Object o : things) {
            System.out.printf("%-12s -> %s%n", show(o), describeOldWay(o));
        }
    }

    // THE MODERN FORM.
    //
    // `case String s` does three things at once: tests the type, casts, and
    // introduces `s` scoped to that branch only.
    //
    // `when` adds a guard. Guarded cases must come BEFORE the unguarded case
    // for the same type, because the first match wins and an unguarded
    // `case String s` would swallow everything.
    //
    // `case null` is explicit. Without it, a switch on a null throws
    // NullPointerException, which is the pre-existing behaviour kept for
    // compatibility. Listing it makes the intent visible.
    static String describe(Object o) {
        return switch (o) {
            case null -> "nothing at all";
            case Integer i when i > 100 -> "a big number, " + i;
            case Integer i -> "a number, " + i;
            case String s when s.isEmpty() -> "an empty string";
            case String s -> "text of length " + s.length();
            case Double d -> "a decimal, " + d;
            case int[] arr -> "an int array of " + arr.length;
            default -> "something else";
        };
    }

    // THE OLD FORM, for comparison. Every branch repeats the type name twice
    // and the cast is a separate act of faith.
    //
    // The cast is where bugs lived: nothing stops you writing
    // `if (o instanceof String) { Integer i = (Integer) o; }`, which compiles
    // and throws at runtime.
    static String describeOldWay(Object o) {
        if (o == null) {
            return "nothing at all";
        }
        if (o instanceof Integer) {
            Integer i = (Integer) o;
            return i > 100 ? "a big number, " + i : "a number, " + i;
        }
        if (o instanceof String) {
            String s = (String) o;
            return s.isEmpty() ? "an empty string" : "text of length " + s.length();
        }
        if (o instanceof Double) {
            Double d = (Double) o;
            return "a decimal, " + d;
        }
        if (o instanceof int[]) {
            int[] arr = (int[]) o;
            return "an int array of " + arr.length;
        }
        return "something else";
    }

    // instanceof PATTERNS work outside switch too, and this is where you will
    // use them most often.
    static String shorten(Object o) {
        // The pattern variable is in scope exactly where the test has passed.
        if (o instanceof String s && s.length() > 5) {
            return s.substring(0, 5) + "...";
        }
        // `s` does not exist here, and referring to it is a compile error.
        return String.valueOf(o);
    }

    // The scoping is smarter than it looks. Here the variable is in scope in
    // the ELSE branch, because the method can only continue past the return
    // when the pattern matched.
    static int lengthOf(Object o) {
        if (!(o instanceof String s)) {
            return -1;
        }
        return s.length();
    }

    static String show(Object o) {
        if (o instanceof int[] arr) {
            return "int[" + arr.length + "]";
        }
        return o == null ? "null" : (o instanceof String s ? "\"" + s + "\"" : o.toString());
    }
}

// Varargs let a method take any number of arguments. Inside, it is just an array.

void main() {
    IO.println("count()        = " + count());
    IO.println("count(1, 2, 3) = " + count(1, 2, 3));

    // Because the parameter IS an array, you can hand it one directly.
    int[] existing = {4, 5};
    IO.println("count(existing) = " + count(existing));

    IO.println(join("-", "2026", "09", "23"));

    // RULE 1: the varargs parameter must be last. Only one per method.
    //
    //     void bad(int... nums, String label)   // will not compile

    // RULE 2: calling with no arguments gives an EMPTY array, not null.
    // So this is safe without a null check:
    IO.println("empty call is length " + count());

    // RULE 3: passing an explicit null needs care.
    //
    // An untyped null is ambiguous: does it mean "one null element" or "the
    // whole array is null"? Java picks the second, and a cast makes it explicit.
    IO.println(describe((Object[]) null));      // the array itself is null
    IO.println(describe((Object) null));        // an array holding one null

    // This is why varargs methods that might receive null should say so, and
    // why library code usually checks for it even though an ordinary call can
    // never produce it.

    // A practical use: String.format and System.out.printf are both varargs,
    // which is why they accept any number of values.
    IO.println(String.format("%s scored %d out of %d", "Ada", 95, 100));
}

int count(int... numbers) {
    // numbers is an int[]. If the caller passed nothing, it has length 0.
    return numbers.length;
}

String join(String separator, String... parts) {
    // A non-varargs parameter can come first. Only the last one may vary.
    var sb = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
        if (i > 0) sb.append(separator);
        sb.append(parts[i]);
    }
    return sb.toString();
}

String describe(Object... parts) {
    if (parts == null) {
        return "the array itself was null";
    }
    return "an array of length " + parts.length;
}

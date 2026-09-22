// Overloading: several methods sharing a name, told apart by their parameters.
//
// The return type is NOT part of the signature. Two methods differing only in
// what they return will not compile, which surprises people.

void main() {
    // Straightforward cases.
    IO.println(describe(42));
    IO.println(describe("hello"));
    IO.println(describe(3.14));
    IO.println(describe(1, 2));

    // RESOLUTION ORDER. When no overload matches exactly, the compiler tries,
    // in this order:
    //
    //   1. widening      int -> long -> float -> double
    //   2. boxing        int -> Integer
    //   3. varargs       int -> int...
    //
    // It stops at the first one that works, and it decides at COMPILE time.
    IO.println(pick(1));     // "long", because widening beats boxing
    IO.println(choose(1));   // "Integer", because boxing beats varargs

    // That ordering exists for backward compatibility: widening and varargs
    // predate autoboxing, and changing the preference would have silently
    // altered the behaviour of code written before Java 5.

    // THE TRAP THIS CAUSES IN REAL CODE.
    //
    // List has both remove(int index) and remove(Object o). Watch:
    var numbers = new java.util.ArrayList<Integer>(java.util.List.of(10, 20, 30));

    numbers.remove(1);
    IO.println("after remove(1):                 " + numbers);
    // Removed the element AT INDEX 1, which was 20. Not the value 1.

    numbers.remove(Integer.valueOf(30));
    IO.println("after remove(Integer.valueOf(30)): " + numbers);
    // Removed the VALUE 30.

    // Same method name, same-looking argument, completely different meaning,
    // decided by the static type. This has caused real production bugs. When a
    // List holds Integers, always be explicit about which overload you want.

    // WHY RETURN TYPE IS NOT PART OF THE SIGNATURE:
    //
    //     int    parse(String s) { ... }
    //     double parse(String s) { ... }   // will not compile
    //
    // The compiler picks an overload from the arguments at the call site. In
    // `parse("1");` as a bare statement there is nothing to disambiguate from,
    // so the rule is simply that the parameters must differ.
}

String describe(int n)         { return "an int: " + n; }
String describe(String s)      { return "a String: " + s; }
String describe(double d)      { return "a double: " + d; }
String describe(int a, int b)  { return "two ints: " + a + ", " + b; }

String pick(long x)    { return "long"; }
String pick(Integer x) { return "Integer"; }

String choose(Integer x) { return "Integer"; }
String choose(int... x)  { return "varargs"; }

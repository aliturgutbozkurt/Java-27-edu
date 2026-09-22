// `var` lets the compiler work out the type from the right-hand side.
//
// Read this first, because it is the thing people get wrong: var is NOT dynamic
// typing. The variable still has one fixed type forever. You simply did not
// type it out. Everything is still checked at compile time.

void main() {
    // These two lines produce identical bytecode.
    String explicit = "hello";
    var inferred = "hello";          // inferred is a String, permanently

    IO.println(explicit + " " + inferred);

    // Proof that it is not dynamic: this does not compile.
    //
    //     var x = "hello";
    //     x = 42;          // error: incompatible types: int cannot be converted to String

    // WHERE var EARNS ITS KEEP: when the type is long and already obvious.
    //
    //   Before:
    //     Map<String, List<Integer>> scores = new HashMap<String, List<Integer>>();
    //   After:
    //     var scores = new HashMap<String, List<Integer>>();
    //
    // The type is written once instead of twice, and nothing was hidden.
    var scores = new java.util.HashMap<String, java.util.List<Integer>>();
    scores.put("ada", java.util.List.of(100, 98));
    IO.println(scores);

    // WHERE var HURTS: when the right-hand side does not say what you get.
    //
    //     var result = service.process(input);
    //
    // A reader now has to go and look up process() to know what result is. The
    // rule of thumb: if you can see the type at a glance on the same line, var
    // is fine. If you cannot, write it out for whoever reads this next.

    // Things var cannot do, all compile errors rather than surprises:
    //
    //   var a;            // nothing to infer from
    //   var b = null;     // null has no useful type
    //   var c = () -> 1;  // a lambda needs a target type, see Module 13
    //
    // It is also local-variable only. Fields, method parameters and return
    // types must still be written out, which keeps APIs readable.

    // var in a loop, where it reads well:
    var words = java.util.List.of("one", "two", "three");
    for (var word : words) {
        IO.print(word.length() + " ");
    }
    IO.println();
}

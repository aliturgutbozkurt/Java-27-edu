// EXPECT: runtime-error
//
// The second autoboxing bite, on its own because it crashes on purpose.
//
// Run it:  java modules/02-variables-and-types/examples/NullUnboxing.java
//
// This file is marked runtime-error, meaning the verification script expects it
// to compile and then fail. A crash you can reproduce on demand teaches more
// than a paragraph describing one.

void main() {
    java.util.Map<String, Integer> scores = new java.util.HashMap<>();
    scores.put("ada", 100);

    IO.println("Looking up a key that is not there...");

    // get() returns null for a missing key. Assigning to an int forces Java to
    // unbox it, which means calling intValue() on null.
    int missing = scores.get("grace");

    IO.println("You will never see this line: " + missing);
}

// What you get:
//
//     Exception in thread "main" java.lang.NullPointerException:
//       Cannot invoke "java.lang.Integer.intValue()" because the return value of
//       "java.util.Map.get(Object)" is null
//
// Read that message closely, because modern Java NPEs are unusually helpful.
// It names the method that returned null (Map.get) and the call that failed on
// it (Integer.intValue). Older Java would have said "NullPointerException" and
// left you to guess which of the five things on that line was null.
//
// The fix is to decide what a missing key means:
//
//     int missing = scores.getOrDefault("grace", 0);
//
// Module 15 covers Optional, which is Java's way of making "might be absent"
// visible in the type instead of hiding it behind a null.

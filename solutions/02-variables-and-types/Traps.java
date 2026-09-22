// Reference solution for Homework 02, part two.

void main() {
    // TRAP ONE: == on boxed integers.
    Integer small1 = 100, small2 = 100;
    Integer large1 = 1000, large2 = 1000;

    IO.println("100  == 100  : " + (small1 == small2));   // true
    IO.println("1000 == 1000 : " + (large1 == large2));   // false
    IO.println("1000 .equals : " + large1.equals(large2)); // true

    // Why, in my own words:
    //
    // == never promised to compare values. On any object it asks "are these two
    // names pointing at the same thing in memory?". Java happens to keep a
    // ready-made set of Integer objects for -128..127 and hands the same one
    // back every time, so both names point at one object and == says true. Ask
    // for 1000 and Java builds a new object each time, so the two names point
    // at different objects and == correctly says false.
    //
    // The trap is not the number 127. The trap is that == appeared to work,
    // which is far worse than it failing every time, because the bug only shows
    // up once real data pushes the values past the cache.

    IO.println();

    // TRAP TWO: String immutability.
    String greeting = "  hello  ";

    greeting.strip();   // the result is computed and immediately discarded
    IO.println("[" + greeting + "]");   // still "  hello  "

    String stripped = greeting.strip();
    IO.println("[" + stripped + "]");   // "hello"

    // Why, in my own words:
    //
    // A String object can never be modified after it is built. So strip() has
    // no way to change `greeting`, and it does not try. It builds a brand new
    // String and returns it. Calling it as a bare statement computes that new
    // String and then drops it on the floor.
    //
    // Nothing warns you, because "call a method and ignore the result" is
    // legal and often sensible. Java cannot tell the difference between that
    // and a mistake. The habit that saves you: if a String method's name
    // sounds like it changes something, it does not, so assign the result.
}

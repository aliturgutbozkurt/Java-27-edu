// EXPECT: compile-only
//
// This example waits for you to type something, so the automated verification
// script compiles it but does not run it. Run it yourself:
//
//     java modules/01-getting-started/examples/AskingForInput.java
//
// The marker on line 1 is how every example in this curriculum tells the
// verification script what to expect. You will see three others:
// compile-error, preview, and no marker at all.

void main() {
    // readln(prompt) prints the prompt and waits for a line. Coming from
    // Python's input() or Node's readline, this is the same idea with less setup.
    String name = IO.readln("What is your name? ");

    // Java concatenates String with + like most languages you know.
    IO.println("Hello, " + name + ".");

    // Everything arrives as text. If you want a number, you convert it, and the
    // conversion can fail. Module 10 covers what to do when it does; for now
    // just notice that Java makes you ask for the conversion explicitly rather
    // than guessing what you meant.
    String yearText = IO.readln("What year is it? ");
    int year = Integer.parseInt(yearText);

    IO.println("Next year is " + (year + 1) + ".");

    // A parenthesis trap worth meeting on day one:
    //   "Next year is " + year + 1     gives  "Next year is 20261"
    //   "Next year is " + (year + 1)   gives  "Next year is 2027"
    // The + operator works left to right. Once the left side is a String,
    // every + after it means "stick this on the end", not "add these numbers".
}

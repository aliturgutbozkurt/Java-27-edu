// EXPECT: compile-only
//
// Reference solution for Homework 01.
//
// It waits for typed input, so the verification script compiles it without
// running it. To try it:
//
//     java solutions/01-getting-started/Solution.java
//
// Try the homework yourself before reading this. Getting the error and fixing
// it teaches you more than reading a finished answer.

void main() {
    // Every answer arrives as text, no matter what the user types.
    String name = IO.readln("What is your name? ");
    String birthYearText = IO.readln("What year were you born? ");
    String currentYearText = IO.readln("What year is it now? ");

    // Java will not silently treat "1990" as a number, so we ask for the
    // conversion. Integer.parseInt throws if the text is not a number; Module 10
    // covers how to handle that properly. For now, typing letters here will
    // crash the program, and seeing that crash is worth doing once.
    int birthYear = Integer.parseInt(birthYearText);
    int currentYear = Integer.parseInt(currentYearText);

    int age = currentYear - birthYear;

    // println() with no argument prints a blank line.
    IO.println();
    IO.println("--- About " + name + " ---");
    IO.println("Name: " + name);

    // The parentheses here are not optional.
    //
    //   "Age this year: " + currentYear - birthYear
    //
    // does not compile at all. The + runs first and produces a String, and Java
    // will not subtract an int from a String. The error reads:
    //
    //   error: bad operand types for binary operator '-'
    //
    // Computing the value into `age` first, as we did above, sidesteps the
    // question entirely and reads better anyway.
    IO.println("Age this year: " + age);
}

// A real compiler error, produced by deleting the semicolon after the `age`
// assignment and running the file again:
//
//     Solution.java:26: error: ';' expected
//         int age = currentYear - birthYear
//                                          ^
//     1 error
//     error: compilation failed

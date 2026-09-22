// EXPECT: compile-error
//
// This file does not compile, and that is the point. Learning to read javac's
// output is a day-one skill, so here is a broken file to practise on.
//
// Run it and read what comes back:
//
//     java modules/01-getting-started/examples/ReadTheError.java
//
// You will see:
//
//     ReadTheError.java:29: error: ';' expected
//         IO.println("I am missing something")
//                                             ^
//     1 error
//     error: compilation failed
//
// Read it right to left, which is the opposite of how people usually try:
//
//   - the caret ^ points at the exact column
//   - :23 is the line number
//   - "';' expected" is what the compiler wanted to find there
//
// Java is not guessing. It read your code up to that caret, and at that spot
// the only legal thing was a semicolon. Fix the caret's position, not the
// whole line.

void main() {
    IO.println("I am missing something")
}

// Your first real skill in a new language is not writing code. It is reading
// the error the compiler hands back and knowing where to look. Compiler errors
// are blunt, but unlike runtime bugs they tell you the exact spot.

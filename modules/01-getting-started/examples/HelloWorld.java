// The smallest Java program that does something.
//
// If you have met Java before, you are probably bracing for this:
//
//     public class HelloWorld {
//         public static void main(String[] args) {
//             System.out.println("Hello, World!");
//         }
//     }
//
// Four keywords and a parameter you never use, just to print one line. That
// boilerplate was Java's most-mocked feature for about twenty-five years.
//
// It is gone now. JEP 512 landed in JDK 25 and this file is the whole program.
// No class declaration, no `static`, no `String[] args`, no import.

void main() {
    IO.println("Hello, World!");
}

// Run it:  java modules/01-getting-started/examples/HelloWorld.java
//
// Two things worth noticing before you move on:
//
// 1. You ran a .java file directly. No separate compile step, no build tool.
// 2. `IO` came from nowhere. It lives in java.lang, which Java imports into
//    every file automatically, so println is simply available.
//
// The boilerplate did not actually disappear, by the way. The compiler writes
// it for you now. WhatTheCompilerWrites.java shows you the proof.

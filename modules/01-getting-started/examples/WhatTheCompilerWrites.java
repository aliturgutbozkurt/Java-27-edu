// "Where did the class go?"
//
// It did not go anywhere. A compact source file is a normal Java class with the
// ceremony implied rather than typed. The compiler fills in what you left out.
//
// You do not have to take that on faith. Compile this file and disassemble it:
//
//     javac -d /tmp/java27 modules/01-getting-started/examples/WhatTheCompilerWrites.java
//     javap -c -p -cp /tmp/java27 WhatTheCompilerWrites
//
// The header that comes back looks like this:
//
//     final class WhatTheCompilerWrites {
//       WhatTheCompilerWrites();
//       void main();
//     }
//
// There is your class. The compiler named it after the file, marked it final,
// gave it a no-argument constructor, and kept main as an *instance* method.
// That last part is why it is called an "instance main method": the runtime
// constructs one instance of this class and calls main on it.
//
// This matters more than it looks. Java did not grow a second, simpler
// language for beginners. You are writing ordinary Java the whole time, with
// less typing. Everything you learn here transfers to the classic form in
// Module 05, because it *is* the classic form.

void main() {
    // Because main is an instance method, `this` exists here. In the classic
    // `public static void main` it does not, since static methods belong to the
    // class rather than to any object.
    IO.println("I am an instance method on " + this.getClass().getName());

    // Proof of the generated shape, read back at runtime.
    IO.println("Is the class final? " + java.lang.reflect.Modifier.isFinal(
            this.getClass().getModifiers()));

    // And the classic printing form still works, because nothing was replaced.
    // IO.println is shorthand, not a substitute.
    System.out.println("System.out.println still works, unchanged.");
}

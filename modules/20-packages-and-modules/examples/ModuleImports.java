// Module import declarations, JEP 511, finalised in JDK 25.
//
// One line imports every public package a module exports, which removes the
// import block from small programs and scripts.

import module java.base;

void main() {
    // No java.util import anywhere in this file. List, Map and Set all came
    // from `import module java.base`.
    List<String> names = List.of("ada", "grace", "alan");
    Map<String, Integer> lengths = new LinkedHashMap<>();
    for (String name : names) {
        lengths.put(name, name.length());
    }

    IO.println("names:   " + names);
    IO.println("lengths: " + lengths);

    // java.base exports a great deal, so this also covers java.nio.file,
    // java.time, java.util.stream, java.util.function and more.
    IO.println("stream:  " + names.stream().map(String::toUpperCase).toList());
    IO.println("path:    " + Path.of("/tmp", "example.txt"));
    IO.println("time:    " + LocalDate.of(2026, 9, 23));

    IO.println("");
    IO.println("--- what it is and is not ---");
    IO.println("  import module java.base;      every package java.base exports");
    IO.println("  import java.util.*;           one package");
    IO.println("  import java.util.List;        one type");
    IO.println("");
    IO.println("  It is a COMPILE-TIME convenience. Nothing is loaded that would");
    IO.println("  not otherwise be, and the bytecode is identical.");

    IO.println("");
    IO.println("--- when to use it ---");
    IO.println("  scripts, single-file programs, prototypes, teaching examples");
    IO.println("");
    IO.println("--- when not to ---");
    IO.println("  In a real codebase, explicit imports document what a file");
    IO.println("  depends on. A reader can see at the top which types are in");
    IO.println("  play, and a reviewer can notice when a file starts reaching");
    IO.println("  somewhere new. A module import hides both.");
    IO.println("");
    IO.println("  It also makes ambiguity possible. Import two modules that both");
    IO.println("  export a List and the compiler will make you disambiguate,");
    IO.println("  which is a problem explicit imports never have.");
}

// EXPECT: compile-error
//
// Two interfaces, the same default method, one class implementing both. Java
// refuses to guess which one you meant.
//
// The error:
//
//     error: types Timestamped and Versioned are incompatible;
//     class Entry implements Timestamped, Versioned {
//     ^
//       class Entry inherits unrelated defaults for describe() from types
//       Timestamped and Versioned
//
// This is the "diamond problem" that Java avoided for twenty years by banning
// multiple class inheritance. Default methods reintroduced a narrow version of
// it, so the language handles it the only safe way: it makes you decide.
//
// THE FIX is to override and say explicitly:
//
//     @Override
//     public String describe() {
//         return Timestamped.super.describe() + " / " + Versioned.super.describe();
//     }
//
// The InterfaceName.super.method() syntax exists only for this. You can call
// either, both, or neither.
//
// Note there is no conflict when only ONE interface supplies a default, or when
// a class supplies its own implementation. The error appears only when Java has
// two equally good candidates and no way to choose.

public class DiamondConflict {

    public static void main(String[] args) {
        System.out.println(new Entry().describe());
    }
}

interface Timestamped {
    default String describe() {
        return "created at some time";
    }
}

interface Versioned {
    default String describe() {
        return "at version 1";
    }
}

class Entry implements Timestamped, Versioned {
}

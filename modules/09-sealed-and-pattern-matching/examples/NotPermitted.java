// EXPECT: compile-error
//
// A type trying to join a sealed hierarchy without being invited.
//
// The error:
//
//     error: class is not allowed to extend sealed class: Result
//            (as it is not listed in its 'permits' clause)
//     record Maybe(String hint) implements Result {
//     ^
//
// This is the guarantee that makes exhaustive switches trustworthy. If any code
// anywhere could implement Result, the compiler could never prove a switch was
// complete, and every such switch would need a default branch that quietly
// handled the unknown.
//
// Sealing is therefore not about preventing reuse. It is about making a promise
// the compiler can rely on: this list is the whole list.

public class NotPermitted {

    public static void main(String[] args) {
        System.out.println(new Success("ok"));
    }
}

sealed interface Result permits Success, Failure {
}

record Success(String value) implements Result {
}

record Failure(String reason) implements Result {
}

record Maybe(String hint) implements Result {
}

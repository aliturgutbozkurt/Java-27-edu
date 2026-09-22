// EXPECT: compile-error
//
// A switch EXPRESSION must cover every possible input, because it has to
// produce a value no matter what comes in. This one does not, and the compiler
// refuses it rather than inventing an answer.
//
// The error names exactly what is missing:
//
//     error: the switch expression does not cover all possible input values
//         return switch (light) {
//                ^
//       missing patterns:
//           Light.GREEN
//
// That is the feature. Add a constant to an enum anywhere in a codebase and
// every exhaustive switch over it stops compiling, pointing at the places that
// need updating. A `default` branch would have hidden all of them.

void main() {
    IO.println(describe(Light.RED));
}

enum Light { RED, AMBER, GREEN }

String describe(Light light) {
    return switch (light) {
        case RED -> "stop";
        case AMBER -> "get ready";
    };
}

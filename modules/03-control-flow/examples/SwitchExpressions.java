// The modern switch. If you have seen Java's old switch, this is the good one.
//
// A switch EXPRESSION produces a value. A switch STATEMENT does something.
// Prefer the expression: it is shorter, it cannot fall through, and the
// compiler checks that you covered every case.

void main() {
    int day = 6;

    // Arrow form. Multiple labels on one line, no break anywhere.
    String kind = switch (day) {
        case 1, 2, 3, 4, 5 -> "weekday";
        case 6, 7 -> "weekend";
        default -> "not a day";
    };
    IO.println(kind);

    // When a branch needs more than one statement, use a block and `yield` to
    // say what the value is. `return` would try to leave the whole method,
    // which is why yield exists.
    int workload = switch (day) {
        case 6, 7 -> {
            int base = 10;
            int weekendBonus = base * 2;
            yield weekendBonus;
        }
        default -> 8;
    };
    IO.println("workload = " + workload);

    // switch works on String, which surprises people coming from C.
    String command = "stop";
    String result = switch (command) {
        case "go" -> "moving";
        case "stop" -> "halted";
        default -> "unknown command";
    };
    IO.println(result);

    // EXHAUSTIVENESS is the real prize.
    //
    // describe() below has no default branch. The compiler accepts it only
    // because the three enum constants are the complete set of possibilities.
    // Add a fourth constant to Light and this file stops compiling, pointing
    // straight at the switch you forgot to update.
    //
    // A default branch would have silently swallowed the new case. Leaving it
    // out turns a future bug into a compile error, which is why you often want
    // no default at all.
    IO.println(describe(Light.RED));
    IO.println(describe(Light.AMBER));
    IO.println(describe(Light.GREEN));

    // Module 09 extends this to patterns, where switch can match on types and
    // destructure records. The exhaustiveness guarantee carries over.
}

enum Light { RED, AMBER, GREEN }

String describe(Light light) {
    return switch (light) {
        case RED -> "stop";
        case AMBER -> "get ready";
        case GREEN -> "go";
    };
}

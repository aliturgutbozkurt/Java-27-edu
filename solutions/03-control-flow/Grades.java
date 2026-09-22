// Reference solution for Homework 03, part one.

void main() {
    int[] scores = {95, 83, 71, 64, 42, 100, 0};

    for (int score : scores) {
        IO.println(String.format("%3d  %-2s  %s", score, grade(score), comment(grade(score))));
    }

    IO.println();
    IO.println("Class average: " + average(scores) + " (" + grade(average(scores)) + ")");
}

// Scores are ranges, and switch matches values, so this one stays an if-chain.
//
// Trying to force it into a switch would mean listing a hundred cases, or
// switching on `score / 10`, which works but hides the boundaries: a reader has
// to divide in their head to see where the grade changes. The if-chain states
// the boundaries literally. Picking the clearer tool over the newer one is the
// point of this method.
String grade(int score) {
    if (score >= 90) return "A";
    if (score >= 80) return "B";
    if (score >= 70) return "C";
    if (score >= 60) return "D";
    return "F";
}

// Here a switch expression is the right tool: the input is a small closed set of
// values, not a range.
//
// There is no `default` branch on purpose. The grades are fixed, and if someone
// later adds "E" to grade() above, the compiler will point at this method
// instead of quietly returning a fallback string. That is only true because the
// switch covers every string it can actually receive; since String is not a
// closed type, `default` is still required here for the code to compile at all.
//
// So this version keeps default, but keeps it honest: it reports the
// unexpected value rather than pretending everything is fine.
String comment(String grade) {
    return switch (grade) {
        case "A" -> "excellent";
        case "B" -> "solid";
        case "C" -> "passing";
        case "D" -> "scraped through";
        case "F" -> "see me";
        default -> "UNKNOWN GRADE: " + grade;
    };
}

int average(int[] scores) {
    int total = 0;
    for (int s : scores) {
        total += s;
    }
    // Integer division, deliberately. The average of these is 65.0, and 65 is
    // what we want to show. If fractions mattered we would have to decide how
    // to round, exactly as the receipt in Module 02 did.
    return total / scores.length;
}

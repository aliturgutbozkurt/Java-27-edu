// Every loop Java has, and when each one is the right pick.

void main() {
    // 1. The enhanced for. Use this whenever you do not need the index, which
    //    is most of the time. Closest to Python's `for x in xs`.
    var words = java.util.List.of("alpha", "beta", "gamma");
    for (String word : words) {
        IO.print(word + " ");
    }
    IO.println();

    // It works on arrays too.
    int[] numbers = {3, 1, 4, 1, 5};
    int sum = 0;
    for (int n : numbers) {
        sum += n;
    }
    IO.println("sum = " + sum);

    // 2. The classic for. Use it when you need the index itself, or a step
    //    other than one, or you are walking backwards.
    for (int i = numbers.length - 1; i >= 0; i--) {
        IO.print(numbers[i] + " ");
    }
    IO.println();

    // The three parts are init, condition, update, and each is optional.
    // `for (;;)` is a legal infinite loop, though `while (true)` says it better.

    // 3. while, when the count is not known up front.
    int countdown = 3;
    while (countdown > 0) {
        IO.print(countdown + "... ");
        countdown--;
    }
    IO.println("go");

    // 4. do-while, which always runs at least once. Rare, but it exists, and
    //    the semicolon at the end is easy to forget.
    int attempts = 0;
    do {
        attempts++;
    } while (attempts < 1);
    IO.println("ran " + attempts + " time even though the condition was false at the end");

    // break and continue behave as you expect.
    for (int i = 0; i < 10; i++) {
        if (i % 2 == 0) continue;   // skip evens
        if (i > 6) break;           // stop entirely
        IO.print(i + " ");
    }
    IO.println();

    // LABELLED BREAK: the one piece of Java loop syntax with no equivalent in
    // most languages. It breaks out of an OUTER loop from inside an inner one.
    //
    // Without it you need a flag variable and an extra condition, which is
    // exactly the kind of code that rots.
    int[][] grid = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 9}
    };

    int target = 5;
    int foundRow = -1, foundCol = -1;

    search:
    for (int row = 0; row < grid.length; row++) {
        for (int col = 0; col < grid[row].length; col++) {
            if (grid[row][col] == target) {
                foundRow = row;
                foundCol = col;
                break search;      // leaves BOTH loops
            }
        }
    }
    IO.println("found " + target + " at " + foundRow + "," + foundCol);

    // `continue label` also exists and jumps to the next iteration of the
    // labelled loop. Both are worth knowing and worth using sparingly: more
    // than one label in a method usually means the method wants splitting up.

    // A trap worth meeting once. This loop never ends, because i is compared
    // after overflowing:
    //
    //     for (int i = 1; i > 0; i *= 2) { }
    //
    // i doubles past Integer.MAX_VALUE, wraps negative, and the condition
    // finally fails. It terminates, but not for the reason you wrote.
}

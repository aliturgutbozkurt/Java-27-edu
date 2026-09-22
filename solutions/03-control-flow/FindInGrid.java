// Reference solution for Homework 03, part two.
//
// The same search written twice: once with a flag variable, once with a
// labelled break. Both work. The second is why the syntax exists.

void main() {
    int[][] grid = {
        { 7, 12,  3},
        { 9,  5, 21},
        {14,  2, 18}
    };

    IO.println("with a flag variable:");
    withFlag(grid, 21);
    withFlag(grid, 99);

    IO.println();
    IO.println("with a labelled break:");
    withLabel(grid, 21);
    withLabel(grid, 99);
}

// The flag version. Notice what it costs:
//
//   - a `found` variable that exists only to communicate between the loops
//   - an extra condition on the outer loop, easy to forget
//   - two places that have to stay in sync if the search changes
//
// None of that is about searching a grid. It is all bookkeeping to work around
// break only leaving one loop.
void withFlag(int[][] grid, int target) {
    boolean found = false;
    int row = -1, col = -1;

    for (int r = 0; r < grid.length && !found; r++) {
        for (int c = 0; c < grid[r].length; c++) {
            if (grid[r][c] == target) {
                row = r;
                col = c;
                found = true;
                break;
            }
        }
    }

    report(target, row, col);
}

// The labelled version. The bookkeeping is gone, and `break search` says
// literally what it does.
void withLabel(int[][] grid, int target) {
    int row = -1, col = -1;

    search:
    for (int r = 0; r < grid.length; r++) {
        for (int c = 0; c < grid[r].length; c++) {
            if (grid[r][c] == target) {
                row = r;
                col = c;
                break search;
            }
        }
    }

    report(target, row, col);
}

void report(int target, int row, int col) {
    if (row < 0) {
        IO.println("  " + target + " is not in the grid");
    } else {
        IO.println("  found " + target + " at row " + row + ", column " + col);
    }
}

// A third option worth knowing: extract the inner search into its own method and
// `return` from it. That also escapes both loops, needs no label, and often
// reads best of all. Labelled break earns its place when extracting a method
// would mean passing five variables in and out.

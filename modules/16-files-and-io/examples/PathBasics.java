// A Path is a name, not a file. Every method here is pure string manipulation
// and touches no disk at all.
//
// That separation is deliberate and it is the first thing to internalise:
// Path answers "what would this location be called", and Files answers "what is
// actually there".

import java.nio.file.Path;

public class PathBasics {

    public static void main(String[] args) {
        Path path = Path.of("/home/ada/projects/report.txt");

        System.out.println("--- taking a path apart ---");
        System.out.println("  full:      " + path);
        System.out.println("  fileName:  " + path.getFileName());
        System.out.println("  parent:    " + path.getParent());
        System.out.println("  root:      " + path.getRoot());
        System.out.println("  nameCount: " + path.getNameCount());
        System.out.println("  name(1):   " + path.getName(1));

        System.out.println();
        System.out.println("--- building paths ---");

        // resolve appends. Use it instead of string concatenation, because it
        // gets the separator right on every platform and handles the edge cases
        // around trailing slashes.
        Path base = Path.of("/var/data");
        System.out.println("  resolve:          " + base.resolve("reports/q3.csv"));

        // An ABSOLUTE argument replaces the base entirely rather than appending.
        // That is the behaviour that turns unvalidated user input into a path
        // traversal bug.
        System.out.println("  resolve absolute: " + base.resolve("/etc/passwd"));

        // resolveSibling replaces the last element.
        System.out.println("  resolveSibling:   "
                + Path.of("/var/data/old.txt").resolveSibling("new.txt"));

        // relativize computes the route from one path to another.
        System.out.println("  relativize:       "
                + Path.of("/a/b").relativize(Path.of("/a/b/c/d.txt")));

        System.out.println();
        System.out.println("--- normalising ---");

        Path messy = Path.of("/a/b/../c/./d.txt");
        System.out.println("  before: " + messy);
        System.out.println("  after:  " + messy.normalize());

        // normalize is PURE. It does not check whether /a/b exists, and it does
        // not follow symbolic links. If b were a symlink, removing "b/.." would
        // change which file you end up at.
        //
        // toRealPath() is the one that touches the disk: it resolves symlinks
        // and throws if the file does not exist. Use it when the answer has to
        // be true rather than merely tidy.

        System.out.println();
        System.out.println("--- the security note ---");

        // Never build a path from user input without normalising and checking
        // the result is still where you meant.
        String userSupplied = "../../etc/passwd";
        Path uploads = Path.of("/srv/uploads").toAbsolutePath();
        Path attempted = uploads.resolve(userSupplied).normalize();

        System.out.println("  uploads dir: " + uploads);
        System.out.println("  user asked:  " + userSupplied);
        System.out.println("  resolves to: " + attempted);
        System.out.println("  still inside uploads? " + attempted.startsWith(uploads));

        // startsWith on a Path compares NAME ELEMENTS, not characters, so it
        // cannot be fooled by "/srv/uploads-evil" the way String.startsWith can.
        System.out.println("  String.startsWith would have said: "
                + "/srv/uploads-evil/x".startsWith("/srv/uploads"));
        System.out.println("  Path.startsWith says:             "
                + Path.of("/srv/uploads-evil/x").startsWith(Path.of("/srv/uploads")));

        System.out.println();
        System.out.println("--- comparing paths ---");

        // equals is lexical. Two different names for the same file are not
        // equal, which surprises people.
        System.out.println("  equals:  " + Path.of("/a/b.txt").equals(Path.of("/a/./b.txt")));
        System.out.println("  after normalize: "
                + Path.of("/a/b.txt").equals(Path.of("/a/./b.txt").normalize()));
        System.out.println("  Files.isSameFile() is the one that asks the filesystem");
    }
}

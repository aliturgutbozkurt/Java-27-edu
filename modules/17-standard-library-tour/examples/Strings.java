// The String methods you will reach for constantly, and the formatting API that
// replaces most string concatenation.

import java.util.List;
import java.util.StringJoiner;

public class Strings {

    public static void main(String[] args) {
        System.out.println("--- testing and trimming ---");

        String messy = "  Hello, World  ";
        System.out.println("  strip():   [" + messy.strip() + "]");
        System.out.println("  isBlank(): " + "   ".isBlank());
        System.out.println("  isEmpty(): " + "   ".isEmpty());

        // strip() is Unicode-aware; trim() is not, and only removes characters
        // up to U+0020. Use strip() in new code. trim() predates Unicode
        // whitespace being a thing anyone thought about.
        String unicodeSpace = " hello ";
        System.out.println("  trim() on a Unicode space left: " + unicodeSpace.trim().length() + " chars");
        System.out.println("  strip() left:                   " + unicodeSpace.strip().length() + " chars");

        System.out.println();
        System.out.println("--- splitting and joining ---");

        System.out.println("  split:      " + List.of("a,b,c".split(",")));
        System.out.println("  join:       " + String.join("-", "2026", "09", "23"));
        System.out.println("  repeat:     " + "ab".repeat(3));
        System.out.println("  lines:      " + "one\ntwo\nthree".lines().toList());

        // split() takes a REGEX, not a literal. Splitting on "." or "|" without
        // escaping gives an empty array or the wrong answer, and it is a
        // classic one-hour bug.
        System.out.println("  split on \".\" unescaped: " + "a.b.c".split("\\.").length + " parts");
        System.out.println("  (that needed \\\\. because split takes a regex)");

        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        joiner.add("one").add("two");
        System.out.println("  StringJoiner: " + joiner);

        System.out.println();
        System.out.println("--- formatting ---");

        // formatted() is an instance method added in Java 15, so the format
        // string comes first and reads naturally.
        System.out.println("  " + "%-10s|%8.2f|%,d".formatted("name", 3.14159, 1_234_567));
        System.out.println("  " + "%s scored %d%%".formatted("ada", 95));

        // The conversions worth memorising:
        //
        //   %s   anything, via toString
        //   %d   integer
        //   %f   floating point, %.2f for two places
        //   %n   platform line separator, unlike \n
        //   %%   a literal percent sign
        //
        // Width and alignment: %10s right-aligns in 10 columns, %-10s left.
        // %,d groups thousands. %08.2f zero-pads.
        System.out.println("  " + "%08.2f".formatted(3.14159));

        System.out.println();
        System.out.println("--- text blocks, revisited ---");

        String query = """
                SELECT name, email
                  FROM users
                 WHERE active = true
                """;
        System.out.println(query.strip().indent(2));

        // A text block with a placeholder is often cleaner than concatenation:
        String report = """
                Report for %s
                ============
                Total: %,d
                """.formatted("September", 1_234_567);
        System.out.print(report);

        System.out.println();
        System.out.println("--- comparing ---");

        System.out.println("  equals:           " + "abc".equals("ABC"));
        System.out.println("  equalsIgnoreCase: " + "abc".equalsIgnoreCase("ABC"));
        System.out.println("  compareTo:        " + "apple".compareTo("banana"));

        // compareTo returns a NEGATIVE number, zero, or a positive one. It does
        // not return -1, 0, 1, and code that assumes it does is wrong. The
        // value above is the difference between the first differing characters.

        System.out.println();
        System.out.println("--- building in a loop ---");

        // Concatenation in a loop creates a new String each time, because
        // Strings are immutable. For a handful of pieces the compiler often
        // optimises it away. For thousands, use StringBuilder.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i).append(',');
        }
        sb.setLength(sb.length() - 1);   // drop the trailing comma
        System.out.println("  " + sb);
    }
}

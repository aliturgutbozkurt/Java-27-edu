// EXPECT: runtime-error
//
// What erasure costs you when raw types get involved.
//
// This file compiles with a warning and then fails at runtime, which is exactly
// the pre-generics situation generics were introduced to prevent.

import java.util.ArrayList;
import java.util.List;

public class HeapPollution {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main(String[] args) {
        List<String> words = new ArrayList<>();
        words.add("legitimate");

        // A RAW TYPE. `List` with no type argument. Assigning a List<String> to
        // it is legal, for backward compatibility with code written before 2004.
        List raw = words;

        // The compiler warns here and lets it through. After erasure both are
        // just List, so there is nothing at runtime to stop the Integer going in.
        raw.add(42);

        System.out.println("the list now contains: " + words);
        System.out.println("its declared type still says List<String>");
        System.out.println();
        System.out.println("reading element 0 is fine: " + words.get(0));
        System.out.println("reading element 1 will not be:");

        // THE FAILURE HAPPENS HERE, not at the add.
        //
        // The compiler inserted a cast to String at this line, because that is
        // what the declared type promised. The cast fails.
        //
        //     ClassCastException: class java.lang.Integer cannot be cast to
        //     class java.lang.String
        //
        // Note where the blame lands. The line that broke the invariant was
        // `raw.add(42)`, but the crash is here, possibly in a different class
        // written by a different person. That distance is what makes heap
        // pollution genuinely hard to debug.
        String second = words.get(1);

        System.out.println("never reached: " + second);
    }
}

// THE RULE: never use a raw type in new code.
//
// The compiler's unchecked warnings exist to tell you that it has stopped being
// able to guarantee anything. Turn them on with -Xlint:unchecked and treat them
// as errors.
//
// When you genuinely must suppress one, put @SuppressWarnings on the smallest
// possible scope, ideally a single local variable declaration, and leave a
// comment saying why the cast is safe. A suppression on a whole class hides
// every future mistake too.

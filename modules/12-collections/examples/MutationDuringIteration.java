// EXPECT: runtime-error
//
// Removing from a collection while iterating it. Every Java developer writes
// this once.
//
// The output ends with:
//
//     Exception in thread "main" java.util.ConcurrentModificationException
//
// "Concurrent" is misleading. There is one thread here. The name means the
// collection was modified concurrently with an iteration in progress, not from
// another thread.
//
// HOW IT IS DETECTED: ArrayList keeps a modCount that increments on every
// structural change. The iterator records that number when it is created and
// checks it before every step. A mismatch means the list changed under it, so
// it fails fast rather than quietly skipping elements or reading past the end.
//
// This is a FAIL-FAST check, not a guarantee. It is documented as best-effort,
// so you cannot rely on it to catch every case. Do not write code that depends
// on the exception being thrown.
//
// THE FOUR CORRECT ALTERNATIVES:
//
//   1. removeIf, which is the right answer nearly always:
//
//          numbers.removeIf(n -> n % 2 == 0);
//
//   2. An explicit Iterator, when the condition needs more than a predicate:
//
//          var it = numbers.iterator();
//          while (it.hasNext()) {
//              if (it.next() % 2 == 0) it.remove();
//          }
//
//   3. Collect what to remove, then remove it afterwards.
//
//   4. Build a new collection instead of editing the old one. Module 14 covers
//      streams, which make this the most readable option.

import java.util.ArrayList;
import java.util.List;

public class MutationDuringIteration {

    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        System.out.println("starting with: " + numbers);

        // The correct version first, so the contrast is visible.
        List<Integer> safe = new ArrayList<>(numbers);
        safe.removeIf(n -> n % 2 == 0);
        System.out.println("removeIf gives: " + safe);

        System.out.println();
        System.out.println("now the broken version:");

        for (Integer n : numbers) {
            if (n % 2 == 0) {
                numbers.remove(n);
            }
        }

        System.out.println("never reached: " + numbers);
    }
}

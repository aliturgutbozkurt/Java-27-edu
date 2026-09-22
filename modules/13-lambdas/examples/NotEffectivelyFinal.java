// EXPECT: compile-error
//
// A lambda capturing a variable that gets reassigned.
//
// The error:
//
//     error: local variables referenced from a lambda expression must be final
//            or effectively final
//         Runnable r = () -> System.out.println("count is " + count);
//                                                             ^
//
// WHY THE RULE EXISTS: a lambda can outlive the method that made it. By the
// time it runs, the method's stack frame may be long gone, so the lambda cannot
// hold a reference to the variable. It holds a copy of the value.
//
// If reassignment were allowed, two questions would have no good answer: does
// the already-created lambda see the new value, and what happens when two
// threads assign at once? Java avoids both by requiring the variable never to
// change after initialisation.
//
// Note it is about the VARIABLE, not the object. This is fine:
//
//     List<String> items = new ArrayList<>();
//     Runnable r = () -> items.add("x");     // the variable never changes
//
// THE FIXES, in order of preference:
//
//   1. Do not reassign. Introduce a second variable if you need a new value.
//   2. Compute the final value first, then create the lambda.
//   3. If you are accumulating, use a stream reduction instead. Module 14.
//
// The array trick, `int[] box = {0}`, compiles because the variable never
// changes. It is legal and it is a smell: it means the lambda is being used for
// mutation, which is what streams and reductions exist to replace.

public class NotEffectivelyFinal {

    public static void main(String[] args) {
        int count = 0;

        Runnable r = () -> System.out.println("count is " + count);

        count = 1;

        r.run();
    }
}

// What a lambda can see from around it, and the rule that trips people up.

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Capture {

    public static void main(String[] args) {
        System.out.println("--- capturing a local ---");

        int base = 10;
        Function<Integer, Integer> addBase = n -> n + base;
        System.out.println("  addBase(5) = " + addBase.apply(5));

        // THE RULE: a captured local must be FINAL or EFFECTIVELY FINAL.
        //
        // Effectively final means you never assign to it after initialisation.
        // You do not have to write the keyword; the compiler checks the
        // behaviour. Add `base = 20;` anywhere in this method and the lambda
        // above stops compiling, as NotEffectivelyFinal.java shows.

        System.out.println();
        System.out.println("--- why the rule exists ---");

        // A lambda can outlive the method that created it. Here the Supplier is
        // returned and called later, long after makeCounter has finished and
        // its stack frame is gone.
        Supplier<String> later = makeMessage();
        System.out.println("  called after its method returned: " + later.get());

        // The lambda therefore cannot hold a reference to the variable itself,
        // because the variable no longer exists. It holds a COPY of the value.
        //
        // If reassignment were allowed, there would be two questions with no
        // good answer: does the lambda see the new value, and what happens when
        // two threads reassign at once? Java sidesteps both by requiring the
        // variable never to change.

        System.out.println();
        System.out.println("--- primitives copy, objects share ---");

        // The COPY is of the variable, exactly as Module 04 described for
        // method arguments. For a primitive, that is the value. For an object,
        // that is the reference, so the object itself is still shared.
        List<String> shared = new ArrayList<>();
        Runnable adder = () -> shared.add("added from inside the lambda");
        adder.run();
        System.out.println("  the captured list was mutated: " + shared);

        // `shared` is effectively final: the VARIABLE never gets reassigned.
        // The object it points at changes freely, and the compiler has no
        // objection, because the rule is about the variable, not the object.
        //
        // That is also the loophole people use to work around the rule, and it
        // is usually a sign the code wants restructuring:
        //
        //     int[] counter = {0};
        //     list.forEach(x -> counter[0]++);   // compiles, and is a smell
        //
        // Module 14 shows what to write instead.

        System.out.println();
        System.out.println("--- fields have no such rule ---");

        new Capture().demonstrateFieldCapture();

        System.out.println();
        System.out.println("--- `this` means the enclosing object ---");

        new Capture().compareThis();
    }

    private int callCount = 0;

    void demonstrateFieldCapture() {
        // Fields are not captured. The lambda captures `this` and reads the
        // field through it, so a mutable field is fine and reassignment is
        // allowed.
        Runnable increment = () -> callCount++;
        increment.run();
        increment.run();
        System.out.println("  field after two lambda calls: " + callCount);

        // Which also means a lambda holding `this` keeps the whole enclosing
        // object alive. That matters when lambdas are stored in long-lived
        // listener lists.
    }

    void compareThis() {
        // In a lambda, `this` is the ENCLOSING object. There is no separate
        // lambda object to refer to.
        // getName rather than getSimpleName: an anonymous class HAS no simple
        // name, so getSimpleName returns an empty string and the difference
        // being demonstrated would be invisible.
        Runnable lambda = () -> System.out.println(
                "  inside a lambda, this is:           " + this.getClass().getName());
        lambda.run();

        // In an anonymous class, `this` is the anonymous instance itself. Note
        // the generated name ending in $1: that is a separate class, and a
        // separate object. This is a genuine behavioural difference, not style.
        Runnable anonymous = new Runnable() {
            @Override
            public void run() {
                System.out.println("  inside an anonymous class, this is: "
                        + this.getClass().getName());
            }
        };
        anonymous.run();
    }

    static Supplier<String> makeMessage() {
        String local = "captured before the method returned";
        return () -> local;
    }
}

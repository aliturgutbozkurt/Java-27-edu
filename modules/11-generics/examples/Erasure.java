// Generics are a compile-time feature. At runtime the type arguments are gone.
// That is called erasure, and it explains every limitation in this file.

import java.util.ArrayList;
import java.util.List;

public class Erasure {

    public static void main(String[] args) {
        List<String> words = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>();

        // The same class. The type argument left no trace.
        System.out.println("List<String> class:  " + words.getClass().getName());
        System.out.println("List<Integer> class: " + numbers.getClass().getName());
        System.out.println("same class:          " + (words.getClass() == numbers.getClass()));

        // WHY IT WORKS THIS WAY:
        //
        // Generics arrived in Java 5, fifteen years after the JVM. Erasure meant
        // generic code and pre-generic code could call each other and run on the
        // same unchanged JVM. Existing libraries did not have to be rewritten
        // and existing class files kept working.
        //
        // The cost is everything below. It was a deliberate trade: compatibility
        // then, in exchange for these limits forever.

        System.out.println();
        System.out.println("what erasure forbids:");

        // 1. NO instanceof ON A TYPE ARGUMENT.
        //
        //        if (x instanceof List<String>)   // does not compile
        //
        //    At runtime there is no such type to test against. You can test the
        //    raw type, which tells you less:
        System.out.println("  raw instanceof works: " + (words instanceof List));

        // 2. NO new T[] AND NO new T().
        //
        //    The compiler cannot emit an allocation for a type it will not know
        //    at runtime. Workarounds pass a Class<T> or a factory instead.
        //    CannotCreateGenericArray.java shows the error.

        // 3. NO OVERLOADING ON TYPE ARGUMENTS.
        //
        //        void show(List<String> l) { }
        //        void show(List<Integer> l) { }   // same erasure, does not compile
        //
        //    After erasure both are show(List), so they collide.

        // 4. NO PRIMITIVES AS TYPE ARGUMENTS.
        //
        //        List<int>     // does not compile
        //        List<Integer> // fine, and boxes every element
        //
        //    Module 02's note about boxing costs applies directly. This is the
        //    limitation Valhalla, a long-running OpenJDK project, aims to remove.

        // 5. STATIC MEMBERS CANNOT USE THE CLASS TYPE VARIABLE.
        //
        //        class Box<T> { static T shared; }   // does not compile
        //
        //    There is one static field per class, not one per type argument, and
        //    the class does not know T.

        System.out.println();
        System.out.println("what you CAN still do:");

        // Pass a Class object when you need the type at runtime. This is the
        // standard workaround and it is why so many APIs take a Class parameter.
        System.out.println("  " + describe(String.class, "hello"));
        System.out.println("  " + describe(Integer.class, 42));

        // Reflection can recover type arguments from FIELDS and METHOD
        // SIGNATURES, because those are recorded in the class file. It cannot
        // recover them from an object, because the object never had them.
    }

    static <T> String describe(Class<T> type, T value) {
        // type.getSimpleName() works because we were handed the Class, not
        // because T survived to runtime.
        return type.getSimpleName() + " holding " + value;
    }
}

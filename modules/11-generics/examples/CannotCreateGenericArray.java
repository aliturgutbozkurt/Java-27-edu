// EXPECT: compile-error
//
// You cannot create an array of a type variable. This is erasure's most
// annoying practical consequence.
//
// The error:
//
//     error: generic array creation
//             this.items = new T[size];
//                          ^
//
// WHY: at runtime the JVM stores an array's element type and checks every
// write against it, which is what produces ArrayStoreException. For `new T[]`
// there is no element type to store, because T was erased. The JVM cannot make
// the check it is required to make, so the compiler refuses to emit the
// allocation.
//
// THE STANDARD WORKAROUNDS:
//
//   1. Use a List instead. Almost always the right answer.
//
//          private final List<T> items = new ArrayList<>();
//
//   2. Allocate Object[] and cast, suppressing the warning on the narrowest
//      possible scope. This is what ArrayList itself does internally:
//
//          @SuppressWarnings("unchecked")
//          T[] items = (T[]) new Object[size];
//
//      Safe only while the array never escapes the class, because a caller
//      given it as T[] would get a ClassCastException on assignment.
//
//   3. Pass a Class<T> and use reflection, which is what Arrays.copyOf does:
//
//          T[] items = (T[]) java.lang.reflect.Array.newInstance(type, size);

public class CannotCreateGenericArray {

    public static void main(String[] args) {
        Buffer<String> buffer = new Buffer<>(4);
        System.out.println(buffer.capacity());
    }
}

class Buffer<T> {

    private final T[] items;

    Buffer(int size) {
        this.items = new T[size];
    }

    int capacity() {
        return items.length;
    }
}

// Every primitive has an object twin: int/Integer, double/Double, char/Character,
// boolean/Boolean. Java converts between them automatically, which is called
// autoboxing, and it is convenient right up to the moment it bites.
//
// Here are both bites.

void main() {
    // BITE ONE: == on boxed values compares identity, not value.

    Integer a = 127;
    Integer b = 127;
    IO.println("127 == 127 : " + (a == b));      // true

    Integer c = 128;
    Integer d = 128;
    IO.println("128 == 128 : " + (c == d));      // false

    // Same code, same kind of value, opposite answer. Nothing is broken.
    //
    // Java keeps a cache of Integer objects for -128..127 because small numbers
    // are so common. Inside that range you get the SAME cached object back, so
    // == is comparing an object to itself and says true. Outside it you get two
    // fresh objects, and == compares their identities, which differ.
    //
    // The lesson is not "remember 127". The lesson is that == on objects has
    // always meant "the same object", and it was misleading you inside the cache.
    IO.println("c.equals(d) : " + c.equals(d));  // true, which is what you meant

    // THE RULE:  == for primitives.  .equals() for objects.
    //
    // And when you write == on two boxed values, you almost never wanted to.

    // BITE TWO: unboxing null. See NullUnboxing.java for the crash itself.

    // A useful habit: prefer the primitive unless you actually need an object.
    // You need the object form for collections, since List<int> is illegal and
    // List<Integer> is not. Module 12 comes back to this.
    int fast = 42;                   // no allocation
    Integer boxed = 42;              // an object, on the heap
    IO.println(fast + " " + boxed);

    // Boxing in a hot loop is a real cost. This allocates a million objects:
    //
    //     Long sum = 0L;
    //     for (long i = 0; i < 1_000_000; i++) sum += i;
    //
    // Changing Long to long removes every one of those allocations. The code
    // looks nearly identical, which is exactly why it is worth knowing.
}

// Java has two parallel worlds for data, and almost every confusing thing in
// this module comes from the border between them.
//
//   PRIMITIVES   int, long, double, float, short, byte, char, boolean
//                The variable holds the value itself. Eight of them, that is
//                the complete list, and you cannot add more.
//
//   REFERENCES   String, arrays, and every class ever written
//                The variable holds a *reference* to an object living
//                elsewhere. The variable is a label, not the thing.
//
// Coming from Python or JavaScript, where everything is an object, that split
// looks like an annoying historical accident. It partly is. It also makes Java
// fast, because an int is genuinely just 32 bits on the stack with no object
// header, no allocation, and no pointer to follow.

void main() {
    // A primitive: this variable IS 42.
    int count = 42;

    // A reference: this variable POINTS AT a String object.
    String label = "answers";

    IO.println(count + " " + label);

    // The difference shows up the moment you copy.
    int copied = count;
    copied = 99;
    // count is untouched, because copying a primitive copies the value.
    IO.println("original primitive after copying and changing: " + count);

    // Now the same thing with references.
    int[] original = {1, 2, 3};
    int[] alias = original;   // copies the REFERENCE, not the array
    alias[0] = 999;

    // Both names point at the same array, so both see the change.
    IO.println("original[0] after changing through the alias: " + original[0]);

    // This is the single most common source of "but I never changed that!"
    // bugs for people arriving from languages that copy more eagerly. Module 04
    // returns to it when we look at what happens to method arguments.

    // The eight primitives, with the ones you will actually use marked.
    boolean flag = true;        // true or false, nothing else. No truthiness.
    int whole = 1_000_000;      // underscores are legal and purely for reading
    long big = 9_000_000_000L;  // the L matters; without it this will not compile
    double precise = 3.14;      // default for decimals
    char letter = 'A';          // single quotes, and secretly a number

    // byte, short and float exist and you will rarely reach for them.
    byte small = 127;
    short medium = 32_000;
    float lessPrecise = 3.14f;  // the f matters, same reason as the L

    IO.println(flag + " " + whole + " " + big + " " + precise + " " + letter);
    IO.println(small + " " + medium + " " + lessPrecise);

    // Note the quotes. 'A' is a char, "A" is a String. They are not
    // interchangeable and swapping them is a compile error, not a warning.
}

// The most misunderstood sentence in Java:
//
//     Java is always pass-by-value. Always. Including for objects.
//
// People argue about this online constantly, usually because they are using
// "pass by reference" to mean two different things. Here is what actually
// happens, demonstrated rather than asserted.
//
// The rule in one line: the METHOD GETS A COPY OF THE VARIABLE. When the
// variable holds a reference, the method gets a copy of the reference, so both
// now point at the same object.

void main() {
    // CASE 1: a primitive. Obviously copied.
    int number = 10;
    tryToChangePrimitive(number);
    IO.println("primitive after the call: " + number);   // still 10

    // CASE 2: an object, MUTATED inside the method.
    var list = new java.util.ArrayList<String>();
    list.add("original");
    mutateTheObject(list);
    IO.println("list after mutation:      " + list);     // changed!

    // CASE 3: an object, REASSIGNED inside the method.
    var other = new java.util.ArrayList<String>();
    other.add("original");
    reassignTheParameter(other);
    IO.println("list after reassignment:  " + other);    // unchanged!

    // Case 2 and case 3 are the whole lesson. Same type, same call shape,
    // opposite outcomes.
    //
    // In case 2 the method followed its copy of the reference to the shared
    // object and changed that object. You can see it from here because there is
    // only one object.
    //
    // In case 3 the method pointed its own copy at a brand new object. Your
    // variable still points where it always did. The method rebound its local
    // name, which is all it can ever do.

    // CASE 4: Strings look like they prove reassignment works. They do not.
    String text = "before";
    tryToChangeString(text);
    IO.println("string after the call:    " + text);     // still "before"
    // Strings are immutable, so there is no case-2 equivalent for them at all.
    // Every String method returns a new object, which is why this looks like
    // pass-by-value even to people who believe objects are passed by reference.

    // The practical consequence, and the reason this matters:
    //
    //   - To give a method the ability to change your data, pass a mutable
    //     object and let it mutate.
    //   - To stop a method changing your data, pass something immutable, or a
    //     copy, or a List.copyOf view.
    //   - A method can NEVER make your variable point somewhere else. If it
    //     needs to hand you a different object, it has to return it.
    var protectedList = java.util.List.copyOf(list);
    IO.println("an immutable copy:        " + protectedList);
}

void tryToChangePrimitive(int value) {
    value = 999;   // rebinds the local copy only
}

void mutateTheObject(java.util.List<String> items) {
    items.add("added inside the method");   // follows the reference, changes the shared object
}

void reassignTheParameter(java.util.List<String> items) {
    items = new java.util.ArrayList<>();    // points the local copy at something new
    items.add("this list is invisible to the caller");
}

void tryToChangeString(String text) {
    text = text + " and after";   // builds a new String, rebinds the local copy
}

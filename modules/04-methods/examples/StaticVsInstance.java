// static belongs to the CLASS. Everything else belongs to an OBJECT.
//
// You have been writing instance methods this whole time without noticing,
// because a compact source file's main() is one. Module 05 makes that explicit.

void main() {
    // An instance method. `this` exists, so it can reach instance state.
    IO.println(greetInstance("Ada"));

    // A static method. No `this`, so it can only work with what it is given.
    IO.println(greetStatic("Grace"));

    // Both look identical at the call site from in here. The difference shows
    // up in what they are allowed to touch.

    // The counter below is static, so there is exactly one of it for the whole
    // class rather than one per object.
    record();
    record();
    record();
    IO.println("recorded " + callCount + " calls");

    // WHEN TO USE static:
    //
    //   - the method needs nothing from any particular object
    //   - it is a pure function of its arguments, like Math.max
    //   - a factory that builds instances, like List.of
    //
    // WHEN NOT TO:
    //
    //   - it reads or writes per-object state
    //   - you want to override it later. Static methods are not polymorphic;
    //     Module 06 covers what that means and why it matters.
    //
    // A useful test: if making the method static requires passing in several
    // things that an object would already know, it should not be static.

    IO.println("Math.max(3, 7) = " + Math.max(3, 7));   // static, pure, no object needed
}

// Static state: one copy shared by everything. Convenient and dangerous. In a
// multi-threaded program this counter is a race condition waiting to happen,
// which Module 18 covers in detail.
static int callCount = 0;

static void record() {
    callCount++;
}

String greetInstance(String name) {
    // Could use `this` here. An instance method has one.
    return "instance method greeting " + name;
}

static String greetStatic(String name) {
    // `this` does not exist here. Referring to it is a compile error, because
    // there is no particular object this method was called on.
    return "static method greeting " + name;
}

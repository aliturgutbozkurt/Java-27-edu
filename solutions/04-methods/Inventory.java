// Reference solution for Homework 04, part one.
//
// The brief: addItem must be able to change the caller's list, replaceAll must
// not, and both must be demonstrated rather than claimed.

void main() {
    var stock = new java.util.ArrayList<String>(java.util.List.of("bolt", "nut"));

    IO.println("start:              " + stock);

    addItem(stock, "washer");
    IO.println("after addItem:      " + stock);

    replaceAll(stock, java.util.List.of("gear", "spring"));
    IO.println("after replaceAll:   " + stock);

    var replaced = replaceAllProperly(stock, java.util.List.of("gear", "spring"));
    IO.println("returned list:      " + replaced);
    IO.println("original unchanged: " + stock);

    IO.println();
    IO.println("--- defensive copy ---");

    // Two separate objects, each starting from the same contents, so the two
    // lines below can be compared directly. Reusing one object would leave the
    // first mutation baked into the second result and prove nothing.
    var leaky = new Guarded(stock);
    var safe = new Guarded(stock);
    IO.println("both start as:                   " + leaky.itemsUnsafe());

    leaky.itemsUnsafe().add("injected");
    IO.println("leaky object after outside edit: " + leaky.itemsUnsafe());

    safe.itemsSafe().add("injected");
    IO.println("safe object after outside edit:  " + safe.itemsSafe());
}

// WORKS. The method follows its copy of the reference to the one shared
// ArrayList and calls add on it. There is only one list, so the caller sees it.
void addItem(java.util.List<String> items, String item) {
    items.add(item);
}

// DOES NOTHING VISIBLE. This is the trap the homework asks you to walk into.
//
// The assignment points this method's own local copy of the reference at a new
// ArrayList. The caller's variable still points at the original. A method can
// change the object it was handed; it can never change which object the caller's
// variable names.
void replaceAll(java.util.List<String> items, java.util.List<String> replacements) {
    items = new java.util.ArrayList<>(replacements);
    items.add("and this is invisible too");
}

// THE FIX, and there are only two of them.
//
// Option A, used here: build the new list and RETURN it. The caller decides what
// to do with the result, which is explicit and usually the better design.
//
// Option B: mutate in place with items.clear() then items.addAll(replacements).
// That works because it changes the shared object instead of rebinding a name.
// It is the right choice when the caller genuinely wants the method to edit
// their list, and the wrong one when they did not expect it.
java.util.List<String> replaceAllProperly(
        java.util.List<String> items, java.util.List<String> replacements) {
    return new java.util.ArrayList<>(replacements);
}

// A tiny class showing why "return your own list" is a leak. Module 05 covers
// the class syntax itself; here it is only scaffolding for the point.
static class Guarded {
    private final java.util.List<String> items;

    Guarded(java.util.List<String> items) {
        // Copying on the way IN matters too. Without this, whoever passed the
        // list in keeps a reference to our internal state.
        this.items = new java.util.ArrayList<>(items);
    }

    // Hands out a direct reference to internal state. Anyone can now change
    // this object's contents without going through it.
    java.util.List<String> itemsUnsafe() {
        return items;
    }

    // Hands out a copy. The caller can do whatever they like to it and this
    // object is unaffected.
    //
    // List.copyOf would be cheaper and also immutable, which is better still
    // when the caller has no reason to modify the result.
    java.util.List<String> itemsSafe() {
        return new java.util.ArrayList<>(items);
    }
}

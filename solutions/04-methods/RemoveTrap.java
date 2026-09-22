// Reference solution for Homework 04, part two.
//
// The List.remove overload trap, triggered on purpose and then explained.

void main() {
    // Both lists start identical.
    var byIndex = new java.util.ArrayList<Integer>(java.util.List.of(10, 20, 30, 40));
    var byValue = new java.util.ArrayList<Integer>(java.util.List.of(10, 20, 30, 40));

    IO.println("start:        " + byIndex);

    // remove(int) — the compiler picks the INDEX overload, because the literal
    // 3 is an int and remove(int) matches exactly with no conversion at all.
    byIndex.remove(3);
    IO.println("remove(3):    " + byIndex + "   <- removed position 3, the value 40");

    // remove(Object) — wrapping forces the value overload.
    byValue.remove(Integer.valueOf(30));
    IO.println("remove(I(30)): " + byValue + "   <- removed the value 30");

    IO.println();

    // The dangerous version: a variable instead of a literal. It reads as
    // "remove this id", and it silently means "remove this position".
    // The id 2 sits at index 1 here, NOT at index 2. That gap is what makes the
    // bug visible; line up the value with its own index and both spellings give
    // the same answer, which is exactly how this survives a careless test.
    var ids = new java.util.ArrayList<Integer>(java.util.List.of(100, 2, 200, 300));
    int idToDrop = 2;

    IO.println("ids:                                  " + ids);
    ids.remove(idToDrop);
    IO.println("meant to drop the id 2, actually got: " + ids + "   <- dropped 200, at index 2");

    // Why the compiler chooses the index overload, in my own words:
    //
    // Overload resolution tries an exact match before it tries anything else.
    // `idToDrop` is declared int, and remove(int) takes an int, so that is an
    // exact match and the search stops right there. Reaching remove(Object)
    // would require boxing the int to an Integer, and boxing is only considered
    // after exact matches and widening have both failed.
    //
    // Nothing here is ambiguous to the compiler. It is only ambiguous to the
    // reader, which is worse: there is no warning to ignore.

    // THE HABITS THAT AVOID IT:
    //
    //   1. Wrap when you mean a value:      list.remove(Integer.valueOf(id))
    //   2. Or be explicit about position:   list.remove(list.indexOf(id))
    //   3. Or avoid List<Integer> for ids entirely and use a Set or a Map,
    //      which have no such overload pair.
    var fixed = new java.util.ArrayList<Integer>(java.util.List.of(100, 2, 200, 300));
    fixed.remove(Integer.valueOf(idToDrop));
    IO.println("wrapped properly:                     " + fixed + "   <- dropped the id 2");
}

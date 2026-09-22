// EXPECT: compile-error
//
// Two methods that differ only in return type. Java refuses this.
//
// The error:
//
//     error: method parse(String) is already defined in class ReturnTypeIsNotASignature
//
// The reason: the compiler chooses an overload from the ARGUMENTS at the call
// site. Write `parse("1");` as a bare statement and there is nothing to choose
// from, so the rule is simply that the parameter lists must differ.
//
// If you need both, give them different names. parseInt and parseDouble is
// exactly why the standard library is written that way.

void main() {
    IO.println(parse("1"));
}

int parse(String s) {
    return Integer.parseInt(s);
}

double parse(String s) {
    return Double.parseDouble(s);
}

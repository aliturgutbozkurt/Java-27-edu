// EXPECT: compile-error
//
// One of the three things var cannot do, shown rather than described.
//
// The error:
//
//     VarCannotInferNull.java:16: error: cannot infer type for local variable x
//         var x = null;
//             ^
//       (variable initializer is 'null')
//
// The compiler is not being fussy. null belongs to every reference type at
// once, so there is nothing to infer. Write the type you meant.

void main() {
    var x = null;
    IO.println(x);
}

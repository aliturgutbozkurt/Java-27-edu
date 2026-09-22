// EXPECT: compile-error
//
// A switch over a sealed type that misses a case. This is the error you WANT to
// get, six months from now, when a colleague adds a new payment method.
//
// The error:
//
//     error: the switch expression does not cover all possible input values
//         return switch (payment) {
//                ^
//       missing patterns:
//           Voucher _
//
// It names the missing type. Not "something is wrong somewhere", but exactly
// which case you forgot.
//
// The underscore in `Voucher _` is an unnamed pattern: the compiler is saying
// you need a branch for Voucher, and that a branch which ignores the value
// would do.
//
// THE POINT: adding Voucher to the permits clause turns every incomplete switch
// in the whole codebase into a build failure that points at itself. Writing
// `default -> 0` instead would have shipped a voucher with no fee.

public class NotExhaustive {

    public static void main(String[] args) {
        System.out.println(fee(new Card("1234")));
    }

    static String fee(Payment payment) {
        return switch (payment) {
            case Card c -> "1.5% on " + c.last4();
            case Transfer t -> "flat 20p to " + t.sortCode();
        };
    }
}

sealed interface Payment permits Card, Transfer, Voucher {
}

record Card(String last4) implements Payment {
}

record Transfer(String sortCode) implements Payment {
}

record Voucher(String code) implements Payment {
}

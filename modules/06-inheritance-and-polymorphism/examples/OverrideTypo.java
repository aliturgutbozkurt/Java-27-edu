// EXPECT: compile-error
//
// Why @Override is worth writing every single time.
//
// The subclass below means to override deliver(). It writes `delivar()`. With no
// annotation this compiles happily, adds an unrelated method nobody calls, and
// the base implementation keeps running. That bug can survive code review,
// because the code looks exactly right.
//
// @Override turns it into this, at build time:
//
//     error: delivar() in Email does not override or implement a method from a supertype
//         @Override
//         ^
//
// One annotation, one whole category of silent bug removed.

public class OverrideTypo {

    public static void main(String[] args) {
        System.out.println(new Email().deliver());
    }
}

class Message {
    String deliver() {
        return "base delivery";
    }
}

class Email extends Message {

    @Override
    String delivar() {
        return "email delivery";
    }
}

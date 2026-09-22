// extends, super, @Override, and the dispatch that makes it all worthwhile.

public class Inheritance {

    public static void main(String[] args) {
        // The variable's type is Notification. The OBJECT is something more
        // specific. Java picks the method from the object, not the variable.
        Notification[] outbox = {
            new Notification("system reboot"),
            new EmailNotification("system reboot", "ada@example.com"),
            new SmsNotification("system reboot", "+44 7700 900000")
        };

        for (Notification n : outbox) {
            // One call site. Three different methods run.
            System.out.println(n.deliver());
        }

        System.out.println();

        // This is DYNAMIC DISPATCH, and it is the entire point of inheritance.
        // The compiler only checks that Notification HAS a deliver() method.
        // Which deliver() actually runs is decided at runtime from the object.
        //
        // Practical consequence: you can add a fourth notification type without
        // touching this loop. Code that depends on the base type keeps working.

        Notification n = new EmailNotification("hello", "grace@example.com");
        System.out.println("declared type: Notification");
        System.out.println("actual class:  " + n.getClass().getSimpleName());
        System.out.println("dispatches to: " + n.deliver());
    }
}

class Notification {

    // protected: visible to this package AND to subclasses anywhere.
    // Use it deliberately. It is part of your public API for anyone extending
    // the class, and it is much harder to change later than a private field.
    protected final String message;

    Notification(String message) {
        this.message = message;
    }

    String deliver() {
        return "[log] " + message;
    }

    String describe() {
        return "a notification saying '" + message + "'";
    }
}

class EmailNotification extends Notification {

    private final String address;

    EmailNotification(String message, String address) {
        // super(...) calls the parent constructor. Without this line Java would
        // insert a no-argument super(), and Notification has no no-argument
        // constructor, so it would not compile.
        super(message);
        this.address = address;
    }

    // @Override is optional, and you should always write it.
    //
    // It is an instruction to the compiler: "check that I really am overriding
    // something". Misspell the method name or get a parameter type wrong and
    // you get a compile error instead of a method that silently never runs.
    // See OverrideTypo.java for what that looks like.
    @Override
    String deliver() {
        return "[email to " + address + "] " + message;
    }

    @Override
    String describe() {
        // super.describe() calls the PARENT version. Useful when you want to
        // extend behaviour rather than replace it.
        return super.describe() + ", by email";
    }
}

class SmsNotification extends Notification {

    private final String number;

    SmsNotification(String message, String number) {
        super(message);
        this.number = number;
    }

    @Override
    String deliver() {
        return "[sms to " + number + "] " + message;
    }
}

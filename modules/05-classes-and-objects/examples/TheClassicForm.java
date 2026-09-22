// Graduation day.
//
// Every file so far has been a compact source file. This one is written the
// classic way, because that is what you will meet in every real codebase, every
// tutorial and every Stack Overflow answer ever posted.
//
// Nothing new is happening. Module 01 showed, with javap, that the compiler was
// generating a class for you all along. Here you write it yourself.

// `public` means any code anywhere can use this class.
// `class TheClassicForm` must match the file name, TheClassicForm.java.
//   That rule applies to public classes and is why Java files often hold one
//   class each.
public class TheClassicForm {

    // Dissecting the line you will type a thousand times:
    //
    //   public   anyone can call it, and the JVM needs to
    //   static   belongs to the class, so the JVM can call it WITHOUT first
    //            constructing an object. That is the important one: at startup
    //            there is no object yet.
    //   void     returns nothing. The exit code comes from System.exit or from
    //            finishing normally.
    //   main     the name the JVM looks for. Nothing else will do.
    //   String[] args   command-line arguments, as text.
    //
    public static void main(String[] args) {

        // System.out.println rather than IO.println. IO is available in compact
        // source files; here we use the form that works everywhere.
        System.out.println("Running the classic way.");

        // args is never null. With no arguments it is an empty array, so this
        // is safe without a check.
        System.out.println("received " + args.length + " argument(s)");
        for (String arg : args) {
            System.out.println("  " + arg);
        }

        // Try it:  java TheClassicForm.java hello world

        // `this` does NOT exist here, because main is static. There is no
        // particular object it was called on. That is the one real difference
        // from the compact form, where main was an instance method.
        //
        // To use instance state, something has to construct an object first,
        // which is exactly what main usually does:
        TheClassicForm app = new TheClassicForm();
        app.run();
    }

    // An instance method. It has a `this` and can reach instance fields.
    private void run() {
        System.out.println("now running on an actual object: " + this.getClass().getSimpleName());
    }
}

// WHICH FORM SHOULD YOU USE?
//
// For learning, scripts and single-file tools, the compact form is less noise.
// For anything with more than one file, a library, or a codebase other people
// touch, use the classic form. It is what the tooling, the conventions and
// every reader expect.
//
// They are the same language. You are choosing how much to type, not what the
// program means.

// Reference solution for Homework 15.

import java.util.*;

public class ContactBook {

    // The field is a plain String, not an Optional. The ACCESSOR is what makes
    // the absence visible to callers.
    //
    // Optional as a field would cost an object per contact, make the record
    // non-Serializable, and buy nothing: the class's own methods already know
    // how to handle a null of their own field.
    record Contact(String name, String email, String phoneOrNull) {

        Optional<String> phone() {
            return Optional.ofNullable(phoneOrNull);
        }
    }

    private static final Map<String, Contact> BOOK = Map.of(
            "ada", new Contact("ada", "ada@example.com", "555-0100"),
            "grace", new Contact("grace", "grace@example.com", null),
            "alan", new Contact("alan", "alan@example.com", "555-0199"));

    private static int defaultLookups = 0;

    public static void main(String[] args) {
        partOne();
        System.out.println();
        partTwo();
        System.out.println();
        partThree();
        System.out.println();
        partFour();
    }

    // ---------------------------------------------------------------- part 1

    private static void partOne() {
        System.out.println("--- lookups without unwrapping ---");

        for (String name : List.of("ada", "grace", "nobody")) {
            // No isPresent anywhere. Each line describes what to do with the
            // value and what to fall back to.
            String phone = find(name)
                    .flatMap(Contact::phone)
                    .orElse("no phone on file");

            String domain = find(name)
                    .map(Contact::email)
                    .filter(e -> e.contains("@"))
                    .map(e -> e.substring(e.indexOf('@') + 1))
                    .orElse("unknown");

            System.out.printf("  %-7s phone=%-18s domain=%s%n", name, phone, domain);
        }

        System.out.println();
        System.out.println("--- flatMap was required, not optional ---");

        // Contact::phone already returns an Optional<String>. Using map here
        // would give Optional<Optional<String>>, which no orElse(String) will
        // accept, so the compiler catches it. That is the usual way this
        // mistake is found.
        Optional<Optional<String>> nested = find("ada").map(Contact::phone);
        System.out.println("  map:     " + nested);
        System.out.println("  flatMap: " + find("ada").flatMap(Contact::phone));

        System.out.println();
        System.out.println("--- chained fallbacks with or() ---");

        // or() takes an Optional, so alternatives compose. orElse would have
        // forced a value at the first step and ended the chain.
        String resolved = find("nobody")
                .or(() -> find("also-nobody"))
                .or(() -> find("ada"))
                .map(Contact::name)
                .orElse("none of them exist");
        System.out.println("  " + resolved);

        System.out.println();
        System.out.println("--- bridging to a stream ---");

        // Optional.stream() yields zero or one element, so the misses vanish
        // without a separate filter step.
        System.out.println("  " + List.of("ada", "nobody", "alan").stream()
                .map(ContactBook::find)
                .flatMap(Optional::stream)
                .map(Contact::name)
                .toList());
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo() {
        System.out.println("--- the orElse trap, counted ---");

        defaultLookups = 0;
        String a = find("ada").map(Contact::email).orElse(lookupDefaultEmail());
        System.out.println("  orElse    on a PRESENT optional -> " + a);
        System.out.println("            defaultLookups = " + defaultLookups);

        defaultLookups = 0;
        String b = find("ada").map(Contact::email).orElseGet(ContactBook::lookupDefaultEmail);
        System.out.println("  orElseGet on the same           -> " + b);
        System.out.println("            defaultLookups = " + defaultLookups);

        defaultLookups = 0;
        String c = find("nobody").map(Contact::email).orElseGet(ContactBook::lookupDefaultEmail);
        System.out.println("  orElseGet on an EMPTY optional  -> " + c);
        System.out.println("            defaultLookups = " + defaultLookups);

        // WHY, in my own words:
        //
        // orElse takes a VALUE. Java evaluates every argument before entering
        // the method, so lookupDefaultEmail() runs, produces a string, and that
        // string is passed in. Only then does orElse look at the Optional,
        // decide it is present, and discard what it was handed.
        //
        // orElseGet takes a SUPPLIER. Nothing is computed at the call site; a
        // lambda is passed in and orElse invokes it only on the empty path.
        //
        // WHY IT IS NOT MERELY A PERFORMANCE NOTE:
        //
        // The counter above is a side effect. In real code that would be a
        // database round trip, a log line, or a row inserted. orElse performs
        // it unconditionally, so a "default" that should never have been needed
        // still happens, and the bug leaves no trace at the call site because
        // the returned value is correct.
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree() {
        System.out.println("--- four anti-patterns, fixed ---");

        Optional<Contact> found = find("ada");

        // 1. isPresent + get. An if-null check with extra typing.
        //
        //        if (found.isPresent()) {
        //            System.out.println(found.get().email());
        //        }
        //
        // Every such pair has a direct replacement. This one is map + orElse.
        System.out.println("  1. " + found.map(Contact::email).orElse("none"));

        // 2. Optional as a field. Fixed by the record above: the field is a
        //    plain String and phone() returns the Optional.
        System.out.println("  2. field is plain, accessor returns Optional: "
                + find("grace").flatMap(Contact::phone).orElse("absent"));

        // 3. Optional as a parameter. Fixed with an overload, so callers write
        //    neither Optional.of nor Optional.empty.
        System.out.println("  3. " + describe("ada"));
        System.out.println("     " + describe("ada", "work"));

        // 4. Optional<List<T>>. An empty list already means "nothing here", so
        //    wrapping it gives the caller a third state to handle for no gain.
        System.out.println("  4. no matches -> " + searchByDomain("nowhere.com"));
        System.out.println("     matches    -> " + searchByDomain("example.com").size() + " found");

        // 5. get() itself, replaced by orElseThrow with a message that says
        //    what was expected.
        try {
            find("nobody").orElseThrow(() ->
                    new NoSuchElementException("contact 'nobody' should have been seeded"));
        } catch (NoSuchElementException e) {
            System.out.println("  5. orElseThrow message: " + e.getMessage());
        }
        System.out.println("     compare get()'s message: \"No value present\", which");
        System.out.println("     names neither the lookup nor the key.");
    }

    // ---------------------------------------------------------------- part 4

    private static void partFour() {
        System.out.println("--- what Optional does not protect you from ---");

        // A method declared to return Optional, returning null. The type says
        // one thing and the code does another, and nothing in the language
        // stops it.
        Optional<String> broken = brokenLookup();
        System.out.println("  brokenLookup() returned: " + broken);

        try {
            System.out.println(broken.orElse("fallback"));
        } catch (NullPointerException e) {
            System.out.println("  calling anything on it threw NullPointerException");
        }

        // This is the worst of both worlds. The caller trusted the type, wrote
        // no null check because the signature promised none was needed, and got
        // an NPE anyway.
        //
        // It is also why Optional is a CONVENTION and not a guarantee. Rust and
        // Kotlin have the compiler behind them. Java has a class and an
        // agreement, and an agreement can be broken by any method that forgets.
        //
        // The rule that follows: a method returning Optional must never return
        // null. Return Optional.empty(). There is no case where null is the
        // better answer.
    }

    // ---------------------------------------------------------------- helpers

    static Optional<Contact> find(String name) {
        return Optional.ofNullable(BOOK.get(name));
    }

    @SuppressWarnings("OptionalAssignedToNull")
    static Optional<String> brokenLookup() {
        return null;
    }

    static String lookupDefaultEmail() {
        defaultLookups++;
        return "default@example.com";
    }

    // The overload pair that replaces an Optional parameter.
    static String describe(String name) {
        return describe(name, null);
    }

    static String describe(String name, String label) {
        String base = find(name).map(Contact::email).orElse("unknown");
        return label == null ? base : label + ": " + base;
    }

    // Returns an empty list, never an Optional of one.
    static List<String> searchByDomain(String domain) {
        return BOOK.values().stream()
                .filter(c -> c.email().endsWith("@" + domain))
                .map(Contact::name)
                .sorted()
                .toList();
    }
}

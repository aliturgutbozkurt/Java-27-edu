// EXPECT: runtime-error
//
// Calling get() on an empty Optional.
//
//     Exception in thread "main" java.util.NoSuchElementException: No value present
//
// This is the failure Optional was supposed to prevent, reintroduced by using
// Optional as though it were a box you simply open.
//
// WHY IT IS WORSE THAN A NULL POINTER EXCEPTION, not better:
//
//   - an NPE at least tells you what was null. Since Java 14 the message names
//     the exact expression. "No value present" names nothing at all.
//   - the code LOOKS safe. Optional appears in the type, so a reviewer skims
//     past it, which is not true of a raw dereference.
//   - you wrote extra ceremony and got a worse diagnostic.
//
// THE ALTERNATIVES, all of which are one method call:
//
//     opt.orElse(fallback)                       a default value
//     opt.orElseGet(() -> compute())             a computed default
//     opt.orElseThrow(() -> new AppException(    an exception that SAYS something
//             "user " + id + " should exist"))
//     opt.map(...).orElse(...)                   transform, then default
//     opt.ifPresent(...)                         do something only if present
//
// get() has no legitimate use that orElseThrow() does not cover better, and
// orElseThrow with a supplier lets you explain what you expected. That is why
// JDK 10 added orElseThrow() as a no-argument alias: it does the same thing as
// get() while reading as a deliberate choice rather than an oversight.

import java.util.Map;
import java.util.Optional;

public class UnguardedGet {

    record User(String name) { }

    static final Map<Integer, User> USERS = Map.of(1, new User("ada"));

    public static void main(String[] args) {
        System.out.println("looking up a user that exists:");
        System.out.println("  " + findUser(1).get().name());

        System.out.println("now one that does not:");
        System.out.println("  " + findUser(99).get().name());
    }

    static Optional<User> findUser(int id) {
        return Optional.ofNullable(USERS.get(id));
    }
}

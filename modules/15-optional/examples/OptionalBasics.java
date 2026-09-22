// Optional makes "might be absent" visible in the type, so a caller cannot
// forget to think about it.
//
// It was added for ONE job: a return type for a method that may legitimately
// find nothing. Used for that, it is excellent. Used anywhere else, it is
// usually worse than the null it replaced.

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OptionalBasics {

    record User(String name, String email) { }

    static final Map<Integer, User> USERS = Map.of(
            1, new User("ada", "ada@example.com"),
            2, new User("grace", "grace@example.com"));

    public static void main(String[] args) {
        System.out.println("--- creating one ---");

        System.out.println("  of(value):      " + Optional.of("present"));
        System.out.println("  empty():        " + Optional.empty());
        System.out.println("  ofNullable(x):  " + Optional.ofNullable("present"));
        System.out.println("  ofNullable(null): " + Optional.ofNullable(null));

        // of() REJECTS null. That is deliberate: if you write Optional.of(x)
        // you are asserting x is there, and the NPE tells you your assertion
        // was wrong. Use ofNullable when you genuinely do not know.
        try {
            Optional.of(null);
        } catch (NullPointerException e) {
            System.out.println("  of(null) threw NullPointerException, as designed");
        }

        System.out.println();
        System.out.println("--- the signature is the documentation ---");

        // Compare these two:
        //
        //     User findUser(int id)              might return null. might not.
        //                                        the only way to know is to
        //                                        read the implementation or
        //                                        the javadoc, if there is one.
        //
        //     Optional<User> findUser(int id)    says it, in the type. the
        //                                        caller cannot ignore it
        //                                        without writing something
        //                                        that visibly ignores it.
        //
        // That is the entire value proposition. It is about the CALLER's
        // obligation being checked, not about avoiding null internally.

        System.out.println("  findUser(1): " + findUser(1));
        System.out.println("  findUser(9): " + findUser(9));

        System.out.println();
        System.out.println("--- using it without unwrapping ---");

        // The idiomatic style never asks "is it there". It describes what to do
        // with the value if it is, and what to fall back to if it is not.
        System.out.println("  map:            "
                + findUser(1).map(User::email).orElse("no email"));
        System.out.println("  map on empty:   "
                + findUser(9).map(User::email).orElse("no email"));

        System.out.println("  filter:         "
                + findUser(1).filter(u -> u.name().startsWith("a")).map(User::name).orElse("no match"));

        System.out.println("  ifPresentOrElse:");
        findUser(2).ifPresentOrElse(
                u -> System.out.println("    found " + u.name()),
                () -> System.out.println("    nobody there"));

        // or() supplies an alternative OPTIONAL, not an alternative value, so
        // chains of fallbacks compose.
        System.out.println("  or:             "
                + findUser(9).or(() -> findUser(1)).map(User::name).orElse("none"));

        System.out.println();
        System.out.println("--- map versus flatMap ---");

        // If your function returns a plain value, use map.
        // If it already returns an Optional, map would give you
        // Optional<Optional<T>>. flatMap unwraps one level.
        Optional<Optional<String>> doubled = findUser(1).map(u -> Optional.of(u.email()));
        System.out.println("  map with an Optional-returning fn: " + doubled);

        Optional<String> flat = findUser(1).flatMap(u -> Optional.of(u.email()));
        System.out.println("  flatMap:                            " + flat);

        // Which is the same rule as Stream.map versus Stream.flatMap. When a
        // chain suddenly has nested Optionals in it, you wanted flatMap.

        System.out.println();
        System.out.println("--- and it bridges to streams ---");

        List<Integer> ids = List.of(1, 9, 2);

        // Optional.stream() yields zero or one element, so flatMapping over it
        // drops the misses without a filter step.
        System.out.println("  found users: " + ids.stream()
                .map(OptionalBasics::findUser)
                .flatMap(Optional::stream)
                .map(User::name)
                .toList());
    }

    // The one legitimate use: a lookup that may find nothing.
    static Optional<User> findUser(int id) {
        return Optional.ofNullable(USERS.get(id));
    }
}

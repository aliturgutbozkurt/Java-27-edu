// Optional used wrongly is worse than the null it replaced, because it adds
// ceremony without adding safety. Here are the five ways that happens.

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AntiPatterns {

    record User(String name, Optional<String> nickname) { }   // see anti-pattern 2

    public static void main(String[] args) {
        System.out.println("--- 1. isPresent followed by get ---");

        Optional<String> maybe = Optional.of("value");

        // The wrong way. This is an if-null check with more typing, and it
        // gains you nothing at all.
        if (maybe.isPresent()) {
            System.out.println("  wrong: " + maybe.get().toUpperCase());
        }

        // The right way. Say what to do with the value; never ask whether it is
        // there.
        System.out.println("  right: " + maybe.map(String::toUpperCase).orElse("ABSENT"));

        // Almost every isPresent/get pair can become map, filter,
        // ifPresent, ifPresentOrElse, orElse or orElseGet. If yours cannot,
        // that is worth a second look at the surrounding design.

        System.out.println();
        System.out.println("--- 2. Optional as a FIELD ---");

        // The record above has Optional<String> nickname. Do not do this.
        //
        //   - Optional is not Serializable, so the enclosing class is not either
        //   - it adds an object per instance, which matters at scale
        //   - a field can simply be null, and the class's own methods are
        //     already responsible for handling that
        //
        // Return an Optional from the ACCESSOR instead. The type then says the
        // right thing to callers without imposing on the representation.
        System.out.println("  a field should be a plain String; the GETTER returns Optional");
        System.out.println("  " + new BetterUser("ada", null).nickname());
        System.out.println("  " + new BetterUser("grace", "amazing").nickname());

        System.out.println();
        System.out.println("--- 3. Optional as a PARAMETER ---");

        //   void register(String name, Optional<String> nickname)
        //
        // Now every caller must write Optional.empty() or Optional.of(x), which
        // is noise, and they can still pass null, so you have gained nothing
        // and lost readability. Use an overload:
        System.out.println("  " + register("ada"));
        System.out.println("  " + register("grace", "amazing"));

        System.out.println();
        System.out.println("--- 4. Optional wrapping a collection ---");

        //   Optional<List<String>> findTags()
        //
        // Now the caller has THREE cases to handle: absent, present-and-empty,
        // present-and-populated. An empty list already means "nothing here".
        System.out.println("  wrong: Optional<List<T>>, three states to handle");
        System.out.println("  right: return an empty list, " + tags(false));
        System.out.println("         populated,          " + tags(true));

        System.out.println();
        System.out.println("--- 5. Optional in a hot loop ---");

        // Every Optional is an allocation. In code that runs millions of times
        // a plain null check is faster, and this is one of the few places where
        // that argument actually holds. Everywhere else, prefer clarity.
        System.out.println("  allocation per call; fine for an API, think twice in a tight loop");

        System.out.println();
        System.out.println("--- what Optional was NOT for ---");
        System.out.println("  it is a RETURN TYPE for lookups that may find nothing.");
        System.out.println("  it is not a general-purpose null wrapper, and Java's");
        System.out.println("  designers said so when they added it.");
    }

    record BetterUser(String name, String nicknameOrNull) {
        // The field is plain. The accessor makes the absence explicit for
        // whoever calls it.
        Optional<String> nickname() {
            return Optional.ofNullable(nicknameOrNull);
        }
    }

    static String register(String name) {
        return register(name, null);
    }

    static String register(String name, String nickname) {
        return nickname == null ? "registered " + name : "registered " + name + " as " + nickname;
    }

    static List<String> tags(boolean any) {
        return any ? List.of("java", "streams") : List.of();
    }
}

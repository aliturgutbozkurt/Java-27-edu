// Interfaces can carry implementations. Understanding WHY explains a lot about
// how Java evolves.

import java.util.List;

public class DefaultMethods {

    public static void main(String[] args) {
        Playlist rock = new Playlist(List.of("Kashmir", "Black Dog"));

        // Implemented by the class.
        System.out.println("tracks: " + rock.tracks());

        // Provided by the interface as a default. Playlist never wrote it.
        System.out.println("count:  " + rock.count());
        System.out.println("empty:  " + rock.isEmpty());
        System.out.println("shout:  " + rock.shoutFirst());

        System.out.println();

        // A static method on the interface, used as a factory.
        Collection empty = Collection.empty();
        System.out.println("empty collection count: " + empty.count());
    }
}

interface Collection {

    // The one method an implementer must write.
    List<String> tracks();

    // A DEFAULT METHOD has a body. Implementers get it for free and may
    // override it if they can do better.
    //
    // WHY THESE EXIST:
    //
    // Before Java 8, adding a method to an interface broke every existing
    // implementation, everywhere, immediately. That made interfaces in public
    // libraries effectively frozen forever.
    //
    // Java 8 needed to add stream() to java.util.Collection. Without default
    // methods that single addition would have broken every collection class
    // ever written by anyone. Defaults were added to make that possible.
    //
    // So the feature exists for LIBRARY EVOLUTION. It is not a way to sneak
    // multiple inheritance in, and using it as one leads to the mess in
    // DiamondConflict.java.
    default int count() {
        return tracks().size();
    }

    default boolean isEmpty() {
        return count() == 0;
    }

    // A PRIVATE INTERFACE METHOD, added in Java 9. It exists so that several
    // default methods can share code without exposing that helper to everyone
    // who implements the interface.
    private String emphasise(String text) {
        return text.toUpperCase() + "!";
    }

    default String shoutFirst() {
        return tracks().isEmpty() ? emphasise("nothing") : emphasise(tracks().get(0));
    }

    // A STATIC INTERFACE METHOD. Belongs to the interface itself and is not
    // inherited by implementers. Useful for factories, which is why the
    // standard library has List.of, Map.of and Comparator.comparing.
    static Collection empty() {
        return List::of;
        // That is a lambda, because Collection has exactly one abstract method.
        // Module 13 covers the syntax. Note what it means: an interface with
        // one abstract method can be implemented without writing a class.
    }
}

class Playlist implements Collection {

    private final List<String> tracks;

    Playlist(List<String> tracks) {
        this.tracks = List.copyOf(tracks);
    }

    @Override
    public List<String> tracks() {
        return tracks;
    }

    // count(), isEmpty() and shoutFirst() are inherited from the interface.
    // Overriding count() would be reasonable if this class could answer faster
    // than size() does.
}

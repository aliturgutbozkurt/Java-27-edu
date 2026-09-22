// Writing your own exception types, and the one thing people forget: the cause.

import java.io.IOException;

public class CustomExceptions {

    public static void main(String[] args) {
        System.out.println("--- with the cause preserved ---");
        try {
            loadUserProperly(7);
        } catch (UserLoadException e) {
            System.out.println("  message: " + e.getMessage());
            System.out.println("  userId:  " + e.userId());
            System.out.println("  caused by: " + e.getCause());

            // The whole chain is available, which is what makes a stack trace
            // useful three layers into a framework.
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            System.out.println("  root cause: " + root);
        }

        System.out.println();
        System.out.println("--- with the cause thrown away ---");
        try {
            loadUserBadly(7);
        } catch (UserLoadException e) {
            System.out.println("  message: " + e.getMessage());
            System.out.println("  caused by: " + e.getCause());
            System.out.println("  the original IOException is gone. good luck.");
        }
    }

    // THE RIGHT WAY. Wrap, add context, and pass the original as the cause.
    //
    // The cause is the second constructor argument, and forgetting it is the
    // most common mistake in exception handling. Without it you get a stack
    // trace that starts where you rethrew, which tells you nothing about what
    // actually failed.
    static void loadUserProperly(int userId) throws UserLoadException {
        try {
            readFromDisk(userId);
        } catch (IOException e) {
            throw new UserLoadException("could not load user " + userId, userId, e);
        }
    }

    // THE WRONG WAY. Same wrapping, cause discarded.
    static void loadUserBadly(int userId) throws UserLoadException {
        try {
            readFromDisk(userId);
        } catch (IOException e) {
            throw new UserLoadException("could not load user " + userId, userId);
        }
    }

    static void readFromDisk(int userId) throws IOException {
        throw new IOException("users/" + userId + ".json: no such file");
    }
}

// WHEN TO WRITE YOUR OWN:
//
//   - the caller needs to distinguish this failure from others
//   - you want to carry structured data, like the userId below
//   - you are crossing an abstraction boundary and the underlying exception
//     type would leak an implementation detail
//
// WHEN NOT TO: if IllegalArgumentException or IllegalStateException says it,
// use those. A codebase with forty bespoke exception types nobody catches
// individually has forty classes doing the job of two.
//
// CHECKED OR UNCHECKED?
//
//   extends Exception          checked. callers must handle or declare it.
//   extends RuntimeException   unchecked. callers may ignore it.
//
// Ask whether a caller can realistically do something other than log and give
// up. If not, unchecked is honest and keeps signatures clean.
class UserLoadException extends Exception {

    private final int userId;

    // Always provide a constructor that takes a cause. Always.
    UserLoadException(String message, int userId, Throwable cause) {
        super(message, cause);
        this.userId = userId;
    }

    UserLoadException(String message, int userId) {
        super(message);
        this.userId = userId;
    }

    // Structured data beats parsing the message string. A caller that needs the
    // id can ask for it instead of pulling it back out of the text.
    int userId() {
        return userId;
    }
}

// Fields, visibility, and why `private` is the default you should reach for.

public class Encapsulation {

    public static void main(String[] args) {
        BankAccount account = new BankAccount("Ada", 100_00);

        System.out.println(account.owner() + " has " + account.balanceInCents());

        account.deposit(50_00);
        System.out.println("after deposit: " + account.balanceInCents());

        // This is the whole point of private: the compiler stops nonsense at
        // the door rather than letting it happen and hunting for it later.
        //
        //     account.balanceInCents = -999;
        //     error: balanceInCents has private access in BankAccount
        //
        // Without encapsulation, any code anywhere could put this account into
        // a state the class says is impossible.

        try {
            account.withdraw(1_000_00);
        } catch (IllegalArgumentException e) {
            System.out.println("refused: " + e.getMessage());
        }

        System.out.println("balance is still: " + account.balanceInCents());
    }
}

class BankAccount {

    // THE FOUR VISIBILITY LEVELS, narrowest first:
    //
    //   private      this class only
    //   (none)       this package only. Called "package-private" or "default".
    //   protected    this package, plus subclasses anywhere
    //   public       everyone
    //
    // Start at private and widen only when you have a reason. Widening later is
    // easy; narrowing later breaks everyone who depended on it.
    private final String owner;

    // `final` on a field means assigned exactly once, in the constructor. After
    // that the compiler refuses any reassignment. It is not the same as
    // immutable: a final field can still point at a mutable object.
    private long balanceInCents;

    BankAccount(String owner, long openingBalance) {
        this.owner = owner;
        this.balanceInCents = openingBalance;
    }

    // Accessors. Note the naming: `owner()` rather than `getOwner()`.
    //
    // The getX convention comes from the JavaBeans specification of 1997 and
    // some frameworks still require it. Modern Java, and records in particular,
    // use the short form. Follow whatever the codebase you are in already does;
    // consistency beats either preference.
    String owner() {
        return owner;
    }

    long balanceInCents() {
        return balanceInCents;
    }

    // The real reason to keep the field private: every change goes through a
    // method that can enforce the rules.
    void deposit(long cents) {
        if (cents <= 0) {
            throw new IllegalArgumentException("deposit must be positive");
        }
        balanceInCents += cents;
    }

    void withdraw(long cents) {
        if (cents <= 0) {
            throw new IllegalArgumentException("withdrawal must be positive");
        }
        if (cents > balanceInCents) {
            throw new IllegalArgumentException(
                    "insufficient funds: have " + balanceInCents + ", asked for " + cents);
        }
        balanceInCents -= cents;
    }

    // A class with public mutable fields has no invariants, because anything can
    // change them at any time. A class with private fields and guarded methods
    // can promise that a balance is never negative, and keep that promise.
}

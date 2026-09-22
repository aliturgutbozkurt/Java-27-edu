// Reference solution for Homework 17.
//
// Every value here is fixed or seeded, so the output is identical on every run
// and in every time zone this happens to execute in.

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class Scheduler {

    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final ZoneId LONDON = ZoneId.of("Europe/London");

    // A fixed clock, injected rather than read from the machine. Without this
    // the "next invoice date" test below could only be run on one day of the
    // year, and would then start failing tomorrow.
    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2026-01-31T09:00:00Z"), ZoneOffset.UTC);

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
        System.out.println("--- monthly billing across short months ---");

        LocalDate start = LocalDate.now(CLOCK);
        System.out.println("  signed up: " + start);

        LocalDate naive = start;
        for (int i = 1; i <= 4; i++) {
            naive = naive.plusMonths(1);
            System.out.println("    naive +1 month x" + i + ": " + naive);
        }

        // THE BUG, in my own words:
        //
        // plusMonths clamps to the last valid day, so 31 January becomes 28
        // February. That part is correct and unavoidable.
        //
        // The damage is that the clamp is PERMANENT. Adding another month to
        // the 28th gives 28 March, not 31 March, so a customer who signed up on
        // the 31st is billed on the 28th forever after one short month. The
        // anniversary has silently moved.

        System.out.println();
        System.out.println("  anchored to the original day instead:");

        for (int i = 1; i <= 4; i++) {
            System.out.println("    anchored +" + i + " months: " + billingDate(start, i));
        }

        // THE FIX: always compute from the ORIGINAL date rather than from the
        // previous result. Errors then cannot accumulate, because each answer
        // is derived independently.
    }

    // Always measured from the anchor, never from the last result.
    private static LocalDate billingDate(LocalDate anchor, int monthsElapsed) {
        return anchor.plusMonths(monthsElapsed);
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo() {
        System.out.println("--- a meeting across daylight saving ---");

        // A recurring 09:00 New York meeting, scheduled either side of the
        // spring transition on 8 March 2026.
        ZonedDateTime beforeDst = ZonedDateTime.of(2026, 3, 6, 9, 0, 0, 0, NEW_YORK);
        ZonedDateTime afterDst = beforeDst.plusDays(3);

        System.out.println("  Fri 6 Mar, 09:00 NY: " + beforeDst);
        System.out.println("  plusDays(3):         " + afterDst);
        System.out.println("  real hours elapsed:  "
                + ChronoUnit.HOURS.between(beforeDst, afterDst) + " (not 72)");

        System.out.println("  plusHours(72):       " + beforeDst.plusHours(72));

        // WHY THEY DIFFER, in my own words:
        //
        // plusDays is a CALENDAR operation. It keeps the wall-clock time and
        // lets the offset change, so the meeting stays at 09:00 local, which is
        // what everyone in the room expects.
        //
        // plusHours is an ELAPSED-TIME operation. It adds exactly 72 hours of
        // real time, and because one of those days was 23 hours long it lands
        // at 10:00 local.
        //
        // Neither is wrong. They answer different questions, and the bug is
        // using one where the other was meant. For a recurring meeting you want
        // plusDays; for a timeout you want plusHours.

        System.out.println();
        System.out.println("  what London sees for the same two moments:");
        System.out.println("    before: " + beforeDst.withZoneSameInstant(LONDON));
        System.out.println("    after:  " + afterDst.withZoneSameInstant(LONDON));

        // The UK changes on a different date, so for three weeks each spring
        // the usual five-hour gap becomes four. Anyone hard-coding the offset
        // between two cities is wrong twice a year.

        System.out.println();
        System.out.println("--- the ambiguous hour ---");

        LocalDateTime ambiguous = LocalDateTime.of(2026, 11, 1, 1, 30);
        ZonedDateTime earlier = ambiguous.atZone(NEW_YORK);
        ZonedDateTime later = earlier.withLaterOffsetAtOverlap();

        System.out.println("  01:30 on 1 Nov happens twice:");
        System.out.println("    earlier: " + earlier);
        System.out.println("    later:   " + later);
        System.out.println("    apart:   " + Duration.between(earlier, later).toHours() + " hour");

        // atZone silently picks the earlier one. For a booking system that is a
        // decision being made by default rather than on purpose, and the two
        // instants are genuinely an hour apart.

        System.out.println();
        System.out.println("--- and the hour that does not exist ---");

        LocalDateTime gap = LocalDateTime.of(2026, 3, 8, 2, 30);
        System.out.println("  02:30 on 8 Mar was never on the clock");
        System.out.println("  atZone moves it forward: " + gap.atZone(NEW_YORK));
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree() {
        System.out.println("--- four Math traps ---");

        // 1. abs can be negative.
        int hash = Integer.MIN_VALUE;
        // 7, not 8. MIN_VALUE happens to be divisible by 8, so both forms
        // would return 0 and the negative-index trap would stay invisible.
        int buckets = 7;
        System.out.println("  1. Math.abs(MIN_VALUE)      = " + Math.abs(hash));
        System.out.println("     abs(hash) % buckets      = " + (Math.abs(hash) % buckets));
        System.out.println("     floorMod(hash, buckets)  = " + Math.floorMod(hash, buckets));

        // The int range is asymmetric: there is no positive counterpart to
        // MIN_VALUE, so negating it wraps back to itself. abs(hash) % buckets
        // can therefore be negative, which is an array index that crashes for
        // one hash value in four billion. floorMod is always in range.

        // 2. Integer division versus floorDiv.
        System.out.println("  2. -7 / 2                   = " + (-7 / 2));
        System.out.println("     Math.floorDiv(-7, 2)     = " + Math.floorDiv(-7, 2));

        // Division truncates toward zero; floorDiv rounds toward negative
        // infinity. For "which page is item -7 on" the second is usually meant.

        // 3. round is asymmetric.
        System.out.println("  3. Math.round(2.5)          = " + Math.round(2.5));
        System.out.println("     Math.round(-2.5)         = " + Math.round(-2.5));

        // round adds 0.5 and floors, so it rounds half toward positive
        // infinity rather than away from zero. -2.5 gives -2.

        // 4. Silent overflow.
        int big = Integer.MAX_VALUE;
        System.out.println("  4. MAX_VALUE + 1            = " + (big + 1));
        try {
            Math.addExact(big, 1);
        } catch (ArithmeticException e) {
            System.out.println("     Math.addExact threw:     " + e.getMessage());
        }

        // Wrapping is silent. For anything counting money, memory or retries, a
        // crash is a better outcome than a number that has quietly become
        // negative.
    }

    // ---------------------------------------------------------------- part 4

    private static void partFour() {
        System.out.println("--- reproducible sampling ---");

        List<String> pool = List.of("ada", "grace", "alan", "barbara", "edsger", "katherine");

        // The factory creates the generator. RandomGenerator.of returns a
        // generator directly and has no create method, which is the mistake I
        // made first and the compiler caught.
        System.out.println("  seed 2026, run A: " + sample(pool, 3, 2026));
        System.out.println("  seed 2026, run B: " + sample(pool, 3, 2026));
        System.out.println("  seed 7,    run C: " + sample(pool, 3, 7));

        // Runs A and B are identical because the seed is. That is what makes a
        // test assertable. An unseeded generator here would produce a test that
        // passes locally and fails in CI once a month, with no way to reproduce
        // the failing case.

        System.out.println();
        System.out.println("  and what NOT to use it for:");
        System.out.println("    session ids, password reset tokens, API keys");
        System.out.println("    those need java.security.SecureRandom, because everything");
        System.out.println("    above is predictable once an attacker sees enough output");

        System.out.println();
        System.out.println("--- formatting the report ---");

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEE dd MMM yyyy", Locale.UK);

        // The Locale is explicit. Without it this line formats differently on a
        // server configured for another language, which is the same class of
        // bug as relying on the platform default charset used to be.
        LocalDate today = LocalDate.now(CLOCK);
        System.out.println("  " + "%-14s %s".formatted("generated:", today.format(formatter)));
        System.out.println("  " + "%-14s %s".formatted("next bill:",
                billingDate(today, 1).format(formatter)));
        System.out.println("  " + "%-14s %s".formatted("month end:",
                today.with(TemporalAdjusters.lastDayOfMonth()).format(formatter)));
        System.out.println("  " + "%-14s %,d".formatted("accounts:", 1_234_567));
    }

    private static List<String> sample(List<String> pool, int count, long seed) {
        RandomGenerator rng = RandomGeneratorFactory.of("L64X128MixRandom").create(seed);
        return rng.ints(count, 0, pool.size()).mapToObj(pool::get).toList();
    }
}

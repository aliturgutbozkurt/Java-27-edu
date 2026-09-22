// java.time, added in Java 8, replaced Date and Calendar for good reasons. It
// is immutable, unambiguous about time zones, and its arithmetic is defined
// rather than surprising.
//
// Every example here uses a FIXED clock or fixed dates, so the output is the
// same on every run. That is also the technique for testing time-dependent code.

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

public class DateAndTime {

    public static void main(String[] args) {
        System.out.println("--- choosing the right type ---");

        //   LocalDate        a date, no time, no zone.       a birthday
        //   LocalTime        a time, no date, no zone.       opening hours
        //   LocalDateTime    both, still NO zone.            a wall clock
        //   ZonedDateTime    an actual moment somewhere.     a meeting
        //   Instant          a point on the UTC timeline.    a timestamp
        //   Duration         time-based amount: hours, seconds
        //   Period           date-based amount: years, months, days

        System.out.println("  LocalDate:     " + LocalDate.of(2026, 9, 23));
        System.out.println("  LocalTime:     " + LocalTime.of(10, 15, 30));
        System.out.println("  LocalDateTime: " + LocalDateTime.of(2026, 9, 23, 10, 15, 30));
        System.out.println("  Instant:       " + Instant.parse("2026-09-23T10:15:30Z"));

        // LocalDateTime has NO ZONE. "2026-09-23T10:15" is not a moment in
        // time; it is a moment in some unstated place. Storing one and assuming
        // it means UTC is the most common java.time bug.

        System.out.println();
        System.out.println("--- a fixed clock, so this output never changes ---");

        Clock clock = Clock.fixed(Instant.parse("2026-09-23T10:15:30Z"), ZoneOffset.UTC);
        System.out.println("  LocalDate.now(clock): " + LocalDate.now(clock));
        System.out.println("  Instant.now(clock):   " + Instant.now(clock));

        // Every now() method takes an optional Clock. Code that calls now()
        // with no argument cannot be tested for "what happens on 29 February".
        // Take a Clock as a dependency and the problem disappears.

        System.out.println();
        System.out.println("--- immutable, like String ---");

        LocalDate date = LocalDate.of(2026, 1, 31);
        date.plusDays(1);                       // result discarded
        System.out.println("  after an ignored plusDays: " + date);
        System.out.println("  after keeping it:          " + date.plusDays(1));

        System.out.println();
        System.out.println("--- arithmetic that clamps rather than overflows ---");

        System.out.println("  31 Jan + 1 month:  " + LocalDate.of(2026, 1, 31).plusMonths(1));
        System.out.println("  29 Feb + 1 year:   " + LocalDate.of(2024, 2, 29).plusYears(1));

        // Both CLAMP to the last valid day rather than rolling into the next
        // month. That is a deliberate choice and it is not reversible:
        System.out.println("  and back again:    "
                + LocalDate.of(2026, 1, 31).plusMonths(1).minusMonths(1));
        System.out.println("  so +1 month then -1 month is NOT the identity.");

        System.out.println();
        System.out.println("--- Duration versus Period ---");

        Duration duration = Duration.between(
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 2, 3, 0));
        System.out.println("  Duration: " + duration + "  (" + duration.toHours() + " hours)");

        Period period = Period.between(LocalDate.of(2026, 1, 1), LocalDate.of(2027, 3, 5));
        System.out.println("  Period:   " + period
                + "  (" + period.getYears() + "y " + period.getMonths() + "m " + period.getDays() + "d)");

        // Duration is time-based and exact. Period is date-based and
        // calendar-aware. A Period of one month is 28, 29, 30 or 31 days
        // depending on where you apply it, which is why the two types exist.

        System.out.println("  ChronoUnit.DAYS: "
                + ChronoUnit.DAYS.between(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 1)));

        System.out.println();
        System.out.println("--- adjusters ---");

        LocalDate anchor = LocalDate.of(2026, 9, 23);
        System.out.println("  first of month:  " + anchor.with(TemporalAdjusters.firstDayOfMonth()));
        System.out.println("  last of month:   " + anchor.with(TemporalAdjusters.lastDayOfMonth()));
        System.out.println("  next Monday:     " + anchor.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));

        System.out.println();
        System.out.println("--- formatting and parsing ---");

        DateTimeFormatter uk = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.UK);
        System.out.println("  formatted: " + anchor.format(uk));
        System.out.println("  ISO:       " + anchor.format(DateTimeFormatter.ISO_DATE));
        System.out.println("  parsed:    " + LocalDate.parse("2026-09-23"));

        // Always pass a Locale to ofPattern. Without one it uses the machine's
        // default, so the same code produces "Sept" here and something else on
        // a server in another country. That is the same class of bug as the
        // default charset before Java 18.

        try {
            LocalDate.parse("23/09/2026");
        } catch (java.time.format.DateTimeParseException e) {
            System.out.println("  parse failure names the position: " + e.getMessage());
        }

        System.out.println();
        System.out.println("--- why not Date and Calendar ---");
        System.out.println("  Date is mutable, so any method can change yours");
        System.out.println("  Calendar months are ZERO-BASED: January is 0");
        System.out.println("  Date.getYear() returns the year minus 1900");
        System.out.println("  neither carries a time zone properly");
        System.out.println("  java.time fixes all four, and is not going to change again");
    }
}

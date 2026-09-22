// Time zones are where date handling goes wrong, and daylight saving is where
// time zones go wrong.
//
// All dates here are fixed, so the output is identical on every run and in
// every zone the code happens to execute in.

import java.time.*;
import java.time.temporal.ChronoUnit;

public class TimeZonesAndDst {

    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final ZoneId LONDON = ZoneId.of("Europe/London");
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");

    public static void main(String[] args) {
        System.out.println("--- one instant, three wall clocks ---");

        Instant moment = Instant.parse("2026-09-23T14:00:00Z");
        System.out.println("  instant:  " + moment);
        System.out.println("  New York: " + moment.atZone(NEW_YORK));
        System.out.println("  London:   " + moment.atZone(LONDON));
        System.out.println("  Tokyo:    " + moment.atZone(TOKYO));

        // An Instant is a point on the timeline and has no zone. A
        // ZonedDateTime is that same point expressed somewhere. Converting
        // between them changes the presentation, never the moment.

        System.out.println();
        System.out.println("--- the spring forward gap ---");

        // On 8 March 2026 New York skips from 02:00 straight to 03:00.
        ZonedDateTime before = ZonedDateTime.of(2026, 3, 8, 1, 30, 0, 0, NEW_YORK);
        System.out.println("  01:30 EST:        " + before);
        System.out.println("  plus one hour:    " + before.plusHours(1));

        // Notice the result is 03:30, not 02:30, and the offset changed from
        // -05:00 to -04:00. The arithmetic is correct: one real hour passed.

        // A local time in the gap does not exist at all. Java does not throw;
        // it moves forward by the size of the gap, which is a documented choice
        // and worth knowing before it surprises you.
        LocalDateTime nonexistent = LocalDateTime.of(2026, 3, 8, 2, 30);
        System.out.println("  02:30 does not exist; atZone gives: " + nonexistent.atZone(NEW_YORK));

        System.out.println();
        System.out.println("--- the autumn fall-back overlap ---");

        // On 1 November 2026 New York repeats 01:00 to 02:00.
        LocalDateTime ambiguous = LocalDateTime.of(2026, 11, 1, 1, 30);
        ZonedDateTime firstPass = ambiguous.atZone(NEW_YORK);
        ZonedDateTime secondPass = firstPass.withLaterOffsetAtOverlap();

        System.out.println("  01:30 happens twice:");
        System.out.println("    first  (EDT): " + firstPass);
        System.out.println("    second (EST): " + secondPass);
        System.out.println("  one hour apart: "
                + ChronoUnit.HOURS.between(firstPass, secondPass));

        // atZone picks the EARLIER offset by default. If your application needs
        // the later one, you must say so. Neither choice is wrong, and silently
        // assuming either is how bookings get double-counted.

        System.out.println();
        System.out.println("--- days are not always 24 hours ---");

        ZonedDateTime dayBefore = ZonedDateTime.of(2026, 3, 7, 12, 0, 0, 0, NEW_YORK);
        ZonedDateTime dayAfter = dayBefore.plusDays(1);

        System.out.println("  noon on the 7th: " + dayBefore);
        System.out.println("  plusDays(1):     " + dayAfter);
        System.out.println("  real hours elapsed: "
                + ChronoUnit.HOURS.between(dayBefore, dayAfter));

        System.out.println("  plusHours(24):   " + dayBefore.plusHours(24));

        // plusDays keeps the wall-clock time and crosses 23 real hours.
        // plusHours(24) adds 24 real hours and lands at a different wall clock.
        //
        // THIS IS THE DISTINCTION THAT MATTERS. "Same time tomorrow" and
        // "twenty-four hours from now" are different operations, and on two
        // days a year they give different answers.

        System.out.println();
        System.out.println("--- the rules that keep you out of trouble ---");
        System.out.println("  store an Instant or UTC, convert for display only");
        System.out.println("  use a ZoneId like 'Europe/London', never a fixed offset,");
        System.out.println("    because offsets change twice a year and regions change rules");
        System.out.println("  never store a LocalDateTime for a real moment");
        System.out.println("  for a future appointment store the zone too, since the rules");
        System.out.println("    may change between now and then");
    }
}

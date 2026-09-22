// Collectors turn a stream back into something you can hold. groupingBy alone
// justifies learning the whole API.

import java.util.*;
import java.util.stream.Collectors;

public class CollectingResults {

    record Employee(String name, String department, int salary) { }

    static final List<Employee> STAFF = List.of(
            new Employee("ada", "engineering", 95_000),
            new Employee("grace", "engineering", 105_000),
            new Employee("alan", "research", 88_000),
            new Employee("barbara", "research", 92_000),
            new Employee("edsger", "design", 78_000));

    public static void main(String[] args) {
        System.out.println("--- the simple ones ---");

        // toList() on the stream itself is the modern form and returns an
        // UNMODIFIABLE list. Collectors.toList() returns a modifiable one but
        // makes no promise about which type. Prefer .toList().
        System.out.println("  toList:  " + STAFF.stream().map(Employee::name).toList());
        System.out.println("  toSet:   " + STAFF.stream().map(Employee::department)
                .collect(Collectors.toCollection(TreeSet::new)));
        System.out.println("  joining: " + STAFF.stream().map(Employee::name)
                .collect(Collectors.joining(", ", "[", "]")));

        System.out.println();
        System.out.println("--- groupingBy, the one that earns its keep ---");

        Map<String, List<Employee>> byDept = STAFF.stream()
                .collect(Collectors.groupingBy(Employee::department));
        byDept.forEach((dept, people) -> System.out.println("  " + dept + ": "
                + people.stream().map(Employee::name).toList()));

        System.out.println();
        System.out.println("--- groupingBy with a downstream collector ---");

        // The second argument says what to do with each group instead of
        // collecting it to a list. This is where the API stops being verbose
        // and starts being worth it.
        System.out.println("  counting:  " + STAFF.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.counting())));

        System.out.println("  averaging: " + STAFF.stream()
                .collect(Collectors.groupingBy(Employee::department,
                        Collectors.averagingInt(Employee::salary))));

        System.out.println("  mapping:   " + STAFF.stream()
                .collect(Collectors.groupingBy(Employee::department,
                        Collectors.mapping(Employee::name, Collectors.toList()))));

        // Keeping the map ordered takes a third form with the map factory in
        // the middle. Without it you get a HashMap, whose order is undefined.
        System.out.println("  ordered:   " + STAFF.stream()
                .collect(Collectors.groupingBy(Employee::department,
                        TreeMap::new,
                        Collectors.counting())));

        System.out.println();
        System.out.println("--- partitioningBy: exactly two groups ---");

        // Always has both keys, even when one side is empty, which groupingBy
        // on a boolean does not guarantee.
        Map<Boolean, List<String>> split = STAFF.stream()
                .collect(Collectors.partitioningBy(e -> e.salary() > 90_000,
                        Collectors.mapping(Employee::name, Collectors.toList())));
        System.out.println("  over 90k:  " + split.get(true));
        System.out.println("  under:     " + split.get(false));

        System.out.println();
        System.out.println("--- toMap, and its sharp edge ---");

        System.out.println("  name to salary: " + STAFF.stream()
                .collect(Collectors.toMap(Employee::name, Employee::salary)));

        // toMap THROWS on a duplicate key rather than overwriting. That is
        // usually what you want, and it surprises people the first time.
        try {
            STAFF.stream().collect(Collectors.toMap(Employee::department, Employee::name));
        } catch (IllegalStateException e) {
            System.out.println("  duplicate key threw: " + e.getMessage());
        }

        // Supply a merge function to say what should happen instead.
        System.out.println("  with a merge fn: " + STAFF.stream()
                .collect(Collectors.toMap(Employee::department, Employee::name,
                        (a, b) -> a + " & " + b)));

        System.out.println();
        System.out.println("--- summarising ---");

        System.out.println("  " + STAFF.stream()
                .collect(Collectors.summarizingInt(Employee::salary)));

        // teeing runs two collectors over one pass and combines the results.
        String spread = STAFF.stream().collect(Collectors.teeing(
                Collectors.minBy(Comparator.comparingInt(Employee::salary)),
                Collectors.maxBy(Comparator.comparingInt(Employee::salary)),
                (min, max) -> min.get().name() + " earns least, " + max.get().name() + " most"));
        System.out.println("  teeing: " + spread);
    }
}

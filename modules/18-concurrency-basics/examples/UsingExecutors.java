// What to use instead of creating threads yourself.

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class UsingExecutors {

    public static void main(String[] args) throws Exception {
        System.out.println("--- a pool, with try-with-resources ---");

        // ExecutorService has been AutoCloseable since Java 19. close() shuts
        // the pool down and waits for tasks to finish, so this replaces the
        // shutdown/awaitTermination pair people routinely got wrong.
        try (ExecutorService pool = Executors.newFixedThreadPool(3)) {
            for (int i = 1; i <= 5; i++) {
                int id = i;
                pool.submit(() -> System.out.println("  task " + id
                        + " on " + Thread.currentThread().getName()));
            }
        }
        System.out.println("  pool closed, all tasks done");

        System.out.println();
        System.out.println("--- getting results back ---");

        try (ExecutorService pool = Executors.newFixedThreadPool(3)) {
            List<Future<Integer>> futures = new ArrayList<>();
            for (int i = 1; i <= 4; i++) {
                int n = i;
                // submit returns a Future. Callable, unlike Runnable, returns a
                // value AND may throw a checked exception.
                futures.add(pool.submit(() -> n * n));
            }

            for (Future<Integer> future : futures) {
                // get() blocks until that task finishes.
                System.out.println("  result: " + future.get());
            }
        }

        System.out.println();
        System.out.println("--- failures surface at get(), not at submit() ---");

        try (ExecutorService pool = Executors.newFixedThreadPool(1)) {
            Future<Integer> failing = pool.submit(() -> {
                throw new IllegalStateException("the task failed");
            });

            System.out.println("  submitted, and nothing has been reported yet");

            try {
                failing.get();
            } catch (ExecutionException e) {
                // The original exception is the CAUSE, wrapped in an
                // ExecutionException. Module 10's point about always keeping
                // the cause is exactly why this is usable.
                System.out.println("  get() threw ExecutionException");
                System.out.println("  caused by: " + e.getCause());
            }
        }

        // THIS IS A REAL TRAP. A task submitted with submit() that throws and
        // whose Future is never inspected fails SILENTLY. Nothing is logged and
        // nothing crashes. Use execute() if you want the default handler to
        // report it, or always inspect the Future.

        System.out.println();
        System.out.println("--- invokeAll waits for everything ---");

        try (ExecutorService pool = Executors.newFixedThreadPool(3)) {
            List<Callable<String>> tasks = List.of(
                    () -> "alpha",
                    () -> "beta",
                    () -> "gamma");

            List<Future<String>> results = pool.invokeAll(tasks);
            for (Future<String> f : results) {
                System.out.println("  " + f.get());
            }
        }

        System.out.println();
        System.out.println("--- choosing a pool ---");
        System.out.println("  newFixedThreadPool(n)   bounded. the safe default for CPU work,");
        System.out.println("                          sized around the number of cores");
        System.out.println("  newCachedThreadPool()   unbounded. convenient and dangerous,");
        System.out.println("                          since a burst of tasks makes a burst of");
        System.out.println("                          threads and nothing stops it");
        System.out.println("  newSingleThreadExecutor tasks run one at a time, in order");
        System.out.println("  newScheduledThreadPool  delayed and repeating tasks");
        System.out.println();
        System.out.println("  available processors here: " + Runtime.getRuntime().availableProcessors());
        System.out.println();
        System.out.println("  For IO-bound work all of these force an awkward choice between");
        System.out.println("  too few threads and too many. Module 19 removes the choice.");
    }
}

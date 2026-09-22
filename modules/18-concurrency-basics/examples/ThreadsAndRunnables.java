// Starting threads directly. You should almost never do this in real code, and
// knowing why requires seeing it first.

public class ThreadsAndRunnables {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- starting a thread ---");
        System.out.println("  main is running on: " + Thread.currentThread().getName());

        // A Thread takes a Runnable, which is a functional interface, so a
        // lambda works. Module 13's rules apply unchanged.
        Thread worker = new Thread(() ->
                System.out.println("  worker is running on: " + Thread.currentThread().getName()),
                "worker-1");

        worker.start();

        // start() and run() are NOT the same. start() creates a new thread and
        // calls run() on it. Calling run() directly just invokes a method on
        // the current thread, which is a mistake that looks like it works
        // because the output is identical when there is nothing to interleave.
        worker.join();   // wait for it to finish

        System.out.println();
        System.out.println("--- order is not guaranteed ---");

        Thread[] threads = new Thread[4];
        for (int i = 0; i < threads.length; i++) {
            int id = i;
            threads[i] = new Thread(() -> System.out.println("  thread " + id + " ran"));
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        // The numbers above may appear in any order, and the order can differ
        // between runs on the same machine. Anything that relies on it is
        // already broken; it merely has not failed yet.

        System.out.println();
        System.out.println("--- daemon threads do not keep the JVM alive ---");

        Thread daemon = new Thread(() -> {
            try {
                Thread.sleep(60_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        daemon.setDaemon(true);
        daemon.start();
        System.out.println("  started a daemon that sleeps for a minute");
        System.out.println("  the JVM will exit anyway, because no non-daemon thread remains");

        System.out.println();
        System.out.println("--- interruption is a REQUEST, not a kill ---");

        Thread interruptible = new Thread(() -> {
            try {
                Thread.sleep(10_000);
                System.out.println("  never printed");
            } catch (InterruptedException e) {
                // Catching InterruptedException CLEARS the interrupt flag, so
                // restoring it is how you tell code further up that this thread
                // was asked to stop. Swallowing it silently is a common bug.
                Thread.currentThread().interrupt();
                System.out.println("  worker noticed the interrupt and stopped cleanly");
            }
        });
        interruptible.start();
        Thread.sleep(50);
        interruptible.interrupt();
        interruptible.join();

        // There is a Thread.stop() method. It has been deprecated since 2000
        // and removed in recent releases, because killing a thread mid-operation
        // leaves whatever it was mutating in an unknown state. Cooperative
        // interruption is the only safe mechanism.

        System.out.println();
        System.out.println("--- why not to create threads yourself ---");
        System.out.println("  a platform thread costs about 1MB of stack and an OS call");
        System.out.println("  creating one per task does not scale past a few thousand");
        System.out.println("  there is no way to bound how many exist");
        System.out.println("  nothing collects results or propagates failures");
        System.out.println();
        System.out.println("  UsingExecutors.java shows what to do instead, and Module 19");
        System.out.println("  shows what changed when threads stopped being expensive.");
    }
}

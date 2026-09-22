// Random number generation, and how to make it reproducible so tests can exist.
//
// Every generator here is SEEDED, so this file prints the same numbers on every
// run. That is both the point and the technique.

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class RandomNumbers {

    public static void main(String[] args) {
        System.out.println("--- the modern interface ---");

        // RandomGenerator, added in Java 17, is the interface. java.util.Random
        // implements it, and so do several better algorithms.
        RandomGenerator defaultGen = RandomGenerator.getDefault();
        System.out.println("  getDefault() gives: " + defaultGen.getClass().getSimpleName());

        System.out.println();
        System.out.println("--- seeded, therefore reproducible ---");

        // A factory produces a generator; the generator itself is not a factory.
        RandomGenerator first = RandomGeneratorFactory.of("L64X128MixRandom").create(42);
        RandomGenerator second = RandomGeneratorFactory.of("L64X128MixRandom").create(42);

        System.out.println("  seed 42, run A: " + first.ints(5, 0, 100).boxed().toList());
        System.out.println("  seed 42, run B: " + second.ints(5, 0, 100).boxed().toList());
        System.out.println("  identical, which is what makes a test possible");

        RandomGenerator different = RandomGeneratorFactory.of("L64X128MixRandom").create(43);
        System.out.println("  seed 43:        " + different.ints(5, 0, 100).boxed().toList());

        // A test that shuffles or samples needs a fixed seed, or it fails once
        // a month for reasons nobody can reproduce. Take the generator as a
        // parameter, exactly as DateAndTime.java took a Clock.

        System.out.println();
        System.out.println("--- generating what you actually want ---");

        RandomGenerator rng = RandomGeneratorFactory.of("L64X128MixRandom").create(7);

        System.out.println("  nextInt(6) + 1 (a die):  " + (rng.nextInt(6) + 1));
        System.out.println("  nextInt(10, 20) (range): " + rng.nextInt(10, 20));
        System.out.println("  nextDouble():            " + rng.nextDouble());
        System.out.println("  nextBoolean():           " + rng.nextBoolean());
        System.out.println("  nextGaussian():          " + rng.nextGaussian());

        // nextInt(bound) is EXCLUSIVE of the bound. nextInt(low, high) is
        // inclusive of low and exclusive of high, matching every other range
        // API in the library.

        System.out.println();
        System.out.println("--- streams of random values ---");

        RandomGenerator streamGen = RandomGeneratorFactory.of("L64X128MixRandom").create(99);
        System.out.println("  ints:    " + streamGen.ints(5, 1, 7).boxed().toList());
        System.out.println("  doubles: " + RandomGeneratorFactory.of("L64X128MixRandom")
                .create(99).doubles(3).boxed().map(d -> String.format("%.3f", d)).toList());

        System.out.println();
        System.out.println("--- do NOT use these for security ---");

        // Every generator above is a pseudo-random algorithm. Given enough
        // output, its future values are predictable, which is fine for a game
        // and fatal for a password reset token or a session id.
        //
        // For anything security-relevant use SecureRandom, which draws from the
        // operating system's entropy source:
        //
        //     var secure = new java.security.SecureRandom();
        //     byte[] token = new byte[32];
        //     secure.nextBytes(token);
        //
        // It is deliberately not shown running here, because its output cannot
        // be reproduced and this file is meant to print the same thing twice.
        System.out.println("  use java.security.SecureRandom for tokens, ids and keys");
        System.out.println("  the generators above are predictable by design");

        System.out.println();
        System.out.println("--- and do not share one across threads ---");
        System.out.println("  java.util.Random is synchronised, so it becomes a bottleneck");
        System.out.println("  ThreadLocalRandom.current() gives each thread its own");
        System.out.println("  RandomGeneratorFactory also offers splittable and jumpable");
        System.out.println("    algorithms designed for parallel work");
    }
}

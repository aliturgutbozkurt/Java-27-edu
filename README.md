# Java 27, for Programmers New to Java

A self-paced curriculum for developers who already write code in another
language and now need Java. It does not re-teach what a variable or a loop is.
It teaches how Java does things, and where Java will surprise you.

Twenty-two modules, each with a lesson, runnable examples whose comments explain
the *why*, a set of key takeaways, and homework. Reference solutions live in a
separate tree so you are not spoiled by accident.

---

## What "Java 27" means here

JDK 27 reached General Availability on 15 September 2026. It is **not** an LTS
release, and its delivered features are almost entirely runtime-level: G1 as the
default collector, compact object headers, post-quantum TLS key exchange, JFR
redaction. Nothing in that list changes how you write Java.

So this curriculum is honest about its own title:

> **JDK 27 is the runtime and toolchain. The language you learn is the stable
> Java accumulated through JDK 25 LTS.**

The final module covers what JDK 26 and 27 actually changed, why non-LTS releases
matter less than the version number suggests, and how to read a JEP for yourself.

---

## Requirements

**JDK 27.** That is the only requirement. No Maven, no Gradle, no third-party
libraries anywhere in this repository.

Check what you have:

```bash
java -version
```

You want `java version "27"`. If you see something else:

```bash
# macOS, once JDK 27 is installed
export JAVA_HOME=$(/usr/libexec/java_home -v 27)

# Linux, typical layout
export JAVA_HOME=/usr/lib/jvm/jdk-27
export PATH="$JAVA_HOME/bin:$PATH"
```

To install it, take a build from [Adoptium](https://adoptium.net/),
[Oracle](https://www.oracle.com/java/technologies/downloads/), or your package
manager. On macOS, `brew install openjdk@27` works.

---

## Running an example

Every example is a single file you run directly. No compile step, no build file:

```bash
java modules/01-getting-started/examples/HelloWorld.java
```

That works because single-file source launch compiles in memory and runs in one
go. Module 01 explains what is actually happening.

A handful of examples need a preview feature, and say so in their first line.
Those are run with:

```bash
java --enable-preview --source 27 path/to/Example.java
```

---

## Verifying everything still runs

```bash
./scripts/verify-examples.sh                          # the whole repository
./scripts/verify-examples.sh modules/01-getting-started   # a single module
```

The script runs every example and every reference solution, and fails if any of
them misbehaves. It refuses to run at all on a JDK other than 27, because an
example failing for toolchain reasons teaches you nothing.

Examples can declare what they expect in their first ten lines:

| Marker | Meaning |
|---|---|
| *(none)* | Compiles and runs, exits 0 |
| `// EXPECT: compile-error` | Must **fail** to compile; it is teaching a compile error |
| `// EXPECT: preview` | Run with `--enable-preview --source 27` |
| `// EXPECT: compile-only` | Must compile; not run on its own |
| `// EXPECT: runtime-error` | Must compile, then **crash** at runtime; it is teaching a failure |
| `// EXPECT: assertions` | Run with `-ea`, so `assert` statements are live |

---

## Layout

```
modules/<nn>-<name>/
├── README.md      the lesson, ending in Key Takeaways
├── examples/      runnable files, heavily commented
└── homework/      the assignment and its acceptance criteria

solutions/<nn>-<name>/
└── reference solutions, kept out of the lesson tree on purpose
```

Try the homework before opening the solution. The solution is there to check
yourself against, not to read first.

---

## Curriculum

The module list, build order, and progress live in
[tasks/todo.md](tasks/todo.md). The specification this project is built against
is [SPEC.md](SPEC.md), and the implementation plan is
[tasks/plan.md](tasks/plan.md).

Modules are written in order, because each one's prose assumes you have read the
ones before it.

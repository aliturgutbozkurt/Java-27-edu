# Homework 01: About Me

Build a small interactive program using only what Module 01 covered, then break
it on purpose and read what the compiler tells you.

## The Task

Create a file called `AboutMe.java` anywhere you like outside this repository,
for example in a scratch folder. Write it as a **compact source file**, meaning no
`class` declaration and no `static`.

It should:

1. Ask for the user's name.
2. Ask for the year they were born.
3. Ask for the current year.
4. Print a small profile that includes their name and their approximate age,
   where age is the current year minus the birth year.

Sample run, with typed input shown after the prompts:

```
What is your name? Ada
What year were you born? 1990
What year is it now? 2026

--- About Ada ---
Name: Ada
Age this year: 36
```

## Then Break It

Once it works:

1. Delete a semicolon somewhere in the middle of the file.
2. Run it again.
3. Add a comment at the bottom of the file recording the exact error message,
   including the line number and the caret line.
4. Put the semicolon back.

This step is the actual point of the assignment. You will read thousands of
compiler errors in your career and the first one should not happen under
pressure.

## Acceptance Criteria

- [ ] The file runs with `java AboutMe.java`, with no separate `javac` step.
- [ ] It has no `class` declaration and no `static` keyword.
- [ ] It uses `IO.readln` to ask questions and `IO.println` to print.
- [ ] The age is computed with arithmetic, not typed in by the user.
- [ ] The age prints as a number, not concatenated text. `"Age: " + year - birth`
      will not compile, and working out why is part of the exercise.
- [ ] A comment at the bottom records a real compiler error you produced and read.

## Stretch

Run `javap` against your own file and find the class name the compiler invented:

```bash
javac -d /tmp/hw01 AboutMe.java
javap -cp /tmp/hw01 AboutMe
```

Does the generated class match what Module 01 said it would? What is the method
signature of `main`, and is it static?

## Hint, if you are stuck on the age

`IO.readln` always hands you a `String`, even when the user types digits. Java
will not quietly treat `"1990"` as a number. Look again at how
[`AskingForInput.java`](../examples/AskingForInput.java) turns text into an `int`.

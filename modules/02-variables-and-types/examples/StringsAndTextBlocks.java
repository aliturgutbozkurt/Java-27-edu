// Strings are objects, they are immutable, and both facts have consequences.

void main() {
    // IMMUTABLE means no method ever changes the String you called it on.
    String name = "ada";
    name.toUpperCase();                  // computes "ADA" and throws it away
    IO.println("after calling toUpperCase and ignoring it: " + name);

    name = name.toUpperCase();           // you have to keep the result
    IO.println("after assigning the result: " + name);

    // That first line is a genuine mistake people make, and the compiler will
    // not warn you. Every String method returns a NEW String.

    // == vs equals, the String edition.
    String s1 = "java";
    String s2 = "java";
    String s3 = new String("java");

    IO.println("literal == literal : " + (s1 == s2));   // true
    IO.println("literal == new     : " + (s1 == s3));   // false
    IO.println("equals             : " + s1.equals(s3)); // true

    // Identical literals are the same object because the compiler pools them.
    // `new String(...)` forces a separate object, so == fails while the content
    // is the same. This is the Integer cache trap wearing different clothes:
    // == asks "same object", equals asks "same content". On objects you almost
    // always want equals.

    // Comparing against a possibly-null variable, the safe way round:
    String maybeNull = null;
    IO.println("literal.equals(null) : " + "java".equals(maybeNull));  // false, no crash
    // Writing maybeNull.equals("java") would have thrown a NullPointerException.
    // Putting the literal on the left is a cheap habit that avoids a whole
    // category of crash.

    // TEXT BLOCKS: multi-line strings without escape soup.
    String json = """
        {
          "name": "Ada",
          "year": 1990
        }""";
    IO.println(json);

    // The indentation you see in the source is not all in the string. Java
    // strips the common leading whitespace, measured from the least-indented
    // line, so the block lines up with your code without polluting the value.
    IO.println("line count: " + json.lines().count());

    // Useful String methods you will reach for constantly:
    String messy = "  Hello, World  ";
    IO.println("[" + messy.strip() + "]");
    IO.println(messy.contains("World"));
    IO.println("a,b,c".split(",").length);
    IO.println(String.join("-", "2026", "09", "23"));
    IO.println("ab".repeat(3));
    IO.println("Hello".isEmpty() + " " + "   ".isBlank());

    // Building a String in a loop with + creates a new object every iteration.
    // For a handful of pieces that is fine. For thousands, use StringBuilder:
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 5; i++) {
        sb.append(i).append(",");
    }
    IO.println(sb.toString());
}

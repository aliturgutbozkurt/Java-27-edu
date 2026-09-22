// Methods dispatch on the object. Fields and static methods do not.
//
// That asymmetry catches people out, and knowing it explains a whole class of
// confusing bugs.

public class WhatIsNotPolymorphic {

    public static void main(String[] args) {
        Parent asParent = new Child();

        // INSTANCE METHODS: chosen from the actual object. This is the polymorphism
        // you want.
        System.out.println("instance method: " + asParent.instanceName());

        // FIELDS: chosen from the DECLARED type. The reference says Parent, so
        // you get Parent's field, even though the object is a Child.
        System.out.println("field access:    " + asParent.label);

        // Both fields exist on the object simultaneously. Child did not replace
        // Parent's field, it added a second one that hides it.
        System.out.println("as a Child:      " + ((Child) asParent).label);

        // STATIC METHODS: chosen from the declared type too. A static method in
        // a subclass HIDES the parent's rather than overriding it.
        System.out.println("static method:   " + Parent.staticName());
        System.out.println("static on Child: " + Child.staticName());

        System.out.println();
        System.out.println("The object is genuinely a " + asParent.getClass().getSimpleName()
                + ", yet the field and the static method came from Parent.");

        // WHY: dynamic dispatch costs a lookup at runtime. Fields and statics
        // are resolved at compile time, which is faster and, for fields, avoids
        // a whole category of fragility.
        //
        // WHAT TO DO ABOUT IT: never shadow a field in a subclass, and never
        // redeclare a static method with the same name as the parent's. Both are
        // legal, neither does what it appears to, and there is no good reason to
        // write either. Some compilers warn; the language allows it.
        //
        // This is also why Module 04 said making a method static blocks you
        // later: a static method cannot be overridden, so it cannot participate
        // in any of this.
    }
}

class Parent {
    String label = "parent field";

    String instanceName() {
        return "Parent.instanceName";
    }

    static String staticName() {
        return "Parent.staticName";
    }
}

class Child extends Parent {
    // Hides the parent's field rather than replacing it. Do not do this.
    String label = "child field";

    @Override
    String instanceName() {
        return "Child.instanceName";
    }

    // Hides rather than overrides. Note that @Override here would be a compile
    // error, which is the compiler telling you this is not what you think.
    static String staticName() {
        return "Child.staticName";
    }
}

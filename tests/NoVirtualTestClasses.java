package OOP.Tests;

import OOP.Provided.OOP4MethodInvocationFailedException;
import OOP.Provided.OOP4AmbiguousMethodException;
import OOP.Provided.OOP4NoSuchMethodException;
import OOP.Provided.OOP4ObjectInstantiationFailedException;
import OOP.Solution.OOPObject;
import OOP.Solution.OOPParent;

import java.util.List;

public class NoVirtualTestClasses {
    private static List<String> construction_order;

    public static void setConstruction_order(List<String> construction_order) {
        NoVirtualTestClasses.construction_order = construction_order;
    }

    public static List<String> getConstruction_order() {
        return construction_order;
    }

    /*
        Classes for the simple tests, the structures are:
        Simple Structure (check "regular"-style inheritance works):
                          SS_A : OOPObject
                            ^
                            |
                          SS_B : OOPObject

         Multiple Structure (check the multiple inheritance functionality):
                MS_H        MS_H            MS_F
                  |           |               |
                MS_A        MS_B              |
                     \    /                   |
                      MS_C                  MS_E
                          \                /
                            \             /
                              \          /
                                  MS_D


         */
    public static class SS_A extends OOPObject {
        public SS_A() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("A");
        }
        public String public_check() {
            return "public_check_A";
        }

        protected String protected_check() {
            return "protected_check_A";
        }

        private String private_check() {
            return "private_check_A";
        }

        public boolean public_inheritance_check() {
            return true;
        }

//        protected String protected_inheritance_check(Integer a, String b)
//        {
//            return "protected_A_" + a + "_" + b;
//        }

        public String local_parameter_check(String a, Integer b)
        {
            return "This is a constant string.";
        }

        private boolean private_inheritance_check() {
            return true;
        }
    }

    @OOPParent(parent = SS_A.class, isVirtual = false)
    public static class SS_B extends OOPObject {
        SS_B() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("B");
        }

        public String public_check() {
            return "public_check_B";
        }

        protected String protected_check() {
            return "protected_check_B";
        }

        private String private_check() {
            return "private_check_B";
        }

        public String public_local_check() {
            return "public_local_B";
        }

        public String protected_inheritance_check(Integer a, Integer b)
        {
            return "This is a constant string.";
        }

        public String local_parameter_check(Integer a, Integer b)
        {
            return "This is a constant string.";
        }

    }

    public static class MS_H extends OOPObject {
        private static int class_counter = 0;
        private int object_id;
        public MS_H() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("H");
            object_id = class_counter;
            class_counter ++;
        }

        public Integer getObject_id() {
            return object_id;
        }

        public void setObject_id(Integer object_id) {
            this.object_id = object_id;
        }

        public String inherent_ambiguity() {
            return "inherent_H";
        }

        public void ambiguous_to_override() {}
    }

    public static class MS_F extends OOPObject {
        public MS_F() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("F");
        }
        public String accidental_ambiguity() {
            return "accidental_F";
        }
    }

    @OOPParent(parent = MS_H.class)
    public static class MS_A extends OOPObject {
        public MS_A() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("A");
        }
        public String accidental_ambiguity() {
            return "accidental_A";
        }
        public void A_mutate_H_field(Integer newval) throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException, OOP4MethodInvocationFailedException {
            this.invoke("setObject_id", newval);
        }
        public int A_get_H_field() throws OOP4NoSuchMethodException, OOP4MethodInvocationFailedException, OOP4AmbiguousMethodException {
            return (Integer) this.invoke("getObject_id");
        }
    }

    @OOPParent(parent = MS_H.class)
    public static class MS_B extends OOPObject {
        public MS_B() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("B");
        }
        public String unique_function_B() {
            return "unique_B";
        }
        public int B_get_H_field() throws OOP4NoSuchMethodException, OOP4MethodInvocationFailedException, OOP4AmbiguousMethodException {
            return (Integer) this.invoke("getObject_id");
        }
    }

    @OOPParent(parent = MS_A.class)
    @OOPParent(parent = MS_B.class)
    public static class MS_C extends OOPObject {
        public MS_C() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("C");
        }
    }

    @OOPParent(parent = MS_F.class)
    public static class MS_E extends OOPObject {
        public MS_E() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("E");
        }
    }

    @OOPParent(parent = MS_C.class)
    @OOPParent(parent = MS_E.class)
    public static class MS_D extends OOPObject {
        public MS_D() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("D");
        }

        public void ambiguous_to_override() {}
    }

    /*
    Third inheritance hierarchy: extending a non-OOPObject class.
     */
    public static class AS_Regular {
        public void complexly_inherited_function() {
            // No implementation here, finding this function is already impressive
        }
    }

    public static class AS_Midway extends AS_Regular {
        public void weirdly_inherited_function() {
            // No implementation here, finding this function is already impressive
        }
    }

    @OOPParent(parent = AS_Midway.class)
    public static class AS_Child extends OOPObject {
        public AS_Child() throws OOP4ObjectInstantiationFailedException {
        }
    }
}

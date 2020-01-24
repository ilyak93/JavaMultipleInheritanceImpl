package OOP.Tests;

import OOP.Provided.*;
import OOP.Solution.OOPObject;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

public class OOPObjectSimpleTest {
    private static NoVirtualTestClasses.SS_B simple_structure_root;
    private static NoVirtualTestClasses.MS_D multiple_structure_root;

    @BeforeClass
    public static void initialize_fields() throws OOP4ObjectInstantiationFailedException {
        NoVirtualTestClasses.setConstruction_order(new ArrayList<>());
        simple_structure_root = new NoVirtualTestClasses.SS_B();
        multiple_structure_root = new NoVirtualTestClasses.MS_D();
    }

    public void setup_statics() {
        NoVirtualTestClasses.setConstruction_order(new ArrayList<>());
    }

    /*
    Test methods
     */

    /* ############################### multInheritsFrom ###############################*/
    @Test
    public void testSimpleStructureClassInheritanceBasic1() {

        Assert.assertTrue("multInheritsFrom: basic functionality 1",
                simple_structure_root.multInheritsFrom(NoVirtualTestClasses.SS_A.class));
    }

    @Test
    public void testSimpleStructureClassInheritanceBasic2() {
        Assert.assertFalse("SimpleStructure: The classes shouldn't multiple-extend OOPObjectImpl",
                simple_structure_root.multInheritsFrom(OOPObject.class));
    }

    @Test
    public void testSimpleStructureClassInheritanceBasic3() {
        // Just check they haven't fucked up Java:
        Assert.assertFalse("SimpleStructure: B shouldn't regularly extend A",
                NoVirtualTestClasses.SS_B.class.isAssignableFrom(NoVirtualTestClasses.SS_A.class));
    }

    @Test
    public void testMultipleStructureClassInheritanceBasic4() {
        Assert.assertTrue(multiple_structure_root.multInheritsFrom(NoVirtualTestClasses.MS_H.class));
    }

    @Test
    public void testMultipleStructureClassInheritanceBasic5() throws OOP4ObjectInstantiationFailedException {
        Assert.assertFalse(new NoVirtualTestClasses.MS_C().multInheritsFrom(NoVirtualTestClasses.MS_F.class));
    }

    /* ############################# initialization order ############################ */
    @Test
    public void testSimpleStructureInitOrderBasic1() throws OOP4ObjectInstantiationFailedException {
        setup_statics();
        List<String> expected_order = new ArrayList<>(Arrays.asList("A", "B"));
        NoVirtualTestClasses.SS_B b_obj = new NoVirtualTestClasses.SS_B();
        Assert.assertEquals("SimpleStructure: Incorrect initialization order",
                expected_order,
                NoVirtualTestClasses.getConstruction_order());
    }

    @Test
    public void testMultipleStructureInitOrderBasic1() throws OOP4ObjectInstantiationFailedException {
        setup_statics();
        List<String> expected_order = new ArrayList<>(Arrays.asList("H", "A", "H", "B", "C", "F", "E", "D"));
        NoVirtualTestClasses.MS_D d_obj = new NoVirtualTestClasses.MS_D();
        Assert.assertEquals("MultipleStructure: Incorrect initialization order",
                expected_order,
                NoVirtualTestClasses.getConstruction_order());
    }

    /* ################################ definingObject ############################### */

    @Test
    public void testSimpleStructureDefiningObjectBasic1() throws OOP4ObjectInstantiationFailedException {
        NoVirtualTestClasses.SS_B b_obj = new NoVirtualTestClasses.SS_B();

        try {
            Assert.assertEquals("SimpleStructure: Know which methods you defined yourself",
                    NoVirtualTestClasses.SS_B.class,
                    b_obj.definingObject("public_local_check").getClass());
        } catch (OOP4AmbiguousMethodException | OOP4NoSuchMethodException e) {
            fail("SimpleStructure: Know which methods you defined yourself");
        }
    }

    @Test
    public void testSimpleStructureDefiningObjectBasic2() throws OOP4ObjectInstantiationFailedException {
        NoVirtualTestClasses.SS_B b_obj = new NoVirtualTestClasses.SS_B();

        try {
            Assert.assertEquals("SimpleStructure: Find your superclass given its public method",
                    NoVirtualTestClasses.SS_A.class,
                    b_obj.definingObject("public_inheritance_check").getClass());
        } catch (OOP4NoSuchMethodException | OOP4AmbiguousMethodException e) {
            fail("SimpleStructure: Find your superclass given its public method");
        }
    }

    @Test
    public void testSimpleStructureDefiningObjectBasic3() throws OOP4ObjectInstantiationFailedException {
        NoVirtualTestClasses.SS_B b_obj = new NoVirtualTestClasses.SS_B();

        try {
            Assert.assertEquals("SimpleStructure: Correctly override methods",
                    NoVirtualTestClasses.SS_B.class,
                    b_obj.definingObject("public_check").getClass());
        } catch (OOP4NoSuchMethodException | OOP4AmbiguousMethodException e) {
            fail("SimpleStructure: Correctly override methods");
        }
    }

    @Test(expected = OOP4NoSuchMethodException.class)
    public void testSimpleStructureDefiningObjectBasic4() throws OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException {
        NoVirtualTestClasses.SS_B b_obj = new NoVirtualTestClasses.SS_B();

        try {
            Assert.assertEquals("SimpleStructure: Do not inherit private methods",
                    NoVirtualTestClasses.SS_B.class,
                    b_obj.definingObject("private_check").getClass());
        } catch (OOP4AmbiguousMethodException e) {
            fail("SimpleStructure: Do not find private methods");
        }
    }

    @Test(expected = OOP4NoSuchMethodException.class)
    public void testSimpleStructureDefiningObjectBasic5() throws OOP4NoSuchMethodException, OOP4ObjectInstantiationFailedException {
        NoVirtualTestClasses.SS_B b_obj = new NoVirtualTestClasses.SS_B();

        try {
            b_obj.definingObject("private_inheritance_check");
        } catch (OOP4AmbiguousMethodException e) {
            fail("SimpleStructure: Do not inherit private methods");
        }
    }

    @Test(expected = OOP4NoSuchMethodException.class)
    public void testSimpleStructureDefiningObjectBasic6() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        simple_structure_root.definingObject("random_no_name");
    }

    @Test
    public void testSimpleStructureDefiningObjectBasic7() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        // Test correct lookup with parameters.
        Assert.assertSame(simple_structure_root,
                simple_structure_root.definingObject("local_parameter_check",
                        Integer.class, Integer.class));
    }

    @Test
    public void testSimpleStructureDefiningObjectBasic8() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        // Test correct non-inheritance with parameters (same method name, different argument types).
        Assert.assertSame(simple_structure_root,
                simple_structure_root.definingObject("local_parameter_check", Integer.class, Integer.class));
    }

    @Test
    public void testSimpleStructureDefiningObjectBasic9() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        Assert.assertEquals("SimpleStructure: find your superclass's not-overriden methods",
                NoVirtualTestClasses.SS_A.class,
                simple_structure_root.definingObject("local_parameter_check", String.class, Integer.class).getClass());
    }

    @Test(expected = OOP4AmbiguousMethodException.class)
    public void testMultipleStructureDefiningObjectBasic1() throws OOP4AmbiguousMethodException, OOP4ObjectInstantiationFailedException {
        NoVirtualTestClasses.MS_D d_obj = new NoVirtualTestClasses.MS_D();

        try {
            d_obj.definingObject("inherent_ambiguity");
        } catch (OOP4NoSuchMethodException e) {
            fail("MultipleStructure: need to detect when methods are ambiguous");
        }
    }

    @Test(expected = OOP4AmbiguousMethodException.class)
    public void testMultipleStructureDefiningObjectBasic2() throws OOP4AmbiguousMethodException, OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException {
        // Checks that the implementation can detect accidental ambiguity.
        multiple_structure_root.definingObject("accidental_ambiguity");
    }

    @Test
    public void testMultipleStructureDefiningObjectBasic3() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        // Testing correct method lookup in multiple inheritance
        Assert.assertEquals("MultipleStructure: correct lookup",
                NoVirtualTestClasses.MS_B.class,
                multiple_structure_root.definingObject("unique_function_B").getClass());
    }


    /* ################################ invoke ############################### */
    // At this point, we know (or don't need to check) that the program can find correct objects for which to invoke
    // the method. We only need to check the invocation itself works and does not crash.


    @Test
    public void testSimpleStructureInvocationBasic1() throws OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException, OOP4AmbiguousMethodException {
        NoVirtualTestClasses.SS_B b_obj = new NoVirtualTestClasses.SS_B();

        // Test that invoking a regular local method works (public_local_check).
        String retval = (String) b_obj.invoke("public_local_check");
        Assert.assertEquals("SimpleStructure: wrong method called!", "public_local_B", retval);
    }

    @Test
    public void testSimpleStructureInvocationBasic2() throws OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException, OOP4AmbiguousMethodException {
        NoVirtualTestClasses.SS_B b_obj = new NoVirtualTestClasses.SS_B();

        // Test that invoking a regular overridden method works (public_local_check).
        String retval = (String) b_obj.invoke("public_check");
        Assert.assertEquals("SimpleStructure: wrong method called!", "public_check_B", retval);
    }

    @Test
    public void testSimpleStructureInvocationBasic3() throws OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException, OOP4AmbiguousMethodException {
        NoVirtualTestClasses.SS_B b_obj = new NoVirtualTestClasses.SS_B();

        // Test that invoking a regular inherited method works + invoking a method with parameters works
        // (protected_inheritance_check).
        String retval = (String) b_obj.invoke("protected_inheritance_check",
                32, 23);
        Assert.assertEquals("SimpleStructure: wrong method called!",
                "This is a constant string.", retval);
    }

    @Test(expected=OOP4NoSuchMethodException.class)
    public void testSimpleStructureInvocationBasic4()
            throws OOP4NoSuchMethodException, OOP4ObjectInstantiationFailedException, OOP4AmbiguousMethodException, OOP4MethodInvocationFailedException {
        NoVirtualTestClasses.SS_B b_obj = new NoVirtualTestClasses.SS_B();

        // Test that invoking a private (non-inherited) method does not work (private_inheritance_check).
        b_obj.invoke("protected_inheritance_check", 32, "checking_inheritance");
    }

    @Test(expected = OOP4NoSuchMethodException.class)
    public void testSimpleStructureInvocationBasic5()
            throws OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException, OOP4AmbiguousMethodException, OOP4MethodInvocationFailedException {
        NoVirtualTestClasses.SS_B b_obj = new NoVirtualTestClasses.SS_B();

        // Test that providing wrong parameter types will cause OOP4NoSuchMethod to be thrown.
        b_obj.invoke("protected_inheritance_check",
                "this is a false parameter", "checking_inheritance");
    }

    @Test(expected = OOP4NoSuchMethodException.class)
    public void testSimpleStructureInvocationBasic6()
            throws OOP4NoSuchMethodException, OOP4ObjectInstantiationFailedException, OOP4MethodInvocationFailedException, OOP4AmbiguousMethodException {
        NoVirtualTestClasses.SS_B b_obj = new NoVirtualTestClasses.SS_B();

        // Test that providing a wrong number of parameters will cause
        // OOP4NoSuchMethodException to be thrown.
        b_obj.invoke("protected_inheritance_check",
                32, "checking_inheritance", "another parameter");
    }

    @Test(expected = OOP4NoSuchMethodException.class)
    public void testSimpleStructureInvocationBasic7() throws OOP4NoSuchMethodException, OOP4ObjectInstantiationFailedException, OOP4MethodInvocationFailedException, OOP4AmbiguousMethodException {
        NoVirtualTestClasses.SS_B b_obj = new NoVirtualTestClasses.SS_B();

        // Test that providing a wrong number of parameters will cause
        // OOP4MethodParametersInvalidException to be thrown.
        b_obj.invoke("no_such_method",
                32, "checking_inheritance", "another parameter");
    }

//    public static void main(String[] args) throws OOP4ObjectInstantiationFailedException {
//        NoVirtualTestClasses.setConstruction_order(new ArrayList<>());
//        NoVirtualTestClasses.SS_A a_obj = new NoVirtualTestClasses.SS_A();
//        System.out.println(a_obj.protected_inheritance_check(32, "myFuncCheck"));
//    }
}
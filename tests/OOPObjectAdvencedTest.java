package OOP.Tests;

import OOP.Provided.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;


// TODO: add 1-2 tests for a more complex tree?
// TODO: add advanced virtual inheritance tests.

//TODO: add tests for OOP4MethodInvocationFailedException.

public class OOPObjectAdvencedTest {
    private static NoVirtualTestClasses.AS_Child advanced_structure_root;

    @BeforeClass
    public static void init_fields() throws OOP4ObjectInstantiationFailedException {
        NoVirtualTestClasses.setConstruction_order(new ArrayList<>());
        advanced_structure_root = new NoVirtualTestClasses.AS_Child();
    }

    /* ############################### multInheritsFrom ###############################*/
    @Test
    public void testAdvancedStructureAdvanced1() {
        // Test that we can use our mechanism to extend non-OOPObject classes.
        Assert.assertTrue("RegularInherit: must be able to inherit from non-OOPObject classes as well!",
                advanced_structure_root.multInheritsFrom(NoVirtualTestClasses.AS_Midway.class));
    }

    @Test
    public void testAdvancedStructureAdvanced2() {
        // Test that we can use our mechanism to extend non-OOPObject classes via regular inheritance as well.
        Assert.assertTrue("RegularInherit: must be able to inherit from non-OOPObject classes as well!",
                advanced_structure_root.multInheritsFrom(NoVirtualTestClasses.AS_Regular.class));
    }

    /* ############################# initialization order ############################ */
    @Test
    public void testMultipleStructureInitializationAdvanced1() throws OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException, OOP4AmbiguousMethodException {
        // Test that two ancestors of the same type (non-virtual inheritance) are initialized separately.
        NoVirtualTestClasses.setConstruction_order(new ArrayList<>());
        NoVirtualTestClasses.MS_D d_obj = new NoVirtualTestClasses.MS_D();
        Assert.assertNotEquals("Two distinct ancestors initialized together in non-virtual inheritance",
                d_obj.invoke("A_get_H_field"), d_obj.invoke("B_get_H_field"));
    }

    /* ################################ definingObject ############################### */
    @Test
    public void testAdvancedStructureDefiningObjectAdvanced1()
            throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        // Test the new mechanism can apply to regular classes as well.
        Assert.assertEquals(NoVirtualTestClasses.AS_Midway.class,
                advanced_structure_root.definingObject("weirdly_inherited_function").getClass());
    }

    @Test
    public void testAdvancedStructureDefiningObjectAdvanced2()
            throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        // Test the new mechanism can connect with regular inheritance smoothly.
        Assert.assertEquals(NoVirtualTestClasses.AS_Midway.class,
                advanced_structure_root.definingObject("complexly_inherited_function").getClass());
    }

    @Test
    public void testMultipleStructureDefiningObjectAdvanced1() throws OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        // Checks final overrider
        Assert.assertEquals(NoVirtualTestClasses.MS_D.class,
                new NoVirtualTestClasses.MS_D().definingObject("ambiguous_to_override").getClass());
    }

    /* ################################ invoke ############################### */
    @Test
    public void testMultipleStructureInvocationAdvanced1() throws OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException, OOP4AmbiguousMethodException {
        // Test that mutating an object in the non-virtual inheritance case will actually mutate the ancestor.
        NoVirtualTestClasses.MS_D d_obj = new NoVirtualTestClasses.MS_D();
        int old_val = (Integer) d_obj.invoke("A_get_H_field");
        d_obj.invoke("A_mutate_H_field", old_val + 1);
        // This is the actual test!
        Assert.assertEquals("MultipleStructure - advanced: ancestor mutation not working correctly.",
                old_val + 1, d_obj.invoke("A_get_H_field"));
    }

    @Test
    public void testMultipleStructureInvocationAdvanced2() throws OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException, OOP4AmbiguousMethodException {
        // Test that mutating an object in the non-virtual inheritance case will not mutate other ancestors of
        // the same type.
        NoVirtualTestClasses.MS_D d_obj = new NoVirtualTestClasses.MS_D();
        int old_val = (Integer) d_obj.invoke("B_get_H_field");
        d_obj.invoke("A_mutate_H_field", old_val + 1);
        // This is the actual test!
        Assert.assertNotEquals("MultipleStructure - advanced: ancestor mutation not working correctly.",
                old_val + 1, d_obj.invoke("B_get_H_field"));
    }

    @Test
    public void testAdvancedStructureInvocationAdvanced1() throws OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException, OOP4AmbiguousMethodException {
        // Test invoking functions through regular and new mechanism does not make everything crash.
        NoVirtualTestClasses.AS_Child r_obj = new NoVirtualTestClasses.AS_Child();
        r_obj.invoke("complexly_inherited_function");
    }
}

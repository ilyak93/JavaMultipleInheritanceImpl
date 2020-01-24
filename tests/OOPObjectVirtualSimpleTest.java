package OOP.Tests;

import OOP.Solution.*;
import OOP.Provided.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OOPObjectVirtualSimpleTest {
    private static YesVirtualTestClasses.SD_C simple_virtual_instance;
    private static YesVirtualTestClasses.SD_A simple_diamond_root;

    @BeforeClass
    public static void initialize_fields() throws OOP4ObjectInstantiationFailedException {
        YesVirtualTestClasses.setConstruction_order(new ArrayList<>());
        simple_virtual_instance = new YesVirtualTestClasses.SD_C();
        simple_diamond_root = new YesVirtualTestClasses.SD_A();
    }

    /*
    Things to check that were not covered by the other tests:
        - Initialization order (algorithm from tutorial) - done.
        - Ability to invoke methods on virtual ancestors - done.
        - Distinctness of non virtual instances from the virtual instance - done.
        - Different appearances of the same virtual parent class pointing to the same virtual ancestor (use == ) - done.
        - Check that virtual inheritance solves inherent ambiguity - done.
    */

    /* ############################### multInheritsFrom ###############################*/

    @Test
    public void testSimpleDiamondClassInheritanceBasic1() {
        // Checks virtual inheritance is registered in the system as inheritance.
        Assert.assertTrue(simple_virtual_instance.multInheritsFrom(YesVirtualTestClasses.SD_D.class));
    }

    @Test
    public void testSimpleDiamondClassInheritanceBasic2() {
        // Checks the system recognized inheritance when it passes virtual and then regular inheritance.
        Assert.assertTrue(simple_virtual_instance.multInheritsFrom(YesVirtualTestClasses.SD_E.class));
    }

    @Test
    public void testSimpleDiamondClassInheritanceBasic3() {
        // Checks the system recognized inheritance when it passes regular and then virtual inheritance.
        Assert.assertTrue(simple_diamond_root.multInheritsFrom(YesVirtualTestClasses.SD_D.class));
    }

    /* ############################# initialization order ############################ */

    @Test
    public void testSimpleDiamondInitializationOrderBasic1() throws OOP4ObjectInstantiationFailedException {
        YesVirtualTestClasses.setConstruction_order(new ArrayList<>());
        List<String> expected_order = new ArrayList<>(Arrays.asList("E", "D", "F", "B", "C", "A"));
        YesVirtualTestClasses.SD_A a_obj = new YesVirtualTestClasses.SD_A();
        Assert.assertEquals(expected_order, YesVirtualTestClasses.getConstruction_order());
    }

    /* ################################ definingObject ############################### */

    @Test
    public void testSimpleDiamondDefiningObjectBasic1() throws OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        // Checks classes with single virtual inheritance can inherit methods correctly.
        Assert.assertEquals(YesVirtualTestClasses.SD_D.class,
                new YesVirtualTestClasses.SD_B().definingObject("public_to_inherit").getClass());
    }

    @Test
    public void testSimpleDiamondDefiningObjectBasic2() throws OOP4ObjectInstantiationFailedException {
        // Checks definingObject will give the same object for virtual inheritance.
        YesVirtualTestClasses.SD_A a_obj = new YesVirtualTestClasses.SD_A();
        Object b_instance = null;
        Object c_instance = null;
        try {
            b_instance = ((OOPObject) a_obj.definingObject("no_ambiguity_B"))
                    .definingObject("public_to_inherit");
            c_instance = ((OOPObject) a_obj.definingObject("no_ambiguity_C"))
                    .definingObject("public_to_inherit");
        } catch (OOP4AmbiguousMethodException | OOP4NoSuchMethodException e) {
            fail();
        }
        Assert.assertSame(b_instance, c_instance);
    }

    @Test
    public void testSimpleDiamondDefiningObjectBasic3() throws OOP4NoSuchMethodException, OOP.Provided.OOP4AmbiguousMethodException {
        // Checks virtual diamond inheritance can solve inherent method ambiguity.
        Assert.assertEquals(YesVirtualTestClasses.SD_D.class,
                simple_diamond_root.definingObject("public_to_inherit").getClass());
    }

    @Test
    public void testSimpleDiamondDefiningObjectBasic4() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        // Checks method inheritance flows smoothly through regular (multiple) and virtual inheritance together.
        Assert.assertEquals(YesVirtualTestClasses.SD_E.class,
                simple_diamond_root.definingObject("older_public_method").getClass());
    }

    @Test(expected = OOP4AmbiguousMethodException.class)
    public void testSimpleDiamondDefiningObjectBasic5() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        // Checks virtual diamond inheritance does not solve accidental ambiguity.
        simple_diamond_root.definingObject("almost_accidental_ambiguate");
    }

    @Test
    public void testNonVirtualHybridDefiningObjectBasic1() throws OOP4ObjectInstantiationFailedException {
        // Checks we can hold separate instances for virtual and non-virtual ancestors.
        YesVirtualTestClasses.NVH_A a_obj = new YesVirtualTestClasses.NVH_A();
        Object c_instance = null;
        Object d_instance = null;
        try {
            c_instance = ((OOPObject) a_obj.definingObject("no_ambiguity_C"))
                    .definingObject("public_to_inherit");
            d_instance = ((OOPObject) a_obj.definingObject("no_ambiguity_D"))
                    .definingObject("public_to_inherit");
        } catch (OOP4NoSuchMethodException | OOP4AmbiguousMethodException e) {
            fail();
        }
        Assert.assertNotSame(c_instance, d_instance);
    }

    /* ################################ invoke ############################### */

    @Test
    public void testSimpleDiamondInvokeBasic1() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException, OOP4MethodInvocationFailedException {
        Assert.assertEquals("This is SD_A's function",
                simple_diamond_root.invoke("root_function"));
    }

    @Test
    public void testSimpleDiamondInvokeBasic2() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException, OOP4MethodInvocationFailedException {
        int original_id = (Integer) simple_diamond_root.invoke("no_ambiguity_C");
        simple_diamond_root.invoke("no_ambiguity_B");
        Assert.assertEquals(original_id + 1, simple_diamond_root.invoke("no_ambiguity_C"));
    }
}

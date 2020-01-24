package OOP.Tests;

import OOP.Provided.OOP4AmbiguousMethodException;
import OOP.Provided.OOP4MethodInvocationFailedException;
import OOP.Provided.OOP4NoSuchMethodException;
import OOP.Provided.OOP4ObjectInstantiationFailedException;
import OOP.Solution.OOPObject;
import OOP.Solution.OOPParent;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProvidedTests {
    /* ************************ class hierarchy to check over ************************ */
    public static class testClass_A extends OOPObject {
        public testClass_A() throws OOP4ObjectInstantiationFailedException {
        }

        public String inherited_method(Integer a) {
            return "The inherited method was invoked with string " + a;
        }
    }

    public static class testClass_B extends OOPObject {
        public testClass_B() throws OOP4ObjectInstantiationFailedException {
        }
    }

    @OOPParent(parent = testClass_A.class)
    @OOPParent(parent = testClass_B.class, isVirtual = true)
    public static class testClass_C extends OOPObject {
        public testClass_C() throws OOP4ObjectInstantiationFailedException {
        }
    }

    private static testClass_C simple_hierarchy_root;

    @BeforeClass
    public static void init_fields() throws OOP4ObjectInstantiationFailedException {
        simple_hierarchy_root = new testClass_C();
    }

    /* ************************ Test methods ************************ */

    @Test
    public void testSimpleHierarchyClassInheritance() {
        Assert.assertTrue(simple_hierarchy_root.multInheritsFrom(testClass_A.class));
    }

    @Test
    public void testSimpleHierarchyDefiningObject() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        Assert.assertEquals(testClass_A.class,
                simple_hierarchy_root.definingObject("inherited_method", Integer.class).getClass());
    }

    @Test
    public void testSimpleHierarchyInvocation() throws OOP4MethodInvocationFailedException, OOP4NoSuchMethodException,
            OOP4AmbiguousMethodException {
        Assert.assertEquals("The inherited method was invoked with string 3",
                simple_hierarchy_root.invoke("inherited_method", 3));
    }
}

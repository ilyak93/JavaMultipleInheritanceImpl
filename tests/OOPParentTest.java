package OOP.Tests;

import OOP.Provided.OOP4ObjectInstantiationFailedException;
import OOP.Solution.OOPParent;
import OOP.Solution.OOPObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

import org.junit.Test;

public class OOPParentTest {

    /*
     Optional additional tests:
        - Manually check OOPParent's @Target.
        - Check isVirtual's default value.
     */

    @Test
    public void checkRetention() {
        // Check if this annotation stays in runtime.
        Class<?> annotation_test_class = AnnotationTestChildClass.class;
        Annotation parent_annotation = annotation_test_class.getAnnotation(OOPParent.class);
        assertNotNull(parent_annotation);
    }

    @Test
    public void checkParentAttr() {
        // Check that the annotation has the correct "parent" field.
        Method parent_attribute = null;
        try {
            parent_attribute = OOPParent.class.getDeclaredMethod("parent");
            parent_attribute.setAccessible(true);
            if (!parent_attribute.getReturnType().equals(OOPParentTest.class.getClass())) {
                fail("OOPParent: attribute \"parent\" has incorrect type");
            }
        } catch (NoSuchMethodException e) {
            fail("OOPParent: no attribute named \"parent\"");
        } finally {
            if (parent_attribute != null) {
                parent_attribute.setAccessible(false);
            }
        }
    }

    @Test
    public void checkIsvirtualAttr() {
        // Check that the annotation has the correct "isVirtual" field.
        Method isVirtual_attribute = null;
        try {
            isVirtual_attribute = OOPParent.class.getDeclaredMethod("isVirtual");
            isVirtual_attribute.setAccessible(true);
            // TODO: Find some way to check if isVirtual_attribute's "return" type is "boolean".
            if ((!isVirtual_attribute.getReturnType().getTypeName().equals(boolean.class.getTypeName()))) {
                fail("OOPParent: attribute \"parent\" has incorrect type");
            }
        } catch (NoSuchMethodException e) {
            fail("OOPParent: no attribute named \"isVirtual\"");
        } finally {
            if (isVirtual_attribute != null) {
                isVirtual_attribute.setAccessible(false);
            }
        }
    }

    static class AnnotationTestParentClass {}

    @OOPParent(parent=AnnotationTestParentClass.class, isVirtual=false)
    private static class AnnotationTestChildClass extends OOPObject {
        public AnnotationTestChildClass() throws OOP4ObjectInstantiationFailedException {

        }
    }
}

import Exceptions.OOP4ObjectInstantiationFailedException;
import Exceptions.OOP4AmbiguousMethodException;
import Exceptions.OOP4MethodInvocationFailedException;
import Exceptions.OOP4NoSuchMethodException;
import Exceptions.OOP4ObjectInstantiationFailedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class OOPObject {

    private LinkedHashSet<Object> directParents;
    private HashMap<String, Object> virtualAncestor;

    public OOPObject() throws OOP4ObjectInstantiationFailedException {

        Class<?> child = this.getClass();
        OOPParent[] parents = child.getAnnotationsByType(OOPParent.class);
        this.directParents = new LinkedHashSet<>();
//        if (parents == null){
//            System.out.println("this.directParents.toString()");
//        }
        if (parents != null) {
            for (OOPParent parent: parents) {
                Constructor<?> parent_ctor = getCompatibleConstructor(parent.parent());
                if (parent_ctor == null) {
                    throw new OOP4ObjectInstantiationFailedException();
                }
                try {
                    this.directParents.add(parent_ctor.newInstance());
                } catch (Exception e) {
                    throw new OOP4ObjectInstantiationFailedException();
                }
            }
        }
        this.virtualAncestor = new HashMap<>();
    }

    public boolean multInheritsFrom(Class<?> cls) {

//        if (this.directParents != null) {
//            System.out.println(directParents.toString());
//        }
        if (cls == OOPObject.class) {       // return false since obviously "this" inherits from it
            return false;                   // but it's not allowed to multiple inherit from it.
        }
        Class<?> my_class = this.getClass();
        // case A == B
        if (cls == my_class) {
            return true;
        } // case of A is regular son of B
        if (cls.isInstance(this)) {
            return true;
        } // case of A is OOP_son of B, or OOP_son of a C which is regular son of B
        if (directParents.stream()
                .anyMatch(parent -> parent.getClass() == cls || parent.getClass().isInstance(cls))) {
            return true;
        } // case of A is OOP_son of C which is OOP_son of B
        return directParents.stream()
                .filter(parent -> parent instanceof OOPObject)
                .anyMatch(parent -> ((OOPObject) parent).multInheritsFrom(cls));
    }

    public Object definingObject(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException, OOP4NoSuchMethodException {
        // TODO: Implement
        return null;
    }

    public Object invoke(String methodName, Object... callArgs) throws
            OOP4AmbiguousMethodException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException {
        // TODO: Implement
        return null;
    }
    // checks that class has non-private zero-argument c'tor and returns it
    private Constructor<?> getCompatibleConstructor(Class<?> cl) {
        for (Constructor<?> ctor : cl.getDeclaredConstructors()) {
            if (ctor.getParameterCount() == 0 && !Modifier.isPrivate(ctor.getModifiers())) {
                return ctor;
            }
        }
        return null;
    }
}

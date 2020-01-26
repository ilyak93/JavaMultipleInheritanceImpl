import Exceptions.OOP4ObjectInstantiationFailedException;
import Exceptions.OOP4AmbiguousMethodException;
import Exceptions.OOP4MethodInvocationFailedException;
import Exceptions.OOP4NoSuchMethodException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class OOPObject {

    private LinkedHashSet<Object> directParents;
    private HashMap<String, Object> virtualAncestor;

    public OOPObject() throws OOP4ObjectInstantiationFailedException {

        Class<?> child = this.getClass();
        OOPParent[] parents = child.getAnnotationsByType(OOPParent.class);
        this.directParents = null;
//        if (parents == null){
//            System.out.println("this.directParents.toString()");
//        }
        if (parents != null) {
            this.directParents = new LinkedHashSet<>();
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
        if (cls == OOPObject.class && this.getClass()!= cls) {       // return false since obviously "this" inherits from it
            return false;                   // but it's not allowed to multiple inherit from it and it's not it itself.
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
                .anyMatch(parent -> parent.getClass() == cls ||  parent.getClass().isInstance(cls))) {
            return true;
        } // case of A is OOP_son of C which is OOP_son of B
        return directParents.stream()
                .filter(parent -> parent instanceof OOPObject)
                .anyMatch(parent -> ((OOPObject) parent).multInheritsFrom(cls));
    }

    public Object definingObject(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException, OOP4NoSuchMethodException {
        Object res = definingObjectRec(methodName, argTypes);
        if(res == null) throw new OOP4NoSuchMethodException();
        return res;
    }

    private Object definingObjectRec(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException {
        Object res = null;
        res = getObjectOfMethod(methodName, argTypes);
        //if this does'nt have such method
        if(res == null) {
            Object tmpRes = null;
            for (Object obj : directParents) {
                if(obj instanceof OOPObject) {
                    if(res == null) {
                        //first found parent-object of method
                        res = ((OOPObject) obj)
                                .getObjectOfMethod(methodName, argTypes);
                    } else {
                        //second found parent-object of method
                        tmpRes = ((OOPObject) obj)
                                .getObjectOfMethod(methodName, argTypes);
                        if(tmpRes != null) break;
                    }
                }
            }
            if(res != null && tmpRes != null) throw new OOP4AmbiguousMethodException();
        }
        return res;
    }

    public Object invoke(String methodName, Object... callArgs) throws
            OOP4AmbiguousMethodException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException {
        // TODO: Implement
        Object res = null;
        List<Class<?>> argsTypesList = new ArrayList<Class<?>>();
        for(Object arg : callArgs){
            argsTypesList.add(arg.getClass());
        }
        Class<?>[] argTypes = (Class<?>[])(argsTypesList.toArray());
        Object definingObject = definingObject(methodName, argTypes);
        try {
            res = definingObject.getClass().getMethod(methodName, argTypes);
        } catch (NoSuchMethodException e){
            assert(false); //should never happen
        }
        return res;
    }
/*
    public Object invoke(String methodName, Object... callArgs) throws
            OOP4AmbiguousMethodException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException {
        // TODO: Implement
        Object res = null;
        res = thisMethodInvoke(methodName, callArgs);
        //if this does'nt have such method
        if(res == null) {
            Object tmpRes = null;
            for (Object obj : directParents){
                if(obj instanceof OOPObject){
                    if(res == null) {
                        res = ((OOPObject) obj).invoke(methodName, callArgs);
                    } else {
                        tmpRes = ((OOPObject) obj).invoke(methodName, callArgs);
                        break;
                    }
                }
            }
            if(res != null && tmpRes != null) throw new OOP4AmbiguousMethodException();
            if(res != null) return res;
        }
        if(res == null) throw new OOP4NoSuchMethodException();
        return res;
    }
*/
    private boolean methodsEquals(Method m1, Method m2){
        return Arrays.equals(m1.getParameterTypes(), m2.getParameterTypes())
                && m1.getName().equals(m2.getName());
    }

    private Object getObjectOfDecalredMethod(String methodName, Class<?> ...argTypes){ //TODO: check the ... (maybe [])
        Class<?> my_class = this.getClass();
        int params_count = argTypes.length;
        Method[] decalred_methods = my_class.getDeclaredMethods();
        for (Method method : decalred_methods) {
            int method_params_count = method.getParameterCount();
            if (method.getName().equals(methodName) && params_count == method_params_count) {
                Class<?>[] methodArgTypes = method.getParameterTypes();
                if(Arrays.equals(argTypes, methodArgTypes)){
                    return this;
                }
            }
        }
        return null;
    }

    //TODO: check the ... (maybe [])
    private Object getObjectOfInheritedMethod(String methodName,
                                              Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException {
        Class<?> my_class = this.getClass();
        Method[] all_methods = my_class.getMethods();
        Method[] decalred_methods = my_class.getDeclaredMethods();
        Object[] inherited_methods = Arrays.stream(all_methods)
                .distinct().filter(method1 -> Arrays.stream(decalred_methods)
                        .allMatch(method2 -> !methodsEquals(method1, method2)))
                .toArray();
        Object foundObjectOfMethod = null;
        int params_count = argTypes.length;
        for (Object method_obj : inherited_methods) {
            Method method = (Method)method_obj;
            int method_params_count = method.getParameterCount();
            if (method.getName().equals(methodName) && params_count == method_params_count) {
                Class<?>[] methodArgTypes = method.getParameterTypes();
                if(Arrays.equals(argTypes, methodArgTypes)){
                    if(foundObjectOfMethod == null ) foundObjectOfMethod = this;
                    else throw new OOP4AmbiguousMethodException();
                }
            }
        }

        return null;
    }

    private Object getObjectOfMethod(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException {
        Object objOfMethod = null;
        objOfMethod = getObjectOfDecalredMethod(methodName, argTypes);
        if(objOfMethod != null) return objOfMethod;
        objOfMethod = getObjectOfInheritedMethod(methodName, argTypes);
        return objOfMethod;
    }


    private List<Method> getMathcingMethods(Class<?> cl, String methodName,  Object... callArgs) {
        List<Method> found_methods = new ArrayList<Method>();
        int params_count = callArgs.length;
        for (Method method : cl.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == params_count) {
                found_methods.add(method);
            }
        }
        return found_methods;
    }

    //returns null if has not appropriate method or the result of invocation otherwise
    private Object thisMethodInvoke(String methodName, Object... callArgs)
            throws OOP4MethodInvocationFailedException, OOP4NoSuchMethodException {
        Class<?> my_class = this.getClass();
        Object result = null;
        List<Method> matching_methods_by_number_of_params =
                getMathcingMethods(my_class, methodName, callArgs);

        for (Method method : matching_methods_by_number_of_params) {
            if (Modifier.isPrivate(method.getModifiers())) { //TODO: deal with private methods: just continue, they irrelevant
                continue;
            }
            try {
                result = method.invoke(this, callArgs);
            } catch (IllegalAccessException e) {
                //e.printStackTrace(); //TODO: check private methods
                assert (false); //TODO: never should happen
                //continue;
            } catch (IllegalArgumentException e) {
                //e.printStackTrace();
                continue;
            } catch (InvocationTargetException e) {
                //e.printStackTrace();
                throw new OOP4MethodInvocationFailedException();
            }
        }
        return result;
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

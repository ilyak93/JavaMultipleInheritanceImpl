package Solution;

import Provided.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class OOPObject {

    private LinkedHashSet<Object> directParents;
    private HashMap<Class, Object> virtualAncestors;
    static int rec_lvl = 0;
    static private HashMap<Class, Object> initializedVirtualAncestors = new HashMap<>();

    public OOPObject() throws OOP4ObjectInstantiationFailedException {
        rec_lvl++;
        Class<?> child = this.getClass();
        OOPParent[] parents = child.getAnnotationsByType(OOPParent.class);
        this.directParents = null;
//        if (parents == null){
//            System.out.println("this.directParents.toString()");
//        }
        if(parents == null) return;
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
        rec_lvl--;
        this.virtualAncestors = new HashMap<>();
        if(rec_lvl == 0){
            virtualInitiation();
        }

    }
    
    private void virtualInitiation(){
        for(Object parent_obj : directParents){
            if(parent_obj instanceof OOPObject){
                ((OOPObject) parent_obj).virtualInitiation();
            }
        }
        Class<?> child = this.getClass();
        OOPParent[] parents = child.getAnnotationsByType(OOPParent.class);
        if(parents.length == 0) return;

        for(Class classKey : initializedVirtualAncestors.keySet()){
            if(this.virtualAncestors.get(classKey) == null) {
                Object virtual_object = initializedVirtualAncestors.get(classKey);
                this.virtualAncestors.put(classKey, virtual_object);
            }
        }

        boolean atLeastOneVirtual = false;
        for (OOPParent parent: parents) {
            if(parent.isVirtual()){
                atLeastOneVirtual = true;
                Class<?> virtual_cls = parent.parent();
                for(Object parentObj : directParents) {
                    Class<?> parentObjClass = parentObj.getClass();
                    if(parentObjClass == virtual_cls){
                        if(this.virtualAncestors.get(parentObjClass) == null) {
                            this.virtualAncestors.put(parentObjClass, parentObj);
                        }
                        if(initializedVirtualAncestors.get(parentObjClass) == null) {
                            initializedVirtualAncestors.put(parentObjClass, parentObj);
                        }
                    }
                }
            }
        }
        //if(atLeastOneVirtual == false){
        //    initializedVirtualAncestors = new HashMap<>();
        //}
    }

    //A inherits from B ?
    public boolean multInheritsFrom(Class<?> cls) {

//        if (this.directParents != null) {
//            System.out.println(directParents.toString());
//        }
        if (cls == OOPObject.class && this.getClass()!= cls) {       // return false since obviously "this" inherits from it
            return false;                   // but it's not allowed to multiple inherit from it and it's not it itself.
        }
        Class<?> my_class = this.getClass();
        // case A == B
        if (my_class == cls) {
            return true;
        } // case of A is regular son of B
        if (my_class.isInstance(cls)) {
            return true;
        } // case of A is OOP_son of B, or OOP_son of a C which is regular son of B
        for(Object parent_obj : directParents){
            if(parent_obj.getClass() == cls ||
                    cls.isAssignableFrom(parent_obj.getClass()))
                return true;
        }
         // case of A is OOP_son of C which is OOP_son of B
        return directParents.stream()
                .filter(parent -> parent instanceof OOPObject)
                .anyMatch(parent -> ((OOPObject) parent).multInheritsFrom(cls));
    }

    public Object definingObject(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException, OOP4NoSuchMethodException {
        Object res = definingObjectVirtual(methodName, argTypes);
        if(res != null) return res;
        res = definingObjectRec(methodName, argTypes);
        if(res == null) throw new OOP4NoSuchMethodException();
        return res;
    }

    private Object definingObjectRec(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException {
        Object res = null;
        res = getObjectOfMethod(methodName, argTypes);
        if(res !=null) return res;
        //res = getObjectOfInheritedMethod(this, methodName, argTypes);
        //assert(res!=null);
        //if this does'nt have such method
        Object tmpRes = null;
        for (Object parent_obj : directParents) {
            //if(obj instanceof OOPObject) {
            if(res == null) {
                //first found parent-object of method
                if(parent_obj instanceof OOPObject) {
                    res = ((OOPObject) parent_obj)
                            .definingObjectRec(methodName, argTypes);
                } else {
                    res = getObjectOfInheritedMethod(parent_obj, methodName, argTypes);
                }
            } else {
                //second found parent-object of method
                if(parent_obj instanceof OOPObject) {
                    tmpRes = ((OOPObject) parent_obj)
                            .definingObjectRec(methodName, argTypes);
                    if (tmpRes != null) break;
                } else {
                    res = getObjectOfInheritedMethod(parent_obj, methodName, argTypes);
                }
            }
            //}
        }
        if(res != null && tmpRes != null && res != tmpRes) throw new OOP4AmbiguousMethodException();
        return res;
    }

    private Object definingObjectVirtual(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException {
        Object res = null;
        for (Object vertex : this.virtualAncestors.values()) {
            OOPObject defObj = (OOPObject)((OOPObject)vertex).definingObjectRec(methodName, argTypes);
            if(defObj != null){
                res = defObj;
                break;
            }
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
        Class<?>[] argTypes =  argsTypesList.toArray(new Class<?>[0]);
        Object definingObject = definingObject(methodName, argTypes);
        try {
            Method method = definingObject.getClass().getMethod(methodName, argTypes);
            res = method.invoke(definingObject, callArgs);
        } catch (NoSuchMethodException e){
            assert(false); //should never happen
        } catch (IllegalAccessException e) { //TODO: handle exceptions
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new OOP4MethodInvocationFailedException();
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
    static private boolean methodsEquals(Method m1, Method m2){
        return Arrays.equals(m1.getParameterTypes(), m2.getParameterTypes())
                && m1.getName().equals(m2.getName());
    }

    private Object getObjectOfDecalredMethod(String methodName, Class<?> ...argTypes){ //TODO: check the ... (maybe [])
        Class<?> my_class = this.getClass();
        int params_count = argTypes.length;
        Method[] decalred_methods = my_class.getDeclaredMethods();
        for (Method method : decalred_methods) {
            if (Modifier.isPrivate(method.getModifiers())) { //TODO: deal with private methods: just continue, they irrelevant
                continue;
            }
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
    static private Object getObjectOfInheritedMethod(Object suspectedToDefine,
                                                     String methodName,
                                                     Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException {
        Class<?> my_class = suspectedToDefine.getClass();
        Method[] all_methods = my_class.getMethods();
        Method[] decalred_methods = my_class.getDeclaredMethods();
        /*
        Object[] inherited_methods = Arrays.stream(all_methods)
                .distinct().filter(method1 -> Arrays.stream(decalred_methods)
                        .allMatch(method2 -> !methodsEquals(method1, method2)))
                .toArray();
         */
        Object foundObjectOfMethod = null;
        int params_count = argTypes.length;
        for (Object method_obj : all_methods) {
            Method method = (Method)method_obj;
            int method_params_count = method.getParameterCount();
            if (method.getName().equals(methodName) && params_count == method_params_count) {
                Class<?>[] methodArgTypes = method.getParameterTypes();
                if(Arrays.equals(argTypes, methodArgTypes)){
                    if(foundObjectOfMethod == null ) foundObjectOfMethod = suspectedToDefine;
                    else throw new OOP4AmbiguousMethodException();
                }
            }
        }

        return foundObjectOfMethod;
    }

    private Object getObjectOfMethod(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException {
        Object objOfMethod = null;
        objOfMethod = getObjectOfDecalredMethod(methodName, argTypes);
        if(objOfMethod != null) return objOfMethod;
        for(Class classKey : this.virtualAncestors.keySet()){
                try {
                    Object virtualObjOfMethod = this.virtualAncestors
                            .get(this.virtualAncestors.get(classKey).getClass());
                    Method[] methods = this.virtualAncestors.get(classKey).getClass().getMethods();
                    this.virtualAncestors.get(classKey).getClass().getMethod(methodName, argTypes);
                    objOfMethod = virtualObjOfMethod;
                    break;
                } catch (NoSuchMethodException e) {
                    //e.printStackTrace();
                    continue;
                }

        }
        //objOfMethod = getObjectOfInheritedMethod(methodName, argTypes);
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

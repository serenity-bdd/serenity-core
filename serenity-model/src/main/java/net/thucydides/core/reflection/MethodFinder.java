package net.thucydides.core.reflection;



import java.lang.reflect.Method;
import java.util.*;

public class MethodFinder {

    private final Class targetClass;

    private MethodFinder(Class targetClass) {
        this.targetClass = targetClass;
    }

    public static MethodFinder inClass(Class targetClass) {
        return new MethodFinder(targetClass);
    }


    public List<Method> getAllMethods() {
        Set<Method> allMethods = new HashSet<>();
        allMethods.addAll(Arrays.asList(targetClass.getDeclaredMethods()));
        allMethods.addAll(Arrays.asList(targetClass.getMethods()));
        addParentMethods(allMethods,targetClass);
        return new ArrayList<>(allMethods);
    }

    private void addParentMethods(Set<Method> allMethods, Class targetClass) {
        if (targetClass.getSuperclass() != null) {
            allMethods.addAll(Arrays.asList(targetClass.getSuperclass().getDeclaredMethods()));
            addParentMethods(allMethods,targetClass.getSuperclass());
        }
    }

    public Method getMethodNamed(String methodName) {
        List<Method> methods = getAllMethods();
        Method methodFound = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                methodFound = method;
            }
        }
        return methodFound;
    }

    public Method getMethodNamed(String methodName, int argumentCount) {
        List<Method> methods = getAllMethods();
        Method methodFound = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                methodFound = method;
                if (method.getParameterTypes().length == argumentCount){
                    methodFound = method;
                    break;
                }
            }
        }
        return methodFound;
    }
}

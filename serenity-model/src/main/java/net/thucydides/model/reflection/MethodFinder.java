package net.thucydides.model.reflection;



import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

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

    public Method getMethodNamed(String methodName, List<Object> arguments) {
        List<Method> methodsFilteredByName = getAllMethods().stream()
                .filter(m -> Objects.equals(methodName, m.getName()))
                .collect(Collectors.toList());
        if (methodsFilteredByName.isEmpty()) {
            return null;
        }
        Class[] argumentTypes = arguments.stream().map(a -> a == null ? null : a.getClass()).toArray(Class[]::new);
        Method foundMethod = methodsFilteredByName.stream()
                .filter(m -> Arrays.equals(m.getParameterTypes(), argumentTypes))
                .findFirst()
                .orElse(null);
        if (foundMethod == null) {
            foundMethod = methodsFilteredByName.stream()
                    .filter(m -> m.getParameterTypes().length == argumentTypes.length)
                    .findFirst()
                    .orElse(methodsFilteredByName.get(0));
        }
        return foundMethod;
    }
}

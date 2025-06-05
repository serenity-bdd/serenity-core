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

    /**
     * Finds a method by name and arguments, prioritizing methods from more specific (sub)classes.
     *
     * @param methodName the name of the method to find
     * @param arguments  the arguments that will be passed to the method
     * @return the most specific matching Method object, or null if no match found
     */
    public Method getMethodNamed(String methodName, List<Object> arguments) {
        Class<?>[] argumentTypes = arguments.stream()
                .map(a -> a == null ? null : a.getClass())
                .toArray(Class[]::new);

        // Step 1: Filter all methods by name
        List<Method> methodsByName = getAllMethods().stream()
                .filter(m -> methodName.equals(m.getName()))
                .collect(Collectors.toList());

        if (methodsByName.isEmpty()) {
            return null;
        }

        // Step 2: Try exact match on parameter types
        Optional<Method> exactMatch = methodsByName.stream()
                .filter(m -> Arrays.equals(m.getParameterTypes(), argumentTypes))
                .max(Comparator.comparingInt(m -> getClassDepth(m.getDeclaringClass())));

        if (exactMatch.isPresent()) {
            return exactMatch.get();
        }

        // Step 3: Fallback - match by parameter count
        Optional<Method> fallbackMatch = methodsByName.stream()
                .filter(m -> m.getParameterTypes().length == argumentTypes.length)
                .max(Comparator.comparingInt(m -> getClassDepth(m.getDeclaringClass())));

        if (fallbackMatch.isPresent()) {
            return fallbackMatch.get();
        }

        // Step 4: Final fallback - return any method by name (e.g. no parameters)
        return methodsByName.get(0);
    }

    /**
     * Calculates inheritance depth of a class.
     *
     * @param clazz the class to analyze
     * @return number of superclasses between this class and Object (depth in hierarchy)
     */
    private int getClassDepth(Class<?> clazz) {
        int depth = 0;

        while (clazz != null) {
            depth++;
            clazz = clazz.getSuperclass();
        }

        return depth;
    }
}

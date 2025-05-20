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
                .toArray(Class<?>[]::new);

        // Collect all candidate methods with matching name and parameter count
        List<Method> candidates = getAllMethods().stream()
                .filter(m -> methodName.equals(m.getName()))
                .filter(m -> m.getParameterTypes().length == argumentTypes.length)
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            return null;
        }

        // Try to find exact parameter type match, preferring methods from subclasses
        Optional<Method> exactMatch = candidates.stream()
                .filter(m -> Arrays.equals(m.getParameterTypes(), argumentTypes))
                .max(Comparator.comparingInt(m -> getClassDepth(m.getDeclaringClass()))); // Select method from the most specific class (deepest in hierarchy)

        if (exactMatch.isPresent()) {
            return exactMatch.get();
        }

        // If no exact parameter type match, return method with matching parameter count from the most specific class
        return candidates.stream()
                .max(Comparator.comparingInt(m -> getClassDepth(m.getDeclaringClass())))
                .orElse(null);
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

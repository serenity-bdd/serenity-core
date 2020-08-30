package net.serenitybdd.core.lifecycle;

import net.serenitybdd.core.exceptions.TestCompromisedException;
import net.thucydides.core.model.TestOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class LifecycleRegister {

    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleRegister.class);

    private static final ThreadLocal<Set<Object>> registeredObjects
            = ThreadLocal.withInitial(HashSet::new);

    public static void register(Object object) {
        Optional<Object> existingObjectOfThisClass = registeredObjects.get().stream()
                .filter(obj -> obj.getClass().equals(object.getClass()))
                .findFirst();
        registeredObjects.get().add(object);
    }

    public static void clear() {
        registeredObjects.get().clear();
    }

    public static void invokeMethodsAnnotatedBy(Class<? extends Annotation> lifecycleAnnotation,
                                               TestOutcome outcome) {
        List<Object> matchingObjects = registeredObjects.get().stream()
                .filter(object -> hasAnnotatedMethod(object, lifecycleAnnotation))
                .collect(Collectors.toList());

        for (Object object : matchingObjects) {
            invokeAnnotatedMethods(object, lifecycleAnnotation, outcome);

        }
    }

    private static boolean hasAnnotatedMethod(Object object, Class<? extends Annotation> lifecycleAnnotation) {
        return stream(object.getClass().getMethods())
                .anyMatch(method -> method.getDeclaredAnnotation(lifecycleAnnotation) != null);
    }

    private static void invokeAnnotatedMethods(Object object,
                                               Class<? extends Annotation> lifecycleAnnotation,
                                               TestOutcome outcome) {

        List<Method> annotatedMethods = stream(object.getClass().getMethods())
                .filter(method -> method.getDeclaredAnnotation(lifecycleAnnotation) != null)
                .collect(Collectors.toList());

        for (Method method : annotatedMethods) {
            invoke(object, method, outcome);
        }
    }

    private static void invoke(Object object, Method method, TestOutcome outcome) {
        try {
            if (method.getParameterTypes().length == 0) {
                method.invoke(object);
            } else if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == TestOutcome.class) {
                method.invoke(object, outcome);
            } else {
                LOGGER.warn("Invalid signature for " + method.getName() + ": "
                            + "Serenity lifecycle-annotated methods should have either no parameters or a single parameter of type TestOutcome");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new TestCompromisedException("Lifecycle method failure in " + method, e);
        }
    }

}

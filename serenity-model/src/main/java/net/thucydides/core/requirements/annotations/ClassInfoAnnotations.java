package net.thucydides.core.requirements.annotations;

import net.thucydides.core.adapters.TestFramework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClassInfoAnnotations {

    private final Class classInfo;

    public ClassInfoAnnotations(Class classInfo) {
        this.classInfo = classInfo;
    }

    public static ClassInfoAnnotations theClassDefinedIn(Class classInfo) {
        return new ClassInfoAnnotations(classInfo);
    }

    public boolean hasAnAnnotation(Class<? extends Annotation>... annotationClasses) {
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            if (classInfo != null && classInfo.getAnnotation(annotationClass) != null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAPackageAnnotation(Class<? extends Annotation>... annotationClasses) {
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            if (classInfo != null && classInfo.getPackage().getAnnotation(annotationClass) != null) {
                return true;
            }
        }
        return false;
    }

    public boolean containsTests() {
        return allMethods().stream().anyMatch(method -> TestFramework.support().isTestMethod(method));
    }

    private Set<Method> allMethods() {
        Set<Method> allMethods = new HashSet<>();
        if (classInfo == null) {
            return new HashSet<>();
        }
        try {
            allMethods.addAll(Arrays.asList(classInfo.getMethods()));
        } catch (java.lang.NoClassDefFoundError ignored) {}

        try {
            allMethods.addAll(Arrays.asList(classInfo.getDeclaredMethods()));
        } catch (java.lang.NoClassDefFoundError ignored) {}

        Class<?>[] innerClasses = classInfo.getClasses();
        for(Class innerClass : innerClasses) {
            allMethods.addAll(Arrays.asList(innerClass.getMethods()));
        }

        Class<?>[] declaredInnerClasses = classInfo.getClasses();
        for(Class innerClass : declaredInnerClasses) {
            allMethods.addAll(Arrays.asList(innerClass.getDeclaredMethods()));
        }

        return allMethods;
    }
}

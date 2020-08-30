package net.thucydides.core.requirements.annotations;

import com.google.common.reflect.ClassPath;
import net.thucydides.core.util.JUnitAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClassInfoAnnotations {

    private final ClassPath.ClassInfo classInfo;

    public ClassInfoAnnotations(ClassPath.ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    public static ClassInfoAnnotations theClassDefinedIn(ClassPath.ClassInfo classInfo) {
        return new ClassInfoAnnotations(classInfo);
    }

    public boolean hasAnAnnotation(Class<? extends Annotation>... annotationClasses) {
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            if (classInfo.load().getAnnotation(annotationClass) != null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAPackageAnnotation(Class<? extends Annotation>... annotationClasses) {
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            if (classInfo.load().getPackage().getAnnotation(annotationClass) != null) {
                return true;
            }
        }
        return false;
    }

    public boolean containsTests() {
        return allMethods().stream().anyMatch(JUnitAdapter::isTestMethod);
    }

    private Set<Method> allMethods() {
        Set<Method> allMethods = new HashSet<>();
        try {
            allMethods.addAll(Arrays.asList(classInfo.load().getMethods()));
        } catch (java.lang.NoClassDefFoundError ignored) {}

        try {
            allMethods.addAll(Arrays.asList(classInfo.load().getDeclaredMethods()));
        } catch (java.lang.NoClassDefFoundError ignored) {}

        return allMethods;
    }
}
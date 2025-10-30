package net.serenitybdd.junit.runners;

import net.thucydides.junit.annotations.Qualifier;
import net.thucydides.junit.internals.MethodInvoker;
import net.thucydides.model.reflection.MethodFinder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * When running data-driven tests, each set of test data needs a way to distinguish it from the others.
 * This class provides means to distinguish instantiated test cases. By default, the toString() method is used.
 * If a public method that returns a String is marked with the Qualifier annotation, this method will be used instead.
 */
public class QualifierFinder {

    private final Object testCase;

    public QualifierFinder(final Object testCase) {
        this.testCase = testCase;
    }

    public static QualifierFinder forTestCase(final Object testCase) {
        return new QualifierFinder(testCase);
    }

    public String getQualifier() {
        if (hasQualifierAnnotation()) {
            String qualifierValue = (String) MethodInvoker.on(testCase).run(getQualifiedMethod());
            return (qualifierValue != null) ? qualifierValue : "<UNSPECIFIED>";
        } else {
            return "";
        }
    }

    private Method getQualifiedMethod() {
        List<Method> methods = MethodFinder.inClass(testCase.getClass()).getAllMethods();
        for (Method each : methods) {
            if (each.getAnnotation(Qualifier.class) != null) {
                checkModifiersFor(each);
                return each;
            }
        }
        return null;
    }

    private void checkModifiersFor(final Method each) {
        int modifiers = each.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            throw new IllegalArgumentException("Qualifier method must not be static");
        }
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalArgumentException("Qualifier method must be public");
        }
        if (each.getReturnType() != String.class) {
            throw new IllegalArgumentException("Qualifier method must return a String");
        }
    }

    private boolean hasQualifierAnnotation() {
        return (getQualifiedMethod() != null);
    }
}

package net.thucydides.core.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


public class StackTraceAnalyser {

    private final StackTraceElement stackTraceElement;
    private final Logger logger = LoggerFactory.getLogger(StackTraceAnalyser.class);

    private StackTraceAnalyser(StackTraceElement stackTraceElement) {
        this.stackTraceElement = stackTraceElement;
    }

    public static StackTraceAnalyser forStackTraceElement(StackTraceElement stackTraceElement) {
        return new StackTraceAnalyser(stackTraceElement);
    }

    public Method getMethod() {
        try {
            if (allowedClassName(stackTraceElement.getClassName()) && !lambda(stackTraceElement.getClassName()) ) {
                Class callingClass = Class.forName(stackTraceElement.getClassName());
                Method matchingMethod = extractMethod(stackTraceElement, callingClass);
                if (matchingMethod != null) {
                    return matchingMethod;
                }
            }
        } catch (ClassNotFoundException classNotFoundIgnored) {
            logger.warn("Failed to load class during Stack analysis: " + classNotFoundIgnored.getLocalizedMessage());
        } catch (NoClassDefFoundError noClassDefFoundErrorIgnored) {
            logger.warn("Failed to load class definition during Stack analysis: " + noClassDefFoundErrorIgnored.getLocalizedMessage());
        }
        return null;
    }

    private boolean lambda(String className) {
        return className.contains("$$Lambda$");
    }

    public static Method extractMethod(StackTraceElement stackTraceElement, Class callingClass)  {
        Class targetClass;
        if (isInstrumentedMethod(stackTraceElement)) {
            targetClass = callingClass.getSuperclass();
        } else {
            targetClass = callingClass;
        }
        try {
            return targetClass.getMethod(stackTraceElement.getMethodName());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static boolean isInstrumentedMethod(StackTraceElement stackTraceElement) {
        return (stackTraceElement.getFileName()) != null && (stackTraceElement.getFileName().equals("<generated>"));
    }

    private boolean allowedClassName(String className) {
        return !((className.startsWith("sun.")) || (className.startsWith("java.")) || (className.contains("$")));
    }
}

package net.thucydides.junit.internals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A centralized way to invoke a method on a parameter.
 * Raises an IllegalArgumentException if something goes wrong.
 */
public class MethodInvoker {

    private final Object target;

    protected MethodInvoker(final Object target) {
        this.target = target;
    }

    public static MethodInvoker on(final Object target) {
        return new MethodInvoker(target);
    }

    public Object run(final Method method, final Object... parameters) {
        try {
            return invokeMethod(method, parameters);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not access method",e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Could not invoke method",e);
        }

    }

    protected Object invokeMethod(final Method method, final Object[] parameters)
            throws IllegalAccessException, InvocationTargetException {
        return method.invoke(target, parameters);
    }
}

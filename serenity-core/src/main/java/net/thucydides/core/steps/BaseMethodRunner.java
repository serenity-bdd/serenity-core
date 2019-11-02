package net.thucydides.core.steps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class BaseMethodRunner {
    /**
     * Invokes a method on a given object using the given parameters
     * @param obj
     * @param args
     * @param method
     * @return - the result of invoking the method with the given parameters.
     * @throws Throwable
     */
    public Object invokeMethod(final Object obj, final Object[] args, final Method method) throws Throwable {
        try {
            return method.invoke(obj, args);
        } catch(InvocationTargetException invocationTargetException) {
            throw invocationTargetException.getCause();
        }
    }

}

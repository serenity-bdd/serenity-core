package net.thucydides.core.steps;

import java.lang.reflect.Method;

/**
 * Created by john on 9/08/2015.
 */
public interface MethodErrorReporter {
    void reportMethodError(Throwable generalException, Object obj, Method method, Object[] args) throws Throwable;
}

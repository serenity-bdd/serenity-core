package net.thucydides.core.steps.interception;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public interface StepInterceptionListener {
    void start(final Object obj, final Method method,
                      final Object[] args, final MethodProxy proxy);
    void end(final Object obj, final Method method,
                    final Object[] args, final MethodProxy proxy);
}

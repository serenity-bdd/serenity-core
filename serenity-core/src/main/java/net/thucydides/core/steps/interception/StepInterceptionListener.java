package net.thucydides.core.steps.interception;


import java.lang.reflect.Method;

public interface StepInterceptionListener {
    void start(final Object obj, final Method method,
                      final Object[] args, final Method zuperMethod);
    void end(final Object obj, final Method method,
                    final Object[] args, final Method zuperMethod);
}

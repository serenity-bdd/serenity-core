package net.thucydides.core.steps;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Invoke a step multiple times, each time initialized with a different set of test data.
 */
public class DataDrivenStepInterceptor implements Interceptor {

    private List<?> instantiatedSteps;

    public DataDrivenStepInterceptor(List<?> instantiatedSteps) {
        this.instantiatedSteps = instantiatedSteps;
    }

    @RuntimeType
    public  Object intercept(
            @Origin Method method,
            @This Object self,
            @AllArguments Object[] args,
            @SuperMethod Method zuperMethod
    ) throws Throwable {

        DataDrivenStep.startDataDrivenStep();
        Object lastResult = null;
        for (Object steps : instantiatedSteps) {
            lastResult = runMethodAndIgnoreExceptions(steps, zuperMethod, method, args);
            StepEventBus.getEventBus().clearStepFailures();
        }
        DataDrivenStep.endDataDrivenStep();
        return lastResult;
    }

    private Object runMethodAndIgnoreExceptions(Object steps,  Method zuperMethod, Method method, Object[] args) throws Throwable {
        if (isFinalizer(method)) {
            return this;
        }
        method.setAccessible(true);
        return method.invoke(steps, args);
    }

    private boolean isFinalizer(Method method) {
        return method.getName().equals("finalize");
    }
}

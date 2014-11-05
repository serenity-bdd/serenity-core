package net.thucydides.core.steps;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Invoke a step multiple times, each time initialized with a different set of test data.
 */
public class DataDrivenStepInterceptor implements MethodInterceptor {

    private List<?> instantiatedSteps;

    public DataDrivenStepInterceptor(List<?> instantiatedSteps) {
        this.instantiatedSteps = instantiatedSteps;
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        DataDrivenStep.startDataDrivenStep();
        Object lastResult = null;
        for (Object steps : instantiatedSteps) {
            lastResult = runMethodAndIgnoreExceptions(steps, proxy, method, args);
//            StepEventBus.getEventBus().exampleFinished();
            StepEventBus.getEventBus().clearStepFailures();
        }
        DataDrivenStep.endDataDrivenStep();
        return lastResult;
    }

    private Object runMethodAndIgnoreExceptions(Object steps,  MethodProxy proxy, Method method, Object[] args) throws Throwable {
        if (isFinalizer(method)) {
            return this;
        }
        return proxy.invoke(steps, args);
    }

    private boolean isFinalizer(Method method) {
        return method.getName().equals("finalize");
    }
}

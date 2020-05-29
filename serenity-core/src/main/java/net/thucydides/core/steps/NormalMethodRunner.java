
package net.thucydides.core.steps;

import java.lang.reflect.Method;


class NormalMethodRunner extends BaseMethodRunner implements MethodRunner {

    private final MethodErrorReporter errorReporter;

    NormalMethodRunner(MethodErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    @Override
    public Object invokeMethodAndNotifyFailures(Object obj, Method method, Object[] args, Method zuperMethod, Object result) throws Throwable {
        try {
            result = invokeMethod(obj, args, zuperMethod);
        } catch (Throwable generalException) {
            errorReporter.reportMethodError(generalException, obj, method, args);
        }
        return result;

    }
}


package net.thucydides.core.steps;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

class NormalMethodRunner extends BaseMethodRunner implements MethodRunner {

        private final MethodErrorReporter errorReporter;

        NormalMethodRunner(MethodErrorReporter errorReporter) {
            this.errorReporter = errorReporter;
        }


        @Override
        public Object invokeMethodAndNotifyFailures(Object obj, Method method, Object[] args, MethodProxy proxy, Object result) throws Throwable {
            try {
                result = invokeMethod(obj, args, proxy);
            } catch (Throwable generalException) {
                errorReporter.reportMethodError(generalException, obj, method, args);
            }
            return result;
        }
    }

package net.thucydides.core.steps;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.serenitybdd.core.collect.NewList;

import java.lang.reflect.Method;
import java.util.List;

class DryRunMethodRunner extends BaseMethodRunner implements MethodRunner {

        private final List<String> slowDomains = NewList.of("webdriver", "rest");

        @RuntimeType
        public Object invokeMethodAndNotifyFailures(Object obj, Method method, Object[] args, Method zuperMethod, Object result) throws Throwable {
            try {
                if (!isSlow(method)) {
                    result = invokeMethod(obj, args, zuperMethod);
                }
            } catch (Throwable ignorableException) {
                return DefaultValue.defaultReturnValueFor(method, obj);
            }
            return result;
        }

        private boolean isSlow(Method method) {
            for(String slowDomain : slowDomains) {
                if (method.getDeclaringClass().getPackage().toString().contains(slowDomain)) {
                    return true;
                }
            }
            return false;
        }
    }
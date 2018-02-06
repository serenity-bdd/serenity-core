package net.thucydides.core.steps;

import net.serenitybdd.core.collect.NewList;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

class DryRunMethodRunner extends BaseMethodRunner implements MethodRunner {

        private final List<String> slowDomains = NewList.of("webdriver", "rest");
        @Override
        public Object invokeMethodAndNotifyFailures(Object obj, Method method, Object[] args, MethodProxy proxy, Object result) throws Throwable {
            try {
                if (!isSlow(method)) {
                    result = invokeMethod(obj, args, proxy);
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
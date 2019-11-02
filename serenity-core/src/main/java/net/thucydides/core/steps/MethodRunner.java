package net.thucydides.core.steps;

import java.lang.reflect.Method;

interface MethodRunner {
    Object invokeMethodAndNotifyFailures(Object obj, Method method, Object[] args, Method zuperMethod, Object result) throws Throwable;
}

package net.thucydides.core.steps;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

public interface Interceptor {

    @RuntimeType
    public  Object intercept(
            @Origin Method method,
            @This Object self,
            @AllArguments Object[] args,
            @SuperMethod Method zuperMethod
    ) throws Throwable;
}

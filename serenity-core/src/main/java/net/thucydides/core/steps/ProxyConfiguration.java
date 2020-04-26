package net.thucydides.core.steps;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

public interface ProxyConfiguration {
    /**
     * The canonical field name for an interceptor object stored in a proxied object.
     */
    String INTERCEPTOR_FIELD_NAME = "$$_serenity_interceptor";

    /**
     * Defines an interceptor object that specifies the behavior of the proxy object.
     *
     * @param interceptor The interceptor object.
     */
    void $$_serenity_set_interceptor(Interceptor interceptor);

    /**
     * A static interceptor that guards against method calls before the interceptor is set.
     */
    class InterceptorDispatcher {

        /**
         * Intercepts a method call to a proxy.
         *
         * @param instance The proxied instance.
         * @param method The invoked method.
         * @param arguments The method arguments.
         * @param stubValue The intercepted method's default value.
         * @param interceptor The proxy object's interceptor instance.
         *
         * @return The intercepted method's return value.
         *
         * @throws Throwable If the intercepted method raises an exception.
         */
        @RuntimeType
        public static Object intercept(
                @This final Object instance,
                @Origin final Method method,
                @AllArguments final Object[] arguments,
                @StubValue final Object stubValue,
                @FieldValue(INTERCEPTOR_FIELD_NAME) Interceptor interceptor,
                @SuperMethod Method zuperMethod
        ) throws Throwable {
            if ( interceptor == null ) {
                return stubValue;
            }
            else {
                return interceptor.intercept( method, instance, arguments, zuperMethod);
            }
        }
    }
}

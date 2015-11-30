package net.serenitybdd.rest.decorators;

import com.jayway.restassured.specification.RequestSpecification;

/**
 * User: YamStranger
 * Date: 11/30/15
 * Time: 12:25 AM
 */
public abstract class BaseWrapper<T> {
    protected final T core;
    protected final ThreadLocal<RequestSpecification> specification;
    protected final RestDecorator decorator;

    public BaseWrapper(final T spec,
                       final ThreadLocal<RequestSpecification> instrumented,
                       final RestDecorator decorator) {
        this.core = spec;
        this.specification = instrumented;
        this.decorator = decorator;
    }
}
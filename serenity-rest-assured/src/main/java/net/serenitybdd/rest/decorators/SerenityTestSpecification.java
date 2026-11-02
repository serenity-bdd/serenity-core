package net.serenitybdd.rest.decorators;

import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import net.serenitybdd.rest.decorators.request.RequestSpecificationDecorated;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * Pairs a decorated request specification with a decorated response specification and sends requests through them.
 * <p>
 * This replaces RestAssured's TestSpecificationImpl, which (since 6.2.0) casts its arguments to the
 * concrete RequestSpecificationImpl/ResponseSpecificationImpl classes and so cannot accept Serenity's decorators.
 */
public interface SerenityTestSpecification extends RequestSender {

    RequestSpecification getRequestSpecification();

    ResponseSpecification getResponseSpecification();

    static SerenityTestSpecification of(RequestSpecificationDecorated request, ResponseSpecificationDecorated response) {
        request.linkResponseSpecification(response);
        response.linkRequestSpecification(request);
        return (SerenityTestSpecification) Proxy.newProxyInstance(
                SerenityTestSpecification.class.getClassLoader(),
                new Class<?>[]{SerenityTestSpecification.class},
                (proxy, method, args) -> {
                    if (method.getDeclaringClass() == SerenityTestSpecification.class) {
                        return method.getName().equals("getRequestSpecification") ? request : response;
                    }
                    if (method.getDeclaringClass() == Object.class) {
                        return method.invoke(request, args);
                    }
                    try {
                        return method.invoke(request, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                });
    }
}

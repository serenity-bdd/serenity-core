package net.serenitybdd.rest.utils;

import io.restassured.filter.Filter;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.internal.ResponseSpecificationImpl;
import io.restassured.internal.filter.SendRequestFilter;
import io.restassured.specification.*;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.rest.decorators.ResponseSpecificationDecorated;
import net.serenitybdd.rest.decorators.request.RequestSpecificationDecorated;
import net.serenitybdd.rest.filters.FieldsRecordingFilter;
import net.serenitybdd.rest.filters.UpdatingContextFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static io.restassured.filter.log.LogDetail.*;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;


public class RestSpecificationFactory {

    private static final Logger log = LoggerFactory.getLogger(RestSpecificationFactory.class);

    private static Constructor<?> requestSpecificationDecoratedConstructor;

    private static Constructor<?> responseSpecificationDecoratedConstructor;

    private static RequestSpecificationDecoratedFactory requestSpecificationDecoratedFactory;

    private static ResponseSpecificationDecoratedFactory responseSpecificationDecoratedFactory;

    static {
        final Class<?>  requestSpecificationDecoratedClass = new ByteBuddy()
                .subclass(RequestSpecificationDecorated.class)
                .method(isDeclaredBy(RequestSpecification.class).or(isDeclaredBy(RequestSenderOptions.class)).or(isDeclaredBy(FilterableRequestSpecification.class)))
                .intercept(MethodDelegation.toField("core"))
                .make()
                .load(SerenityRest.class.getClassLoader())
                .getLoaded();
        final Class<?> responseSpecificationDecoratedClass = new ByteBuddy()
                .subclass(ResponseSpecificationDecorated.class)
                .method(isDeclaredBy(ResponseSpecification.class).or(isDeclaredBy(RequestSenderOptions.class)).or(isDeclaredBy(FilterableResponseSpecification.class)))
                .intercept(MethodDelegation.toField("core"))
                .make()
                .load(SerenityRest.class.getClassLoader())
                .getLoaded();
        try {
            requestSpecificationDecoratedConstructor = requestSpecificationDecoratedClass.getConstructor(RequestSpecificationImpl.class);
        } catch (NoSuchMethodException e) {
            log.error("Cannot found constructor for RequestSpecificationDecorated ",e);
        }

        try {
            responseSpecificationDecoratedConstructor = responseSpecificationDecoratedClass.getConstructor(ResponseSpecificationImpl.class);
        } catch (NoSuchMethodException e) {
            log.error("Cannot found constructor for ResponseSpecificationDecorated ",e);
        }

        try {
            requestSpecificationDecoratedFactory = new ByteBuddy()
                    .subclass(RequestSpecificationDecoratedFactory.class)
                    .method(isDeclaredBy(RequestSpecificationDecoratedFactory.class))
                    .intercept(MethodCall
                            .construct(requestSpecificationDecoratedConstructor)
                            .withArgument(0))
                    .make()
                    .load(requestSpecificationDecoratedClass.getClassLoader())
                    .getLoaded().newInstance();
        } catch (InstantiationException | IllegalAccessException  e) {
            log.error("Cannot create requestSpecificationDecoratedFactory ",e);
        }

        try {
            responseSpecificationDecoratedFactory = new ByteBuddy()
                    .subclass(ResponseSpecificationDecoratedFactory.class)
                    .method(isDeclaredBy(ResponseSpecificationDecoratedFactory.class))
                    .intercept(MethodCall
                            .construct(responseSpecificationDecoratedConstructor)
                            .withArgument(0))
                    .make()
                    .load(responseSpecificationDecoratedClass.getClassLoader())
                    .getLoaded().newInstance();
        } catch (InstantiationException | IllegalAccessException  e) {
            log.error("Cannot create responseSpecificationDecoratedFactory ",e);
        }

    }

    public static RequestSpecificationDecorated getInstrumentedRequestSpecification(RequestSpecificationImpl delegate) {
        RequestSpecificationDecorated instrumentedResponse  = (RequestSpecificationDecorated) requestSpecificationDecoratedFactory.create((delegate));
        final List<Filter> filters = new LinkedList<>();
        for (final LogDetail logDetail : Arrays.asList(HEADERS, COOKIES, BODY, PARAMS, METHOD, URI)) {
            filters.add(new FieldsRecordingFilter(true, logDetail));
        }
        if (RestExecutionHelper.restCallsAreDisabled()) {
            filters.add(new UpdatingContextFilter(SendRequestFilter.class));
        }
        instrumentedResponse.filters(filters);

        return instrumentedResponse;
    }

    public static ResponseSpecificationDecorated getInstrumentedResponseSpecification(ResponseSpecificationImpl delegate) {
        return  (ResponseSpecificationDecorated) responseSpecificationDecoratedFactory.create(delegate);
    }
}

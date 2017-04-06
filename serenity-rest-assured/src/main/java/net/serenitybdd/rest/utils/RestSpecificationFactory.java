package net.serenitybdd.rest.utils;

import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.internal.ResponseSpecificationImpl;
import com.jayway.restassured.internal.filter.SendRequestFilter;
import com.jayway.restassured.specification.*;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.rest.decorators.ResponseSpecificationDecorated;
import net.serenitybdd.rest.decorators.request.RequestSpecificationDecorated;
import net.serenitybdd.rest.filters.FieldsRecordingFilter;
import net.serenitybdd.rest.filters.UpdatingContextFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.jayway.restassured.filter.log.LogDetail.*;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;


public class RestSpecificationFactory {

    private static final Logger log = LoggerFactory.getLogger(RestSpecificationFactory.class);

    private static Constructor<?> requestSpecificationDecoratedConstructor;

    private static Constructor<?> responseSpecificationDecoratedConstructor;

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
    }

    public static RequestSpecificationDecorated getInstrumentedRequestSpecification(RequestSpecificationImpl delegate) {
        RequestSpecificationDecorated instrumentedResponse = null;
        try {
            instrumentedResponse = (RequestSpecificationDecorated) requestSpecificationDecoratedConstructor.newInstance((delegate));
            final List<Filter> filters = new LinkedList<>();
            for (final LogDetail logDetail : Arrays.asList(HEADERS, COOKIES, BODY, PARAMS, METHOD, PATH)) {
                filters.add(new FieldsRecordingFilter(true, logDetail));
            }
            if (RestExecutionHelper.restCallsAreEnabled()) {
                filters.add(new UpdatingContextFilter(SendRequestFilter.class));
            }
            instrumentedResponse.filters(filters);
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            log.error("Cannot instrument RequestSpecificationImpl ", ex);
        }
        return instrumentedResponse;
    }

    public static ResponseSpecificationDecorated getInstrumentedResponseSpecification(ResponseSpecificationImpl delegate) {
        ResponseSpecificationDecorated instrumentedResponseSpec = null;
        try {
            instrumentedResponseSpec = (ResponseSpecificationDecorated) responseSpecificationDecoratedConstructor.newInstance(delegate);
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            log.error("Cannot instrument ResponseSpecificationDecorated ", ex);
        }
        return instrumentedResponseSpec;
    }
}

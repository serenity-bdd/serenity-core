package net.serenitybdd.rest.utils;

import io.restassured.filter.Filter;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.internal.ResponseSpecificationImpl;
import io.restassured.internal.filter.SendRequestFilter;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSenderOptions;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
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

import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.COOKIES;
import static io.restassured.filter.log.LogDetail.HEADERS;
import static io.restassured.filter.log.LogDetail.METHOD;
import static io.restassured.filter.log.LogDetail.PARAMS;
import static io.restassured.filter.log.LogDetail.URI;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.named;


public class RestSpecificationFactory {

    private static final Logger log = LoggerFactory.getLogger(RestSpecificationFactory.class);

    private static RequestSpecificationDecoratedFactory requestSpecificationDecoratedFactory;

    private static ResponseSpecificationDecoratedFactory responseSpecificationDecoratedFactory;

    static {

        ByteBuddy byteBuddy = new ByteBuddy();

        Class<?>  requestSpecificationDecoratedClass = byteBuddy
                .subclass(RequestSpecificationDecorated.class)
                .method(isDeclaredBy(RequestSpecification.class).or(isDeclaredBy(RequestSenderOptions.class)).or(isDeclaredBy(FilterableRequestSpecification.class)))
                .intercept(MethodDelegation.toField("core"))
                .make()
                .load(SerenityRest.class.getClassLoader(),ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
        Class<?> responseSpecificationDecoratedClass = byteBuddy
                .subclass(ResponseSpecificationDecorated.class)
                .method(named("then").and(isDeclaredBy(ResponseSpecification.class).or(isDeclaredBy(RequestSenderOptions.class))
                        .or(isDeclaredBy(FilterableResponseSpecification.class))))
                .intercept(MethodDelegation.toField("core"))
                .make()
                .load(SerenityRest.class.getClassLoader(),ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        try {
            Constructor<?> requestSpecificationDecoratedConstructor = requestSpecificationDecoratedClass.getConstructor(RequestSpecificationImpl.class);
            Constructor<?> responseSpecificationDecoratedConstructor = responseSpecificationDecoratedClass.getConstructor(ResponseSpecificationImpl.class);

            requestSpecificationDecoratedFactory = byteBuddy
                    .subclass(RequestSpecificationDecoratedFactory.class)
                    .method(isDeclaredBy(RequestSpecificationDecoratedFactory.class))
                    .intercept(MethodCall
                            .construct(requestSpecificationDecoratedConstructor)
                            .withArgument(0))
                    .make()
                    .load(requestSpecificationDecoratedClass.getClassLoader(),ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded().newInstance();

            responseSpecificationDecoratedFactory = byteBuddy
                    .subclass(ResponseSpecificationDecoratedFactory.class)
                    .method(isDeclaredBy(ResponseSpecificationDecoratedFactory.class))
                    .intercept(MethodCall
                            .construct(responseSpecificationDecoratedConstructor)
                            .withArgument(0))
                    .make()
                    .load(responseSpecificationDecoratedClass.getClassLoader(),ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded().newInstance();

        } catch (InstantiationException | IllegalAccessException  e) {
            log.error("Cannot create responseSpecificationDecoratedFactory ",e);
        } catch (NoSuchMethodException e) {
            log.error("Constructor not found",e);
        }

    }

    public static RequestSpecificationDecorated getInstrumentedRequestSpecification(RequestSpecificationImpl delegate) {
        RequestSpecificationDecorated instrumentedResponse  = requestSpecificationDecoratedFactory.create((delegate));
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
        ResponseSpecificationDecorated responseSpecificationDecorated = responseSpecificationDecoratedFactory.create(delegate);
        return responseSpecificationDecorated;
    }
}

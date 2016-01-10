package net.serenitybdd.rest;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.RestAssuredResponseImpl;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.AuthenticationSpecification;
import com.jayway.restassured.specification.RequestLogSpecification;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.serenitybdd.core.rest.RestMethod;
import net.serenitybdd.core.rest.RestQuery;
import net.serenitybdd.rest.decorators.RestDecorator;
import net.serenitybdd.rest.stubs.RequestSpecificationStub;
import net.serenitybdd.rest.stubs.ResponseSpecificationStub;
import net.serenitybdd.rest.stubs.ResponseStub;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFailure;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.core.rest.RestMethod.restMethodCalled;
import static net.thucydides.core.steps.ErrorConvertor.forError;
import static net.thucydides.core.steps.StepEventBus.getEventBus;

public class SerenityRest {

    private static ThreadLocal<RestQuery> currentRestQuery = new ThreadLocal<>();
    private static ThreadLocal<RequestSpecification> currentRequestSpecification = new ThreadLocal<>();
    private static ThreadLocal<Response> currentResponse = new ThreadLocal<>();
    private static ThreadLocal<QueryPayload> currentQueryPayload = new ThreadLocal<>();
    private static ThreadLocal<RestDecorator> decorator = new ThreadLocal<>();

    public static void clearQueryData() {
        currentRequestSpecification.remove();
        currentRestQuery.remove();
    }

    public static QueryPayload currentQueryPayload() {
        if (currentQueryPayload.get() == null) {
            currentQueryPayload.set(new QueryPayload());
        }
        return currentQueryPayload.get();
    }

    public static RequestSpecification rest() {
        currentRequestSpecification.set(instrumentedRequestSpecification());
        getEventBus().registerListener(new RestStepListener());
        return currentRequestSpecification.get();
    }

    public static ValidatableResponse and() {
        return then();
    }

    public static ValidatableResponse then() {
        assert(currentResponse.get() != null);
        return currentResponse.get().then();
    }

    private static RequestSpecification instrumentedRequestSpecification() {
        RequestSpecification specification = RestAssured.given();
        return instrumentedRequestSpecificationFor(specification);
    }

    private static RequestSpecification instrumentedRequestSpecificationFor(final RequestSpecification requestSpecification) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(RequestSpecification.class);
        enhancer.setCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (restMethodCalled(method.getName()).isPresent()) {
                    notifyRestMethodCall(args, restMethodCalled(method.getName()).get());
                } else if (definesContent(method.getName())) {
                    recordContent(method.getName(), args);
                }
                return executeRestQuery(method, args, requestSpecification);
            }
        });

        return (RequestSpecification) enhancer.create();
    }

    private static boolean restCallsAreDisabled() {
        return (getEventBus().isDryRun() || getEventBus().currentTestIsSuspended());
    }

    private static Object executeRestQuery(Method method, Object[] args, RequestSpecification requestSpecification) throws Throwable{

        if (method.getReturnType().isAssignableFrom(RestAssuredResponseImpl.class)) {
            Response response = (Response) wrappedResult(method, requestSpecification, args);
            currentResponse.set(response);
            return response;
        } else {
            return wrappedResult(method, requestSpecification, args);
        }
    }

    private final static List<String> CONTENT_METHODS = ImmutableList.of("content","body");

    private static boolean definesContent(String name) {
        return (CONTENT_METHODS.contains(name.toLowerCase()) || name.equalsIgnoreCase("contentType"));
    }

    private static void recordContent(String name, Object[] args) {
        switch (name) {
            case "content" :
            case "body" :
                registerContent(args[0].toString());
                break;
            case "contentType":
                registerContentType(args[0].toString());
                break;
        }
    }

    private static void registerContentType(String contentType) {
        currentQueryPayload().setContentType(contentType);
    }

    private static void registerContent(String content) {
        currentQueryPayload().setContent(content);
    }


    private static void notifyRestMethodCall(Object[] args, RestMethod methodType) {
        switch (methodType) {
            case GET:
            case DELETE:
                notifyGetOrDelete(args,methodType);
                break;
            case POST:
            case PUT:
            case PATCH:
                notifyPostOrPut(args, methodType);
                break;
        }
    }

    private static Object wrappedResult(Method method, Object target, Object[] args) throws Throwable {
        try {
            if (restCallsAreDisabled()) {
                return stubbed(method);
            }
            Object result = invokeMethod(method, target, args);
            if (result == null) {
                return null;
            }
            if ((RequestSpecification.class.isAssignableFrom(result.getClass()))) {
                currentRequestSpecification.set(instrumentedRequestSpecificationFor((RequestSpecification) result));
                return currentRequestSpecification.get();
            } else if (Response.class.isAssignableFrom(result.getClass())) {
                notifyResponse((Response) result);
                return result;
            }else if (AuthenticationSpecification.class.isAssignableFrom(result.getClass())){
                return getDecorator().decorate((AuthenticationSpecification)result);
            } else if (RequestLogSpecification.class.isAssignableFrom(result.getClass())){
                return getDecorator().decorate((RequestLogSpecification) result);
            }  else {
                return result;
            }
        } catch (Exception generalException) {
            Throwable error = SerenityManagedException.detachedCopyOf(generalException.getCause());
            Throwable assertionError = forError(error).convertToAssertion();
            notifyOfStepFailure(method, args, assertionError);
            return stubbed(method);
        }
    }

    private static RestDecorator getDecorator(){
        if (SerenityRest.decorator.get() == null) {
            SerenityRest.decorator.set(new RestDecorator(SerenityRest.currentRequestSpecification));
        }
        return SerenityRest.decorator.get();
    }

    private static Object stubbed(Method method) {
        if (method.getReturnType().isAssignableFrom(RequestSpecification.class)) {
            return new RequestSpecificationStub();
        }
        if (method.getReturnType().isAssignableFrom(Response.class)) {
            return new ResponseStub();
        }
        if (method.getReturnType().isAssignableFrom(ResponseSpecification.class)) {
            return new ResponseSpecificationStub();
        }
        return Mockito.mock(method.getReturnType());
    }

    private static Object invokeMethod(Method method, Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    private static void notifyOfStepFailure(final Method method, final Object[] args, final Throwable cause) throws Throwable {
        ExecutedStepDescription description = ExecutedStepDescription.withTitle(restMethodName(method, args));
        StepFailure failure = new StepFailure(description, cause);
        StepEventBus.getEventBus().stepStarted(description);
        StepEventBus.getEventBus().stepFailed(failure);
        if (Serenity.shouldThrowErrorsImmediately()) {
            throw cause;
        }
    }

    private static String restMethodName(Method method, Object[] args) {
        String restMethod = method.getName().toUpperCase() + " " + args[0].toString();
        return (args.length < 2) ? restMethod : restMethod + " " + queryParametersIn(args);
    }

    private static String queryParametersIn(Object[] args) {
        List<Object> parameters = Arrays.asList(args).subList(1, args.length);
        return (" " + Joiner.on("&").join(parameters)).trim();
    }


    private static void notifyResponse(Response result) {
        String responseBody = result.prettyPrint();
        int statusCode = result.statusCode();

        if (currentRestQuery.get() != null) {
            RestQuery query = currentRestQuery.get();
            if (shouldRecordResponseBodyFor(result)) {
                query = query.withResponse(responseBody).withStatusCode(statusCode);
            }
            getEventBus().getBaseStepListener().recordRestQuery(query);
            currentRestQuery.remove();
        }
    }

    private static boolean shouldRecordResponseBodyFor(Response result) {
        ContentType type=ContentType.fromContentType(result.contentType());
        return type!=null && (ContentType.JSON == type || ContentType.XML == type
            || ContentType.TEXT == type);
    }

    private static void notifyGetOrDelete(Object[] args, RestMethod method) {
        String path = (args.length == 0) ? RestAssured.basePath : args[0].toString();

        RestQuery query = RestQuery.withMethod(method).andPath(path);
        if (queryHasParameters(args)) {
            query = hasParameterMap(args) ? query.withParameters(mapParameters(args)) : query.withParameters(listParameters(args));
        }
        currentRestQuery.set(query);
    }

    private static void notifyPostOrPut(Object[] args, RestMethod method) {
        String path = (args.length == 0) ? RestAssured.basePath : args[0].toString();

        RestQuery query = RestQuery.withMethod(method).andPath(path);
        if (queryHasParameters(args)) {
            query = hasParameterMap(args) ? query.withParameters(mapParameters(args)) : query.withParameters(listParameters(args));
        }
        if (currentQueryPayload() != null) {
            if (currentQueryPayload().getContentType() != null) {
                query = query.withContentType(currentQueryPayload().getContentType() );
            }
            if (currentQueryPayload().getContent() != null) {
                query = query.withContent(currentQueryPayload().getContent() );
            }
        }
        currentRestQuery.set(query);
    }

    private static boolean hasParameterMap(Object[] args) {
        return (args[1] instanceof Map);
    }


    private static Map<String, ?> mapParameters(Object[] args) {
        return (Map<String, ?>) args[1];
    }


    private static List<Object> listParameters(Object[] args) {
        return Lists.newArrayList((Object[]) args[1]);
    }

    private static boolean queryHasParameters(Object[] args) {
        if (args.length <= 1) {
            return false;
        }
        if (args[1].getClass().isArray()) {
            return (((Object[]) args[1]).length > 0);
        }
        if (args[1] instanceof Map) {
            return !((Map) args[1]).isEmpty();
        }
        return false;
    }

}


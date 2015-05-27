package net.serenitybdd.rest;

import com.google.common.collect.ImmutableList;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.rest.RestMethod;
import net.serenitybdd.core.rest.RestQuery;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import net.thucydides.core.steps.StepEventBus;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.core.rest.RestMethod.GET;
import static net.serenitybdd.core.rest.RestMethod.POST;
import static net.serenitybdd.core.rest.RestMethod.restMethodCalled;

public class SerenityRest {

    private static ThreadLocal<RestQuery> currentRestQuery = new ThreadLocal<>();
    private static ThreadLocal<RequestSpecification> currentRequestSpecification = new ThreadLocal<>();
    private static ThreadLocal<QueryPayload> currentQueryPayload = new ThreadLocal<>();

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
        StepEventBus.getEventBus().registerListener(new RestStepListener());
        return currentRequestSpecification.get();
    }

    public static RequestSpecification then() {
        if (currentRequestSpecification.get() == null) {
            return rest();
        } else {
            return currentRequestSpecification.get();
        }
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
                return wrappedResult(method, requestSpecification, args);
            }



        });

        return (RequestSpecification) enhancer.create();
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
                notifyPostOrPut(args, methodType);
                break;
        }
    }

    private static Object wrappedResult(Method method, Object target, Object[] args) {

        try {
            method.setAccessible(true);
            Object result = method.invoke(target, args);
            if (result == null) {
                return null;
            }
            if ((RequestSpecification.class.isAssignableFrom(result.getClass()))) {
                currentRequestSpecification.set(instrumentedRequestSpecificationFor((RequestSpecification) result));
                return currentRequestSpecification.get();
            } else if (Response.class.isAssignableFrom(result.getClass())) {
                notifyResponse((Response) result);
                return result;
            } else {
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static void notifyResponse(Response result) {
        String responseBody = result.prettyPrint();
        int statusCode = result.statusCode();

        if (currentRestQuery.get() != null) {
            RestQuery query = currentRestQuery.get();
            query = query.withResponse(responseBody).withStatusCode(statusCode);
            StepEventBus.getEventBus().getBaseStepListener().recordRestQuery(query);
            currentRestQuery.remove();
        }
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
        return ImmutableList.copyOf((Object[]) args[1]);
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


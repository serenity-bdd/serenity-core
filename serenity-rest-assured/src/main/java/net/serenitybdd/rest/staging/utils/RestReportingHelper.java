package net.serenitybdd.rest.staging.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.LogConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestLogSpecification;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.rest.RestMethod;
import net.serenitybdd.core.rest.RestQuery;
import net.serenitybdd.rest.QueryPayload;
import net.serenitybdd.rest.RestStepListener;
import net.serenitybdd.rest.staging.decorators.ResponseDecorated;
import net.serenitybdd.rest.staging.decorators.request.RequestSpecificationDecorated;
import net.serenitybdd.rest.staging.filters.FieldsRecordingFilter;
import net.serenitybdd.rest.stubs.RequestSpecificationStub;
import net.serenitybdd.rest.stubs.ResponseSpecificationStub;
import net.serenitybdd.rest.stubs.ResponseStub;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFailure;
import org.apache.commons.lang3.ObjectUtils;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.thucydides.core.steps.StepEventBus.getEventBus;
import static org.apache.commons.lang3.ObjectUtils.*;


/**
 * User: YamStranger
 * Date: 4/6/16
 * Time: 7:51 AM
 */
public class RestReportingHelper {

    private RestQuery query;
    private QueryPayload payload = new QueryPayload();

    public RestReportingHelper() {
        getEventBus().registerListener(new RestStepListener());
    }

    private void notifyGetOrDelete(Object[] args, net.serenitybdd.core.rest.RestMethod method) {
        String path = (args.length == 0) ? RestAssured.basePath : args[0].toString();

        RestQuery query = RestQuery.withMethod(method).andPath(path);
        if (queryHasParameters(args)) {
            query = hasParameterMap(args) ? query.withParameters(mapParameters(args)) : query.withParameters(listParameters(args));
        }
        this.query = query;
    }

    private void notifyPostOrPut(Object[] args, net.serenitybdd.core.rest.RestMethod method) {
        String path = (args.length == 0) ? RestAssured.basePath : args[0].toString();

        RestQuery query = RestQuery.withMethod(method).andPath(path);
        if (queryHasParameters(args)) {
            query = hasParameterMap(args) ? query.withParameters(mapParameters(args)) : query.withParameters(listParameters(args));
        }
        if (this.query != null) {
            if (this.query.getContentType() != null) {
                query = query.withContentType(this.query.getContentType());
            }
            if (this.query.getContent() != null) {
                query = query.withContent(this.query.getContent());
            }
        }
        this.query = query;
    }

    private boolean queryHasParameters(Object[] args) {
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

    private boolean hasParameterMap(Object[] args) {
        return (args[1] instanceof Map);
    }

    private Map<String, ?> mapParameters(Object[] args) {
        return (Map<String, ?>) args[1];
    }

    private void registerContentType(String contentType) {
        this.getPayload().setContentType(contentType);
    }

    private void registerContent(String content) {
        this.getPayload().setContent(content);
    }

    private List<Object> listParameters(Object[] args) {
        return Lists.newArrayList((Object[]) args[1]);
    }

    private void notifyResponse(Response result) {
        String responseBody = result.prettyPrint();
        int statusCode = result.statusCode();

        if (this.query != null) {
            RestQuery query = this.query;
            if (shouldRecordResponseBodyFor(result)) {
                query = query.withResponse(responseBody).withStatusCode(statusCode);
            }
            getEventBus().getBaseStepListener().recordRestQuery(query);
            this.payload = null;
        }
    }

    private static boolean shouldRecordResponseBodyFor(Response result) {
        ContentType type = ContentType.fromContentType(result.contentType());
        return type != null && (ContentType.JSON == type || ContentType.XML == type
                || ContentType.TEXT == type);
    }

    public QueryPayload getPayload() {
        if (this.payload == null) {
            this.payload = new QueryPayload();
        }
        return payload;
    }

    public boolean restCallsAreEnabled() {
        return !(getEventBus().isDryRun() || getEventBus().currentTestIsSuspended());
    }

    public RestQuery recordRestSpecificationData(final RestMethod method, final RequestSpecificationDecorated spec,
                                                 final String path, final Object... params) {
        final Map<LogDetail, String> values = new HashMap<>();
        for (final Filter filter : spec.getDefinedFilters()) {
            if (filter instanceof FieldsRecordingFilter) {
                final FieldsRecordingFilter internal = (FieldsRecordingFilter) filter;
                values.put(internal.logDetail(), internal.recorded());
            }
        }

        final RestQuery query = RestQuery.
                withMethod(method).andPath(path).
                withContentType(String.valueOf(
                                ContentType.fromContentType(spec.getRequestContentType()))
                ).
                withContent(ObjectUtils.firstNonNull(values.get(LogDetail.BODY), ""));
        return query;
    }

    public void registerCall(final RestMethod method, final ResponseDecorated response,
                             final RequestSpecificationDecorated spec,
                             final String path, final Object... params) {

        RestQuery restQuery = recordRestSpecificationData(method, spec, path, params);
        if (shouldRecordResponseBodyFor(response)) {
            restQuery = restQuery.
                    withResponse(response.getBody().prettyPrint()).
                    withStatusCode(response.getStatusCode());
        }
        getEventBus().getBaseStepListener().recordRestQuery(restQuery);

        /*RestQuery query = RestQuery.withMethod(method).andPath(path);
        this.query = query;*/
        //notifyGetOrDelete
        //executeRestQuery
        /**
         * wrappedResult
         try{
         if (restCallsAreDisabled()) {
         return stubbed(method);
         }
         Object result = invokeMethod(method, target, args);

         notifyResponse((Response) result){;
         if (shouldRecordResponseBodyFor(result)) {
         query = query.withResponse(responseBody).withStatusCode(statusCode);
         }
         getEventBus().getBaseStepListener().recordRestQuery(query);
         }
         } catch (Exception generalException) {
         Throwable error = SerenityManagedException.detachedCopyOf(generalException.getCause());
         Throwable assertionError = forError(error).convertToAssertion();
         notifyOfStepFailure(method, args, assertionError);
         return stubbed(method);
         }

         */
    }

    public void registerCall(final RestMethod type, final RequestSpecificationDecorated spec, final String path,
                             final RuntimeException throwable, final Object... params) {
        ExecutedStepDescription description = ExecutedStepDescription.withTitle(type.toString());
        StepFailure failure = new StepFailure(description, throwable);
        StepEventBus.getEventBus().stepStarted(description);
        StepEventBus.getEventBus().stepFailed(failure);
        if (Serenity.shouldThrowErrorsImmediately()) {
            throw throwable;
        }
    }

    public <T> T stubbed(final T type) {
        return (T) Mockito.mock(type.getClass());
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
}

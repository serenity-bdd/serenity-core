package net.serenitybdd.rest.decorators.request;

import io.restassured.filter.*;
import io.restassured.internal.*;
import io.restassured.response.*;
import io.restassured.specification.*;
import net.serenitybdd.core.*;
import net.serenitybdd.core.rest.*;
import net.serenitybdd.rest.stubs.*;
import net.serenitybdd.rest.utils.*;
import org.slf4j.*;

import java.net.*;
import java.util.*;

import static net.serenitybdd.core.rest.RestMethod.*;
import static net.thucydides.core.steps.StepEventBus.*;
import static org.apache.http.util.Args.*;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
public abstract class RequestSpecificationDecorated extends RequestSpecificationAdvancedConfiguration
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationDecorated.class);
    private Response lastResponse;

    public RequestSpecificationDecorated(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public Response get() {
        return get("");
    }

    @Override
    public Response get(URL url) {
        return get(notNull(url, "URL").toString());
    }

    @Override
    public Response get(String path, Object... pathParams) {
        return execute(GET, path, pathParams);
    }

    @Override
    public Response get(String path, Map<String, ?> pathParams) {
        pathParameters(pathParams);
        return get(path);
    }

    @Override
    public Response get(URI uri) {
        return get(notNull(uri, "URI").toString());
    }

    @Override
    public Response post() {
        return post("");
    }

    @Override
    public Response post(URL url) {
        return post(notNull(url, "URL").toString());
    }

    @Override
    public Response post(String path, Object... pathParams) {
        return execute(POST, path, pathParams);
    }

    @Override
    public Response post(String path, Map<String, ?> pathParams) {
        pathParameters(pathParams);
        return post(path);
    }

    @Override
    public Response post(URI uri) {
        return post(notNull(uri, "URI").toString());
    }

    @Override
    public Response put() {
        return put("");
    }

    @Override
    public Response put(URL url) {
        return put(notNull(url, "URL").toString());
    }

    @Override
    public Response put(URI uri) {
        return put(notNull(uri, "URI").toString());
    }

    @Override
    public Response put(String path, Object... pathParams) {
        return execute(PUT, path, pathParams);
    }

    @Override
    public Response put(String path, Map<String, ?> pathParams) {
        pathParameters(pathParams);
        return put(path);
    }

    @Override
    public Response delete() {
        return delete("");
    }

    @Override
    public Response delete(URL url) {
        return delete(notNull(url, "URL").toString());
    }

    @Override
    public Response delete(URI uri) {
        return delete(notNull(uri, "URI").toString());
    }

    @Override
    public Response delete(String path, Object... pathParams) {
        return execute(DELETE, path, pathParams);
    }

    @Override
    public Response delete(String path, Map<String, ?> pathParams) {
        pathParameters(pathParams);
        return delete(path);
    }

    @Override
    public Response head() {
        return head("");
    }

    @Override
    public Response head(URL url) {
        return head(notNull(url, "URL").toString());
    }

    @Override
    public Response head(URI uri) {
        return head(notNull(uri, "URI").toString());
    }

    @Override
    public Response head(String path, Object... pathParams) {
        return execute(HEAD, path, pathParams);
    }

    @Override
    public Response head(String path, Map<String, ?> pathParams) {
        pathParameters(pathParams);
        return head(path);
    }

    @Override
    public Response patch() {
        return patch("");
    }

    @Override
    public Response patch(URL url) {
        return patch(notNull(url, "URL").toString());
    }

    @Override
    public Response patch(URI uri) {
        return patch(notNull(uri, "URI").toString());
    }

    @Override
    public Response patch(String path, Object... pathParams) {
        return execute(PATCH, path, pathParams);
    }

    @Override
    public Response patch(String path, Map<String, ?> pathParams) {
        pathParameters(pathParams);
        return patch(path);
    }

    @Override
    public Response options() {
        return options("");
    }

    @Override
    public Response options(String path, Object... pathParams) {
        return execute(OPTIONS, path, pathParams);
    }

    @Override
    public Response options(String path, Map<String, ?> pathParams) {
        pathParameters(pathParams);
        return options(path);
    }

    @Override
    public Response options(URI uri) {
        return options(notNull(uri, "URI").toString());
    }

    @Override
    public Response options(URL url) {
        return options(notNull(url, "URL").toString());
    }

    protected Response execute(final RestMethod method, final String path, final Object... pathParams) {
        Response response = null;
        RuntimeException exception = null;
        try {
            response = executeCall(method, path, pathParams);
            if (RestExecutionHelper.restCallsAreDisabled()) {
                response = stubbed();
            }
        } catch (RuntimeException e) {
            exception = e;
        }
        if (exception != null) {
            reportError(method, path, exception, pathParams);
            if (Serenity.shouldThrowErrorsImmediately()) {
                throw exception;
            } else {
                response = stubbed();
            }
        } else {
            reportQuery(method, path, response, pathParams);
        }
        this.lastResponse = response;
        return response;
    }

    private void reportQuery(RestMethod method, String path, Response response, Object[] pathParams) {
        if (getEventBus().isBaseStepListenerRegistered()) {
            reporting.registerCall(method, response, this, path, pathParams);
        } else {
            log.info("No BaseStepListener, {} {} not registered.", method.toString(), path);
        }
    }

    private void reportError(RestMethod method, String path, RuntimeException exception, Object[] pathParams) {
        if (getEventBus().isBaseStepListenerRegistered()) {
            reporting.registerCall(method, this, path, exception, pathParams);
        } else {
            log.info("No BaseStepListener, {} {} not registered.", method.toString(), path);
        }
    }

    private Response executeCall(RestMethod method, String path, Object[] pathParams) {
        switch (method) {
            case POST:
                return decorate(this.core.post(path, pathParams));
            case GET:
                return decorate(this.core.get(path, pathParams));
            case DELETE:
                return decorate(this.core.delete(path, pathParams));
            case PUT:
                return decorate(this.core.put(path, pathParams));
            case HEAD:
                return decorate(this.core.head(path, pathParams));
            case OPTIONS:
                return decorate(this.core.options(path, pathParams));
            case PATCH:
                return decorate(this.core.patch(path, pathParams));
            default:
                throw new IllegalArgumentException("Unknown REST query type: " + method);
        }
    }

    public Response getLastResponse() {
        return this.lastResponse;
    }

    private Response stubbed() {
        return new ResponseStub();
    }

    /**
     * Add a filter that will be used in the request.
     *
     * @param filter The filter to add
     * @return the decorated request specification
     */
    @Override
    public RequestSpecification filter(Filter filter){
        return RestDecorationHelper.decorate(core.filter(filter));
    };
}

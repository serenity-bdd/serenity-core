package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.response.*;
import com.jayway.restassured.specification.*;
import net.serenitybdd.core.rest.RestMethod;
import net.serenitybdd.rest.staging.decorators.ResponseDecorated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.util.Map;

import static com.jayway.restassured.internal.assertion.AssertParameter.notNull;
import static net.serenitybdd.core.rest.RestMethod.*;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
public class RequestSpecificationDecorated extends RequestSpecificationAdvancedConfiguration
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationDecorated.class);

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
        ResponseDecorated response = null;
        RuntimeException exception = null;
/*        if (restCallsAreDisabled()) {
            return stubbed(method);
        }*/
        try {
            switch (method) {
                case POST:
                    response = decorate(this.core.post(path, pathParams));
                    break;
                case GET:
                    response = decorate(this.core.get(path, pathParams));
                    break;
                case DELETE:
                    response = decorate(this.core.delete(path, pathParams));
                    break;
                case PUT:
                    response = decorate(this.core.put(path, pathParams));
                    break;
                case HEAD:
                    response = decorate(this.core.head(path, pathParams));
                    break;
                case OPTIONS:
                    response = decorate(this.core.options(path, pathParams));
                    break;
                case PATCH:
                    response = decorate(this.core.patch(path, pathParams));
                    break;
            }
        } catch (RuntimeException e) {
            exception = e;
        } finally {
            if (exception == null) {
                reporting.registerCall(method, response, this, path, pathParams);
            } else {
                reporting.registerCall(method, this, path, exception, pathParams);
                throw exception;
            }
        }
        return response;
    }
}
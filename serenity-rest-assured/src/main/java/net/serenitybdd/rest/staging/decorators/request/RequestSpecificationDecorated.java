package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.response.*;
import com.jayway.restassured.specification.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.util.Map;

import static com.jayway.restassured.internal.assertion.AssertParameter.notNull;

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
        return decorate(core.get(path, pathParams));
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
        return core.post();
    }

    @Override
    public Response post(URL url) {
        return core.post(url);
    }

    @Override
    public Response post(String path, Object... pathParams) {
        return core.post(path, pathParams);
    }

    @Override
    public Response post(String path, Map<String, ?> pathParams) {
        return core.post(path, pathParams);
    }

    @Override
    public Response post(URI uri) {
        return core.post(uri);
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
        return decorate(core.put(path, pathParams));
    }

    @Override
    public Response put(String path, Map<String, ?> pathParams) {
        pathParameters(pathParams);
        return put(path);
    }

    @Override
    public Response delete() {
        return core.delete();
    }

    @Override
    public Response delete(URL url) {
        return core.delete(url);
    }

    @Override
    public Response delete(URI uri) {
        return core.delete(uri);
    }

    @Override
    public Response delete(String path, Object... pathParams) {
        return core.delete(path, pathParams);
    }

    @Override
    public Response delete(String path, Map<String, ?> pathParams) {
        return core.delete(path, pathParams);
    }

    @Override
    public Response head() {
        return core.head();
    }

    @Override
    public Response head(URL url) {
        return core.head(url);
    }

    @Override
    public Response head(URI uri) {
        return core.head(uri);
    }

    @Override
    public Response head(String path, Object... pathParams) {
        return core.head(path, pathParams);
    }

    @Override
    public Response head(String path, Map<String, ?> pathParams) {
        return core.head(path, pathParams);
    }

    @Override
    public Response patch() {
        return core.patch();
    }

    @Override
    public Response patch(URL url) {
        return core.patch(url);
    }

    @Override
    public Response patch(URI uri) {
        return core.patch(uri);
    }

    @Override
    public Response patch(String path, Object... pathParams) {
        return core.patch(path, pathParams);
    }

    @Override
    public Response patch(String path, Map<String, ?> pathParams) {
        return core.patch(path, pathParams);
    }

    @Override
    public Response options() {
        return core.options();
    }

    @Override
    public Response options(String path, Object... pathParams) {
        return core.options(path, pathParams);
    }

    @Override
    public Response options(String path, Map<String, ?> pathParams) {
        return core.options(path, pathParams);
    }

    @Override
    public Response options(URI uri) {
        return core.options(uri);
    }

    @Override
    public Response options(URL url) {
        return core.options(url);
    }
}
package net.serenitybdd.rest;

import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.net.URI;
import java.net.URL;
import java.util.Map;


/**
 * User: YamStranger
 * Date: 4/5/16
 * Time: 8:27 PM
 * This class directly calls some methods from SerenityRest related only to
 * making requests, can be used for better readability of code.
 * There is no difference in behaving between this class or SerenityRest.
 */
public class RestRequests {
    public static Response get() {
        return SerenityRest.get();
    }

    public static Response get(final String path, final Map<String, ?> pathParams) {
        return SerenityRest.get(path, pathParams);
    }

    public static Response get(final String path, final Object... pathParams) {
        return SerenityRest.get(path, pathParams);
    }

    public static Response get(final URI uri) {
        return SerenityRest.get(uri);
    }

    public static Response get(final URL url) {
        return SerenityRest.get(url);
    }

    public static Response delete(final String path, final Object... pathParams) {
        return SerenityRest.delete(path, pathParams);
    }

    public static Response delete() {
        return SerenityRest.delete();
    }

    public static Response delete(final URI uri) {
        return SerenityRest.delete(uri);
    }

    public static Response delete(final URL url) {
        return SerenityRest.delete(url);
    }

    public static Response delete(final String path, final Map<String, ?> pathParams) {
        return SerenityRest.delete(path, pathParams);
    }

    public static Response put(final URL url) {
        return SerenityRest.put(url);
    }

    public static Response put() {
        return SerenityRest.put();
    }

    public static Response put(final String path, final Object... pathParams) {
        return SerenityRest.put(path, pathParams);
    }

    public static Response put(final URI uri) {
        return SerenityRest.put(uri);
    }

    public static Response post(final URI uri) {
        return SerenityRest.post(uri);
    }

    public static Response post() {
        return SerenityRest.post();
    }

    public static Response post(final URL url) {
        return SerenityRest.post(url);
    }

    public static Response post(final String path, final Map<String, ?> pathParams) {
        return SerenityRest.post(path, pathParams);
    }

    public static Response post(final String path, final Object... pathParams) {
        return SerenityRest.post(path, pathParams);
    }

    public static Response patch(final String path, final Object... pathParams) {
        return SerenityRest.patch(path, pathParams);
    }

    public static Response patch() {
        return SerenityRest.patch();
    }

    public static Response patch(final URL url) {
        return SerenityRest.patch(url);
    }

    public static Response patch(final URI uri) {
        return SerenityRest.patch(uri);
    }

    public static Response patch(final String path, final Map<String, ?> pathParams) {
        return SerenityRest.patch(path, pathParams);
    }

    public static Response options(final String path, final Map<String, ?> pathParams) {
        return SerenityRest.options(path, pathParams);
    }

    public static Response options(final String path, final Object... pathParams) {
        return SerenityRest.options(path, pathParams);
    }

    public static Response options(final URI uri) {
        return SerenityRest.options(uri);
    }

    public static Response options(final URL url) {
        return SerenityRest.options(url);
    }

    public static Response options() {
        return SerenityRest.options();
    }

    public static Response head(final URL url) {
        return SerenityRest.head(url);
    }

    public static Response head(final String path, final Map<String, ?> pathParams) {
        return SerenityRest.head(path, pathParams);
    }

    public static Response head(final String path, final Object... pathParams) {
        return SerenityRest.head(path, pathParams);
    }

    public static Response head() {
        return SerenityRest.head();
    }

    public static Response head(final URI uri) {
        return SerenityRest.head(uri);
    }

    public static RequestSpecification given() {
        return SerenityRest.given();
    }

    public static RequestSender when() {
        return SerenityRest.when();
    }

    public static RequestSender given(final RequestSpecification request, final ResponseSpecification response) {
        return SerenityRest.given(request, response);
    }

    public static RequestSpecification given(final RequestSpecification requestSpecification) {
        return SerenityRest.given(requestSpecification);
    }

    public static ResponseSpecification expect() {
        return SerenityRest.expect();
    }

    public static RequestSpecification with() {
        return SerenityRest.with();
    }
}

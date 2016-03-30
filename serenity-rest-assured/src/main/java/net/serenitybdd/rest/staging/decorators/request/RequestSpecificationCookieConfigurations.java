package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.response.Cookie;
import com.jayway.restassured.response.Cookies;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationCookieConfigurations extends RequestSpecificationSecurityConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationCookieConfigurations.class);

    public RequestSpecificationCookieConfigurations(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public Cookies getCookies() {
        return core.getCookies();
    }

    @Override
    public RequestSpecification cookies(Map<String, ?> cookies) {
        return core.cookies(cookies);
    }

    @Override
    public RequestSpecification cookies(Cookies cookies) {
        return core.cookies(cookies);
    }

    @Override
    public RequestSpecification cookie(String cookieName, Object value, Object... additionalValues) {
        return core.cookie(cookieName, value, additionalValues);
    }

    @Override
    public RequestSpecification cookie(String cookieName) {
        return core.cookie(cookieName);
    }

    @Override
    public RequestSpecification cookie(Cookie cookie) {
        return core.cookie(cookie);
    }

    @Override
    public RequestSpecification cookies(String firstCookieName, Object firstCookieValue, Object... cookieNameValuePairs) {
        return core.cookies(firstCookieName, firstCookieValue, cookieNameValuePairs);
    }
}
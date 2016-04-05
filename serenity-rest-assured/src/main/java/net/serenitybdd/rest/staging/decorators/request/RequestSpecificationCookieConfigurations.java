package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.internal.MapCreator;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.internal.log.LogRepository;
import com.jayway.restassured.response.Cookie;
import com.jayway.restassured.response.Cookies;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.internal.assertion.AssertParameter.notNull;
import static java.util.Arrays.asList;

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
        notNull(cookies, "cookies");
        List<Cookie> list = new LinkedList<>();
        for (Map.Entry<String, ?> cookie : cookies.entrySet()) {
            list.add(new Cookie.Builder(cookie.getKey(),
                    serializeIfNeeded(cookie.getValue())).build());
        }
        return cookies(new Cookies(list));
    }

    @Override
    public RequestSpecification cookies(Cookies cookies) {
        notNull(cookies, "cookies");
        core.cookies(cookies);
        return this;
    }

    @Override
    public RequestSpecification cookie(String cookieName, Object value, Object... additionalValues) {
        List<Cookie> list = new LinkedList<>();
        list.add(new Cookie.Builder(cookieName, serializeIfNeeded(value)).build());
        for (Object additionalValue : additionalValues) {
            list.add(new Cookie.Builder(cookieName, serializeIfNeeded(additionalValue)).build());
        }
        return cookies(new Cookies(list));
    }

    @Override
    public RequestSpecification cookie(String cookieName) {
        return cookie(cookieName, null);
    }

    @Override
    public RequestSpecification cookie(Cookie cookie) {
        notNull(cookie, "Cookie");
        return cookies(new Cookies(asList(cookie)));
    }

    @Override
    public RequestSpecification cookies(String firstCookieName, Object firstCookieValue, Object... cookieNameValuePairs) {
        return cookies(MapCreator.createMapFromParams(firstCookieName, firstCookieValue, cookieNameValuePairs));
    }

    protected String serializeIfNeeded(final Object object) {
        if (object == null) {
            return null;
        }
        try {
            return (String) this.helper.executeFunction("serializeIfNeeded", new Class[]{Object.class}, object);
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not execute serializeIfNeeded from request, SerenityRest can work incorrectly", e);
        }
    }
}
package net.serenitybdd.rest.decorators.request;

import com.google.common.base.Preconditions;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.internal.MapCreator;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        Preconditions.checkNotNull(cookies, "cookies");
        List<Cookie> list = new LinkedList<>();
        for (Map.Entry<String, ?> cookie : cookies.entrySet()) {
            list.add(new Cookie.Builder(cookie.getKey(),
                    serializeIfNeeded(cookie.getValue())).build());
        }
        return cookies(new Cookies(list));
    }

    @Override
    public RequestSpecification cookies(Cookies cookies) {
        Preconditions.checkNotNull(cookies, "cookies");
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
        Preconditions.checkNotNull(cookie, "Cookie");
        return cookies(new Cookies(asList(cookie)));
    }

    @Override
    public RequestSpecification cookies(String firstCookieName, Object firstCookieValue, Object... cookieNameValuePairs) {
        return cookies(MapCreator.createMapFromParams(MapCreator.CollisionStrategy.OVERWRITE, firstCookieName, firstCookieValue, cookieNameValuePairs));
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

package net.serenitybdd.rest.staging.utils;

import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.internal.ResponseSpecificationImpl;
import com.jayway.restassured.internal.filter.SendRequestFilter;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import net.serenitybdd.rest.staging.decorators.ResponseSpecificationDecorated;
import net.serenitybdd.rest.staging.decorators.request.RequestSpecificationDecorated;
import net.serenitybdd.rest.staging.filters.FieldsRecordingFilter;
import net.serenitybdd.rest.staging.filters.UpdatingContextFilter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.jayway.restassured.filter.log.LogDetail.*;

/**
 * User: YamStranger
 * Date: 4/15/16
 * Time: 8:08 AM
 */
public class RestDecorationHelper {
    public static RequestSpecification decorate(final RequestSpecification specification) {
        if (specification instanceof RequestSpecificationDecorated) {
            return specification;
        } else if (specification instanceof RequestSpecificationImpl) {
            final RequestSpecificationDecorated decorated = new RequestSpecificationDecorated((RequestSpecificationImpl) specification);
            final List<Filter> filters = new LinkedList<>();
            for (final LogDetail logDetail : Arrays.asList(HEADERS, COOKIES, BODY, PARAMS, METHOD, PATH)) {
                filters.add(new FieldsRecordingFilter(true, logDetail));
            }
            if (RestExecutionHelper.restCallsAreEnabled()) {
                filters.add(new UpdatingContextFilter(SendRequestFilter.class));
            }
            decorated.filters(filters);
            return decorated;
        } else {
            throw new IllegalArgumentException("Can not be used custom Request Specification Implementation");
        }
    }

    public static ResponseSpecification decorate(final ResponseSpecification specification) {
        if (specification instanceof ResponseSpecificationDecorated) {
            return specification;
        } else if (specification instanceof ResponseSpecificationImpl) {
            return new ResponseSpecificationDecorated((ResponseSpecificationImpl) specification);
        } else {
            throw new IllegalArgumentException("Can not be used custom Response Specification Implementation");
        }
    }
}

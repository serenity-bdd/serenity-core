package net.serenitybdd.rest.filters;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.internal.filter.FilterContextImpl;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import net.serenitybdd.rest.utils.RestRuntimeException;
import net.serenitybdd.rest.stubs.ResponseStub;
import net.serenitybdd.rest.utils.ReflectionHelper;
import net.serenitybdd.rest.utils.RestExecutionHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * User: YamStranger
 * Date: 4/15/16
 * Time: 7:40 AM
 */
public class UpdatingContextFilter implements Filter {
    final private Set<Class> skipping = new HashSet<>();

    public UpdatingContextFilter(final Class... skip) {
        skipping.addAll(Arrays.asList(skip));
    }

    @Override
    public Response filter(final FilterableRequestSpecification requestSpec,
                           final FilterableResponseSpecification responseSpec, final FilterContext ctx) {
        try {
            final ReflectionHelper<FilterContextImpl> helper = new ReflectionHelper<>((FilterContextImpl) ctx);
            Object iterator = helper.getValueFrom("filters");
            helper.setValueTo("filters", new SkipClassIterator((Iterator<Filter>) iterator, skipping));
            return ctx.next(requestSpec, responseSpec);
        } catch (Throwable e) {
            if (RestExecutionHelper.restCallsAreDisabled()) {
                return stubbed();
            }
            throw new RestRuntimeException("Incorrect implementation, should update field without any problem", e);
        }
    }

    private Response stubbed() {
        return new ResponseStub();
    }


}

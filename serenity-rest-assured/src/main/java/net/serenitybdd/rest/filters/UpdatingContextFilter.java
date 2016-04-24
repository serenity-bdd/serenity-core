package net.serenitybdd.rest.filters;

import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.filter.FilterContext;
import com.jayway.restassured.internal.filter.FilterContextImpl;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.FilterableResponseSpecification;
import net.serenitybdd.rest.utils.ReflectionHelper;

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
            throw new RuntimeException("Incorrect implementation, should update field without any problem", e);
        }
    }
}

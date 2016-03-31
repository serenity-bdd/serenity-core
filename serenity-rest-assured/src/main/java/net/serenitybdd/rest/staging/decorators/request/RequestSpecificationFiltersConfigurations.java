package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.authentication.AuthenticationScheme;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationFiltersConfigurations extends RequestSpecificationConfigurable
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationFiltersConfigurations.class);

    public RequestSpecificationFiltersConfigurations(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public RequestSpecification filter(Filter filter) {
        return core.filter(filter);
    }

    @Override
    public RequestSpecification filters(List<Filter> filters) {
        return core.filters(filters);
    }

    @Override
    public RequestSpecification filters(Filter filter, Filter... additionalFilter) {
        return core.filters(filter, additionalFilter);
    }

    @Override
    public RequestSpecification noFilters() {
        return core.noFilters();
    }

    @Override
    public List<Filter> getDefinedFilters() {
        return core.getDefinedFilters();
    }

    @Override
    public <T extends Filter> RequestSpecification noFiltersOfType(Class<T> filterType) {
        return core.noFiltersOfType(filterType);
    }

    /**
     * Method created ONLY for using in groovy (rest assured internals)
     */
    protected List<Filter> getFilters() {
        try {
            return (List<Filter>) this.helper.getValueFrom("filters");
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not get filters from request, SerenityRest can work incorrectly");
        }
    }

    /**
     * Method created ONLY for using in groovy (rest assured internals)
     */
    protected List<Filter> getfilters() {
        return getFilters();
    }
}
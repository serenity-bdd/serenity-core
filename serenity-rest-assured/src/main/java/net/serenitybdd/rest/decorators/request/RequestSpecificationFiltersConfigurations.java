package net.serenitybdd.rest.decorators.request;

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
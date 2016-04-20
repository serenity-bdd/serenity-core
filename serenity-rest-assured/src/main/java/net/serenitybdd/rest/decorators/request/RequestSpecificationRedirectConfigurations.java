package net.serenitybdd.rest.decorators.request;

import com.jayway.restassured.internal.RedirectSpecificationImpl;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RedirectSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationRedirectConfigurations extends RequestSpecificationBodyConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationRedirectConfigurations.class);

    public RequestSpecificationRedirectConfigurations(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public RedirectSpecification redirects() {
        return new RedirectSpecificationImpl(this, httpClientParams());
    }

    protected Map<String, Object> httpClientParams() {
        try {
            return (Map<String, Object>) this.helper.getValueFrom("httpClientParams");
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not get httpClientParams from request, SerenityRest can work incorrectly");
        }
    }
}
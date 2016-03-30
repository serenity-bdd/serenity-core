package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.internal.RequestLogSpecificationImpl;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.internal.log.LogRepository;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RequestLogSpecification;
import net.serenitybdd.rest.staging.decorators.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationLoggable extends RequestSpecificationRedirectConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationLoggable.class);

    public RequestSpecificationLoggable(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public RequestLogSpecification log() {
        final RequestLogSpecificationImpl specification = new RequestLogSpecificationImpl();
        final ReflectionHelper<RequestLogSpecificationImpl> logHelper = new ReflectionHelper<>(specification);
        try {
            logHelper.setValueTo("requestSpecification", this);
            logHelper.setValueTo("logRepository", logRepository());
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not set requestSpecification or logRepository to RequestLogSpecificationImpl, SerenityRest can work incorrectly");
        }
        return specification;
    }

    protected LogRepository logRepository() {
        try {
            return (LogRepository) this.helper.getValueFrom("logRepository");
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not get logRepository from request, SerenityRest can work incorrectly");
        }
    }
}
package net.serenitybdd.rest.decorators;

import com.jayway.restassured.internal.ResponseParserRegistrar;
import com.jayway.restassured.internal.ResponseSpecificationImpl;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.FilterableResponseSpecification;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import net.serenitybdd.rest.decorators.request.RequestSpecificationDecorated;
import net.serenitybdd.rest.utils.ReflectionHelper;
import net.serenitybdd.rest.utils.RestDecorationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
public abstract class ResponseSpecificationDecorated implements FilterableResponseSpecification {
    private static final Logger log = LoggerFactory.getLogger(ResponseSpecificationDecorated.class);
    private final ResponseSpecificationImpl core;
    private final ReflectionHelper<ResponseSpecificationImpl> helper;

    public ResponseSpecificationDecorated(final ResponseSpecificationImpl core) {
        this.core = core;
        this.helper = new ReflectionHelper<ResponseSpecificationImpl>(core);
    }

    @Override
    public ResponseSpecification response() {
        return this;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private ResponseSpecification check(final ResponseSpecification specification) {
        if (specification instanceof RequestSpecificationDecorated) {
            return specification;
        } else {
            log.warn("returned not decorated response, SerenityRest can work incorrectly");
            return specification;
        }
    }

    protected void setrequestSpecification(final RequestSpecification specification) {
        setRequestSpecification(specification);
    }

    protected void setRequestSpecification(final RequestSpecification specification) {
        try {
            this.helper.setValueTo("requestSpecification", RestDecorationHelper.decorate(specification));
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not set specification to response, SerenityRest can work incorrectly");
        }
    }

    /**
     * This method used when in groovy 'response.assertionClosure = value' called
     *
     * @param assertionClosure HamcrestAssertionClosure value that will be set to repose specification
     */
    protected void setassertionClosure(final ResponseSpecificationImpl.HamcrestAssertionClosure assertionClosure) {
        setAssertionClosure(assertionClosure);
    }

    protected void setAssertionClosure(final ResponseSpecificationImpl.HamcrestAssertionClosure assertionClosure) {
        try {
            this.helper.setValueTo("assertionClosure", assertionClosure);
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not set assertion closure to response, SerenityRest can work incorrectly");
        }
    }


    /**
     * This method used when in groovy 'value = response.assertionClosure' called
     *
     * @return HamcrestAssertionClosure value of current object
     */
    protected ResponseSpecificationImpl.HamcrestAssertionClosure getassertionClosure() {
        return getAssertionClosure();
    }

    protected ResponseSpecificationImpl.HamcrestAssertionClosure getAssertionClosure() {
        try {
            return (ResponseSpecificationImpl.HamcrestAssertionClosure) this.helper.getValueFrom("assertionClosure");
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not get assertion closure from response, SerenityRest can work incorrectly");
        }
    }

    /**
     * This method used when in groovy 'response.restAssuredResponse = value' called
     *
     * @param restAssuredResponse Response value that will be set to rest assured response
     */
    protected void setrestAssuredResponse(final Response restAssuredResponse) {
        setRestAssuredResponse(restAssuredResponse);
    }

    protected void setRestAssuredResponse(final Response restAssuredResponse) {
        try {
            this.helper.setValueTo("restAssuredResponse", restAssuredResponse);
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not set assertion closure to response, SerenityRest can work incorrectly");
        }
    }

    /**
     * This method used when in groovy 'value = response.restAssuredResponse' called
     *
     * @return HamcrestAssertionClosure value of current object
     */
    protected Response getrestAssuredResponse() {
        return getRestAssuredResponse();
    }

    protected Response getRestAssuredResponse() {
        try {
            return (Response) this.helper.getValueFrom("restAssuredResponse");
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not get assertion closure from response, SerenityRest can work incorrectly");
        }
    }

    public ResponseParserRegistrar getRpr() {
        return core.getRpr();
    }

    public void setRpr(final ResponseParserRegistrar rpr) {
        core.setRpr(rpr);
    }

    public boolean hasBodyAssertionsDefined() {
        return core.hasBodyAssertionsDefined();
    }

}
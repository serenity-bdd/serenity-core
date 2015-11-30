package net.serenitybdd.rest.decorators;

import com.jayway.restassured.specification.PreemptiveAuthSpec;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * User: YamStranger
 * Date: 11/30/15
 * Time: 12:18 AM
 */
public class PreemptiveAuthSpecWrapper extends BaseWrapper<PreemptiveAuthSpec>
    implements PreemptiveAuthSpec {

    public PreemptiveAuthSpecWrapper(final PreemptiveAuthSpec auth,
                                     final ThreadLocal<RequestSpecification> instrumented,
                                     final RestDecorator decorator) {
        super(auth, instrumented, decorator);
    }

    @Override
    public RequestSpecification basic(final String username,
                                      final String password) {
        this.core.basic(username, password);
        return this.specification.get();
    }
}
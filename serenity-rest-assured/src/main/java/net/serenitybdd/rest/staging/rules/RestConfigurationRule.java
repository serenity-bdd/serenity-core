package net.serenitybdd.rest.staging.rules;

import net.serenitybdd.rest.staging.SerenityRest;
import net.thucydides.core.steps.StepEventBus;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: YamStranger
 * Date: 3/14/16
 * Time: 1:34 PM
 */
public class RestConfigurationRule implements MethodRule {

    private final ArrayList<RestConfigurationAction> actions = new ArrayList<>();

    public RestConfigurationRule(RestConfigurationAction... action) {
        actions.addAll(Arrays.asList(action));
    }

    @Override
    public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object o) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    for (RestConfigurationAction action : RestConfigurationRule.this.actions) {
                        action.apply();
                    }
                    statement.evaluate();
                } finally {
                    SerenityRest.reset();
                }
            }
        };
    }
}

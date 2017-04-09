package net.serenitybdd.rest.rules;

import net.serenitybdd.rest.SerenityRest;
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

    public RestConfigurationRule(final RestConfigurationAction... action) {
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

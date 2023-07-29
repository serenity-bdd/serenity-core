package net.serenitybdd.junit.spring.integration;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks;
import org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks;

/**
 * A {@link org.junit.rules.TestRule} to be used with @{@link org.junit.ClassRule} to run the @BeforeClass and @AfterClass modifiers that are part of {@link org.springframework.test.context.junit4.SpringJUnit4ClassRunner}.
 *
 * @author scott.dennison@costcutter.com
 */
public class SpringIntegrationClassRule extends SpringIntegrationRuleBase implements TestRule {
    private static final Logger LOG = LoggerFactory.getLogger(SpringIntegrationClassRule.class);

    /**
     * Wraps {@link RunBeforeTestClassCallbacks} and {@link RunAfterTestClassCallbacks} around the provided statement.
     *
     * @param base        The base statement
     * @param description Information about the test class being run.
     * @return The wrapped statement.
     */
    @Override
    public Statement apply(Statement base, Description description) {
        Class<?> testClass = description.getTestClass();
        LOG.debug("Applying class rule to class {}", testClass.getName());
        return super.apply(
                base,
                testClass,
                null,
                (next, testContextManager) -> new RunBeforeTestClassCallbacks(next, testContextManager),
                (next, testContextManager) -> new RunAfterTestClassCallbacks(next, testContextManager)
        );
    }
}

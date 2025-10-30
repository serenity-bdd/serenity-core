package net.serenitybdd.junit.spring.integration;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks;
import org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks;

import java.lang.reflect.Method;

/**
 * A {@link org.junit.rules.TestRule} to be used with @{@link org.junit.Rule} to run the @Before and @After modifiers that are part of {@link org.springframework.test.context.junit4.SpringJUnit4ClassRunner}.
 * @author scott.dennison@costcutter.com
 */
public class SpringIntegrationMethodRule extends SpringIntegrationRuleBase implements MethodRule {
    private static final Logger LOG = LoggerFactory.getLogger(SpringIntegrationMethodRule.class);

    /**
     * Wraps {@link RunBeforeTestMethodCallbacks} and {@link RunAfterTestMethodCallbacks} around the provided statement.
     * @param base The base statement
     * @param frameworkMethod Information about the test method being run.
     * @param target The instance of the test class being run.
     * @return The wrapped statement.
     */
    @Override
    public Statement apply(final Statement base, final FrameworkMethod frameworkMethod, final Object target) {
        final Method testMethod = frameworkMethod.getMethod();
        final Class<?> testClass = testMethod.getDeclaringClass();
        LOG.debug("Applying method rule to method {} of class {}",testMethod.getName(),testClass.getName());
        return super.apply(
            base,
            testClass,
            target,
                (next, testContextManager) -> new RunBeforeTestMethodCallbacks(next, target, testMethod, testContextManager),
                (next, testContextManager) -> new RunAfterTestMethodCallbacks(next, target, testMethod, testContextManager)
        );
    }
}

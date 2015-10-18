package net.thucydides.junit.spring;


import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;

@Deprecated
/**
 * Use the Spring test annotations in Thucydides tests.
 *
 * @author johnsmart
 * @deprecated Replaced by {@see net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule},
 * which doesn't use the deprecated {@see org.junit.rules.TestWatchman}, and also works together with
 * {@see net.serenitybdd.junit.spring.integration.SpringIntegrationClassRule}.
 * However, the suggested replacements do not support Spring Framework 2.5,
 * while this class retains support for Spring Framework 2.5.
 */
public class SpringIntegration extends TestWatchman {

    public SpringIntegration() {
    }

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        TestContextManager contextManager = getTestContextManager(method.getMethod().getDeclaringClass());
        try {
            contextManager.prepareTestInstance(target);
        } catch (Exception e) {
            throw new IllegalStateException("Could not instantiate test instance", e);
        }
        Statement statement = super.apply(base, method, target);
        statement = withBefores(method, target, statement, contextManager);
        statement = withAfters(method, target, statement, contextManager);
        return statement;
    }

    protected TestContextManager getTestContextManager(Class<?> clazz) {
        return new TestContextManager(clazz);
    }

    protected Statement withBefores(FrameworkMethod frameworkMethod,
                                    Object testInstance,
                                    Statement statement,
                                    TestContextManager testContextManager) {
        return new RunBeforeTestMethodCallbacks(statement,
                testInstance,
                frameworkMethod.getMethod(),
                testContextManager);
    }

    protected Statement withAfters(FrameworkMethod frameworkMethod,
                                   Object testInstance,
                                   Statement statement,
                                   TestContextManager testContextManager) {
        return new RunAfterTestMethodCallbacks(statement,
                testInstance,
                frameworkMethod.getMethod(),
                testContextManager);
    }

}
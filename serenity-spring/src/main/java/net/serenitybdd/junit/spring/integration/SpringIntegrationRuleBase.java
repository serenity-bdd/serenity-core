package net.serenitybdd.junit.spring.integration;

import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;

/**
 * A base class for {@link SpringIntegrationClassRule} and {@link SpringIntegrationMethodRule} that contains common code.
 * @author scott.dennison@costcutter.com
 */
class SpringIntegrationRuleBase {
	/**
	 * A replacement for Java 8's BiFunction<Statement,TestContextManager,Statement> used by subclasses to provide Statement wrappers.
	 */
	protected static interface StatementWrapper {
		public Statement apply(Statement next, TestContextManager testContextManager);
	}

    private boolean runPrepareTestInstance;
    private TestContextManager testContextManager;

    /**
     * Construct a {@link SpringIntegrationRuleBase} with runPrepareTestInstance set to true, and no pre-constructed {@link TestContextManager}
     */
    protected SpringIntegrationRuleBase() {
        this.setupInner(true,null);
    }

    /**
     * Setup this {@link SpringIntegrationRuleBase} with config. Package private, used by {@link SpringIntegrationSerenityRunner}.
     * @param runPrepareTestInstance Whether or not to run {@link org.springframework.test.context.TestContextManager#prepareTestInstance(java.lang.Object)} in the {@link #apply(org.junit.runners.model.Statement, java.lang.Class, java.lang.Object, java.util.function.BiFunction, java.util.function.BiFunction)) method or not.
     * @param testContextManager An already constructed {@link TestContextManager}, or <tt>null</tt> if one should be created.
     */
    void setup(boolean runPrepareTestInstance, TestContextManager testContextManager) {
        this.setupInner(runPrepareTestInstance,testContextManager);
    }

    private void setupInner(boolean runPrepareTestInstance, TestContextManager testContextManager) {
        this.runPrepareTestInstance = runPrepareTestInstance;
        this.testContextManager = testContextManager;
    }

    /**
     * Creates a test context manager.
     * @param testClass The test class to create the test context manager for.
     * @return The new test context manager.
     */
    public static TestContextManager createTestContextManager(Class<?> testClass) {
        return new TestContextManager(testClass);
    }

    /**
     * Gets the test context manager, creating it if it doesn't exist.
     * @param testClass The test class to create the test context manager for, if it doesn't exist.
     * @return The test context manager.
     */
    public TestContextManager getTestContextManager(Class<?> testClass) {
        if (this.testContextManager == null) {
            this.testContextManager = SpringIntegrationRuleBase.createTestContextManager(testClass);
        }
        return this.testContextManager;
    }

    /**
     * Wraps a before and after statement around the supplied statement, possibly preparing the test instance first.
     * @param next The next statement.
     * @param testClass The class being tested.
     * @param testInstance The instance of the class being tested.
     * @param beforeWrapper The before wrapper.
     * @param afterWrapper The after wrapper.
     * @return The new, wrapped statement.
     */
    protected Statement apply(
        Statement next,
        Class<?> testClass,
        Object testInstance,
        StatementWrapper beforeWrapper,
        StatementWrapper afterWrapper
    ) {
        TestContextManager testContextManagerLocal = this.getTestContextManager(testClass);
        if (this.runPrepareTestInstance && testInstance != null) {
            try {
                testContextManagerLocal.prepareTestInstance(testInstance);
            } catch (Exception e) {
                throw new IllegalStateException("Could not instantiate test instance", e);
            }
        }
        return
            afterWrapper.apply(
                beforeWrapper.apply(
                    next,
                    testContextManagerLocal
                ),
                testContextManagerLocal
            );
    }
}

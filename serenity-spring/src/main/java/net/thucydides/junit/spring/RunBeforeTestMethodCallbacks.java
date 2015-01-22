package net.thucydides.junit.spring;

import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;

import java.lang.reflect.Method;

/**
 * <code>RunBeforeTestMethodCallbacks</code> is a custom JUnit 4.5+
 * {@link org.junit.runners.model.Statement} which allows the <em>Spring TestContext Framework</em> to
 * be plugged into the JUnit execution chain by calling
 * {@link org.springframework.test.context.TestContextManager#beforeTestMethod(Object, java.lang.reflect.Method)
 * beforeTestMethod()} on the supplied {@link org.springframework.test.context.TestContextManager}.
 * (This is a Spring 3.0 class back-ported into Thucydides to ensure compatibliity with Spring 2.5.x).
 *
 * @see #evaluate()
 * @see net.thucydides.junit.spring.RunAfterTestMethodCallbacks
 * @author Sam Brannen
 * @since 3.0
 */
public class RunBeforeTestMethodCallbacks extends Statement {

	private final Statement next;

	private final Object testInstance;

	private final Method testMethod;

	private final TestContextManager testContextManager;


	/**
	 * Constructs a new <code>RunBeforeTestMethodCallbacks</code> statement.
	 *
	 * @param next the next <code>Statement</code> in the execution chain
	 * @param testInstance the current test instance (never <code>null</code>)
	 * @param testMethod the test method which is about to be executed on the
	 * test instance
	 * @param testContextManager the TestContextManager upon which to call
	 * <code>beforeTestMethod()</code>
	 */
	public RunBeforeTestMethodCallbacks(Statement next, Object testInstance, Method testMethod,
			TestContextManager testContextManager) {
		this.next = next;
		this.testInstance = testInstance;
		this.testMethod = testMethod;
		this.testContextManager = testContextManager;
	}

	/**
	 * Calls {@link org.springframework.test.context.TestContextManager#beforeTestMethod(Object, java.lang.reflect.Method)} and
	 * then invokes the next {@link org.junit.runners.model.Statement} in the execution chain (typically
	 * an instance of org.junit.internal.runners.statements.RunBefores).
	 */
	@Override
	public void evaluate() throws Throwable {
		testContextManager.beforeTestMethod(testInstance, testMethod);
		next.evaluate();
	}

}

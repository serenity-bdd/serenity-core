package net.thucydides.junit.spring;

import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;

import java.lang.reflect.Method;

/**
 * <code>RunBeforeTestMethodCallbacks</code> is a custom JUnit 4.5+
 * {@link Statement} which allows the <em>Spring TestContext Framework</em> to
 * be plugged into the JUnit execution chain by calling
 * {@link TestContextManager#beforeTestMethod(Object, Method)
 * beforeTestMethod()} on the supplied {@link TestContextManager}.
 * (This is a Spring 3.0 class back-ported into Thucydides to ensure compatibliity with Spring 2.5.x).
 *
 * @see #evaluate()
 * @see RunAfterTestMethodCallbacks
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
	 * Calls {@link TestContextManager#beforeTestMethod(Object, Method)} and
	 * then invokes the next {@link Statement} in the execution chain (typically
	 * an instance of org.junit.internal.runners.statements.RunBefores).
	 */
	@Override
	public void evaluate() throws Throwable {
		testContextManager.beforeTestMethod(testInstance, testMethod);
		next.evaluate();
	}

}

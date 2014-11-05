package net.thucydides.junit.spring;


import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.TestContextManager;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class WhenRunningTestMethodCallbacks {


    @Mock
	Statement next;

    @Mock
	Object testInstance;

    Method testMethod;

    @Mock
	TestContextManager testContextManager;

    @Mock
    Throwable testException;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_call_before_test_methods() throws Throwable {
        RunBeforeTestMethodCallbacks beforeTestMethodCallbacks
                = new RunBeforeTestMethodCallbacks(next, testInstance, testMethod, testContextManager);

        beforeTestMethodCallbacks.evaluate();

        verify(next).evaluate();
        verify(testContextManager).beforeTestMethod(testInstance, testMethod);

    }

    @Test
    public void should_evaluate_next_statement() throws Throwable {
        RunBeforeTestMethodCallbacks beforeTestMethodCallbacks
                = new RunBeforeTestMethodCallbacks(next, testInstance, testMethod, testContextManager);

        beforeTestMethodCallbacks.evaluate();

        verify(next).evaluate();
    }

    @Test
    public void should_call_after_test_methods() throws Throwable {
        RunAfterTestMethodCallbacks afterTestMethodCallbacks
                = new RunAfterTestMethodCallbacks(next, testInstance, testMethod, testContextManager);

        afterTestMethodCallbacks.evaluate();

        verify(next).evaluate();
        verify(testContextManager).afterTestMethod(testInstance, testMethod, null);

    }

    @Test
    public void should_record_exceptions_thrown_when_the_statement_is_evaluated() throws Throwable {
        RunAfterTestMethodCallbacks afterTestMethodCallbacks
                = new RunAfterTestMethodCallbacks(next, testInstance, testMethod, testContextManager);

        doThrow(testException).when(next).evaluate();

        try {
            afterTestMethodCallbacks.evaluate();
            fail();
        } catch (Throwable e) {
            assertThat(e, sameInstance(testException));
        }

    }

    @Test
    public void should_record_exception_thrown_when_the_callback_method_is_invoked() throws Throwable {
        RunAfterTestMethodCallbacks afterTestMethodCallbacks
                = new RunAfterTestMethodCallbacks(next, testInstance, testMethod, testContextManager);

        Exception someException = new Exception();
        doThrow(someException).when(testContextManager).afterTestMethod(testInstance, testMethod, null);

        try {
            afterTestMethodCallbacks.evaluate();
            fail();
        } catch (Exception e) {
            assertThat(e, sameInstance(someException));
        }

    }

    @Test
    public void should_record_multiple_exceptions_if_thrown() throws Throwable {
        RunAfterTestMethodCallbacks afterTestMethodCallbacks
                = new RunAfterTestMethodCallbacks(next, testInstance, testMethod, testContextManager);

        Exception someException = new Exception();

        doThrow(testException).when(next).evaluate();
        doThrow(someException).when(testContextManager).afterTestMethod(testInstance, testMethod, testException);

        try {
            afterTestMethodCallbacks.evaluate();
            fail();
        } catch (Throwable e) {
            assertThat(e, instanceOf(MultipleFailureException.class));
            MultipleFailureException multipleFailureException = (MultipleFailureException) e;
            assertThat(multipleFailureException.getFailures().size(), is(2));
        }

    }
}

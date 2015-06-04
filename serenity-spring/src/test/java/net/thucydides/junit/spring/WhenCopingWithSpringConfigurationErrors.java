package net.thucydides.junit.spring;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.TestContextManager;

import java.lang.reflect.Method;

import static org.mockito.Mockito.doThrow;

public class WhenCopingWithSpringConfigurationErrors {

    @Mock
    TestContextManager testContextManager;

    @Mock
    Statement statement;

    @Mock
    Object target;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    final class TestableSpringIntegration extends SpringIntegration {
        @Override
        protected TestContextManager getTestContextManager(Class<?> clazz) {
            return testContextManager;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void should_raise_runtime_exception_if_it_cant_instanciate_the_spring_context() throws Exception {
        TestableSpringIntegration testableSpringIntegration = new TestableSpringIntegration();

        Method testMethod = this.getClass().getMethod("should_raise_runtime_exception_if_it_cant_instanciate_the_spring_context");
        FrameworkMethod method = new FrameworkMethod(testMethod);
        doThrow(new Exception()).when(testContextManager).prepareTestInstance(target);

        testableSpringIntegration.apply(statement, method, target);
    }

}
package net.thucydides.junit.internals;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenInvokingMethods {

    static class TestClass {
        public String foo() {
            return "bar";
        }
    }

    static class TestClassWithIllegalAccessException {
        public String foo() throws Exception {
            throw new IllegalAccessException();
        }
    }

    static class TestClassWithInvocationTargetException {
        public String foo() throws Exception {
            throw new InvocationTargetException(new Exception());
        }
    }


    @Test
    public void should_return_the_result_of_the_invoked_method() throws NoSuchMethodException {
        TestClass testClass = new TestClass();
        Method foo = testClass.getClass().getMethod("foo");

        String result = (String) MethodInvoker.on(testClass).run(foo);
        assertThat(result, is("bar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_an_IllegalArgumentError_if_the_method_cannot_be_invoked() throws Exception {
        TestClassWithInvocationTargetException testClass = new TestClassWithInvocationTargetException();
        Method foo = testClass.getClass().getMethod("foo");
        MethodInvoker.on(testClass).run(foo);
    }
}

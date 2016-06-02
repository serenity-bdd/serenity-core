package net.thucydides.core.steps;

import ch.lambdaj.function.convert.Converter;

import java.lang.reflect.Method;
import java.util.Arrays;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.join;

/**
 * Created by john on 7/08/2015.
 */
public class ReadableMethodName {

    public static TestNameBuilder forMethod(final Method method) {
        return new TestNameBuilder(method);
    }

    public static class TestNameBuilder {

        Method method;
        Object[] args = new Object[]{};

        public TestNameBuilder(Method method) {
            this.method = method;
        }

        public TestNameBuilder withArguments(final Object[] args) {
            this.args = Arrays.copyOf(args,args.length);
            return this;
        }

        public String asString() {
            StringBuilder testName = new StringBuilder(method.getName());
            return testName.append(argumentsTo(testName)).toString();
        }

        private String argumentsTo(StringBuilder testName) {
            if (args.length == 0) {
                return "";
            }
            return ": " + join(convert(args, toReadableForm()));
        }

        private Converter<Object, String> toReadableForm() {
            return new Converter<Object, String>() {
                @Override
                public String convert(Object argument) {
                    return StepArgumentWriter.readableFormOf(argument);
                }
            };
        }
    }
}

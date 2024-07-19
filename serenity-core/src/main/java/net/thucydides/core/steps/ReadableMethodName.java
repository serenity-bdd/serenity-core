package net.thucydides.core.steps;

import net.thucydides.model.steps.StepArgumentWriter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            List<String> readableArguments
                    = Arrays.stream(args)
                            .map(StepArgumentWriter::readableFormOf)
                            .collect(Collectors.toList());

            return ": " + String.join(", ", readableArguments);
        }
    }
}

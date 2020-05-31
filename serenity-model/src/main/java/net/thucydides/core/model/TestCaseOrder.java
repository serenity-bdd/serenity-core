package net.thucydides.core.model;

import net.thucydides.core.annotations.ScenarioOrder;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class TestCaseOrder {
    public static Integer definedIn(Class<?> testCase, String name) {
        if (testCase == null) { return 0; }

        return Arrays.stream(testCase.getMethods())
                .filter(method -> method.getName().equals(name))
                .map(TestCaseOrder::readAnnotatedOrderOf)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(0);
    }

    private static Integer readAnnotatedOrderOf(Method method) {
        ScenarioOrder scenarioOrder = method.getAnnotation(ScenarioOrder.class);
        if (scenarioOrder == null) {
            return null;
        }
        return scenarioOrder.value();
    }
}

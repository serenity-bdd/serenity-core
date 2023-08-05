package net.thucydides.core.steps;

import net.serenitybdd.annotations.Step;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Optional;

public class StepName {

    public static Optional<String> fromStepAnnotationIn(final Method testMethod) {
        Step step = testMethod.getAnnotation(Step.class);
        if ((step != null) && (!StringUtils.isEmpty(step.value()))) {
            return Optional.of(step.value());
        }
        return Optional.empty();
    }
}

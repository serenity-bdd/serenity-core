package net.thucydides.core.steps;

import com.google.common.base.Optional;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

public class StepName {

    public static Optional<String> fromStepAnnotationIn(final Method testMethod) {
        Step step = testMethod.getAnnotation(Step.class);

        if ((step != null) && (!StringUtils.isEmpty(step.value()))) {
            return Optional.of(step.value());
        }
        return Optional.absent();
    }
}

package net.serenitybdd.core.lifecycle;

import net.serenitybdd.core.annotations.events.AfterFeature;
import net.serenitybdd.core.annotations.events.BeforeFeature;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenRunningLifecycleEvents {

    static class FeatureLifecycleEnabled {

        boolean beforeFeatureExecuted;
        boolean afterFeatureExecuted;

        @BeforeFeature
        public void beforeFeature() {
            beforeFeatureExecuted = true;
        }

        @AfterFeature
        public void afterFeature() {
            afterFeatureExecuted = true;
        }
    }

    @Test
    public void stepClassesAreRegisteredBeforeTestExecution() {
        FeatureLifecycleEnabled enabled = new FeatureLifecycleEnabled();

        LifecycleRegister.register(enabled);

        LifecycleRegister.invokeMethodsAnnotatedBy(BeforeFeature.class);
        LifecycleRegister.invokeMethodsAnnotatedBy(AfterFeature.class);

        assertThat(enabled.beforeFeatureExecuted).isTrue();
        assertThat(enabled.afterFeatureExecuted).isTrue();
    }
}

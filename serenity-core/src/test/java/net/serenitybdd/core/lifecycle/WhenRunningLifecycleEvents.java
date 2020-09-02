package net.serenitybdd.core.lifecycle;

import net.serenitybdd.core.annotations.events.AfterScenario;
import net.serenitybdd.core.annotations.events.BeforeScenario;
import net.thucydides.core.model.TestOutcome;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenRunningLifecycleEvents {

    static class FeatureLifecycleEnabled {

        boolean beforeFeatureExecuted;
        boolean afterFeatureExecuted;

        @BeforeScenario
        public void beforeScenario() {
            beforeFeatureExecuted = true;
        }

        @AfterScenario
        public void afterScenario() {
            afterFeatureExecuted = true;
        }

        @BeforeScenario
        public void beforeScenarioWithTestOutcome(TestOutcome testOutcome) {
            afterFeatureExecuted = true;
        }
    }

    @Test
    public void stepClassesAreRegisteredBeforeTestExecution() {
        FeatureLifecycleEnabled enabled = new FeatureLifecycleEnabled();

        LifecycleRegister.register(enabled);

        TestOutcome testOutcome = TestOutcome.forTest("beforeScenario",FeatureLifecycleEnabled.class);

        LifecycleRegister.invokeMethodsAnnotatedBy(BeforeScenario.class, testOutcome);
        LifecycleRegister.invokeMethodsAnnotatedBy(AfterScenario.class, testOutcome);

        assertThat(enabled.beforeFeatureExecuted).isTrue();
        assertThat(enabled.afterFeatureExecuted).isTrue();
    }
}

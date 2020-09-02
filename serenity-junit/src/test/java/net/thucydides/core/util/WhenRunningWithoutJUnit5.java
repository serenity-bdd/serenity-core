package net.thucydides.core.util;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class WhenRunningWithoutJUnit5 {

    @Test(expected = ClassNotFoundException.class)
    public void there_are_no_junit5_classes() throws ClassNotFoundException {
        Class.forName("org.junit.jupiter.api.Test");
    }

    @Test
    public void junit_adapter_has_only_junit4_strategy() {
        assertThat(JUnitAdapter.getStrategies()).hasSize(1);
        assertThat(((Object) JUnitAdapter.getStrategies().get(0)).getClass().getCanonicalName()).contains("JUnit4");
    }

    @Test
    public void junit_adapter_will_still_work() {
        assertThat(JUnitAdapter.isATaggableClass(WhenRunningWithoutJUnit5.class)).isTrue();
    }

}

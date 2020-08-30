package net.thucydides.core.steps;

import net.thucydides.core.pages.Pages;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class WhenUsingTheStepFactory {

    @Test
    public void the_step_factory_is_managed_by_the_StepFactory_class() {
        StepFactory factory = StepFactory.getFactory();

        assertThat(factory).isNotNull();
    }

    @Test
    public void the_step_factory_is_unique_within_a_thread() {
        StepFactory factoryA = StepFactory.getFactory();
        StepFactory factoryB = StepFactory.getFactory();

        assertThat(factoryA).isEqualTo(factoryB);
    }

    @Test
    public void the_step_factory_can_be_provided_with_a_PageObject_factory() {
        Pages pages = new Pages();
        StepFactory factoryA = StepFactory.getFactory().usingPages(pages);
        StepFactory factoryB = StepFactory.getFactory();

        assertThat(factoryA).isEqualTo(factoryB);
    }

}

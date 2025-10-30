package net.thucydides.samples;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.steps.ExampleTables;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class SampleDynamicTableScenario {

    @Steps
    DataSteps dataSteps;

    @Test
    public void should_create_a_data_table_dynamically() {

        ExampleTables.useExampleTable().withHeaders("Fruit","Fruit Salad")
                                       .andTitle("Fruit Salad!")
                                       .start();

        dataSteps.can_go_in_a_fruit_salad("apple", true);
        dataSteps.can_go_in_a_fruit_salad("pear", true);
        dataSteps.can_go_in_a_fruit_salad("tomato", false);
    }

    public static class DataSteps {

        @Step(exampleRow = true)
        public void can_go_in_a_fruit_salad(String ingredient, boolean yesNo) {
            check_recipe();
            check_ingredients();
            Assertions.assertThat(true).isEqualTo(!yesNo);
        }

        @Step
        public void check_recipe() {
        }

        @Step
        public void check_ingredients() {
        }
    }
}

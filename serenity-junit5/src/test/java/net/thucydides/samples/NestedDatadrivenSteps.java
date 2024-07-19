package net.thucydides.samples;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;

import java.io.IOException;

import static net.thucydides.core.steps.stepdata.StepData.withTestDataFrom;


public class NestedDatadrivenSteps {

    @Steps
    public SampleScenarioSteps steps;


    @Step
    public void run_data_driven_tests() throws Throwable {
        prepare_test_data();
        check_each_row();
        tidy_up();
    }

    @Step
    private void tidy_up() {
    }

    @Step
    public void check_each_row() throws IOException {
        withTestDataFrom("test-data/simple-data.csv").run(steps).simple_data_driven_test_step();
    }

    @Step
    public void prepare_test_data() {
    }

    @Step
    public void do_something() {
    }

    @Step
    public void do_something_else() {
    }
}

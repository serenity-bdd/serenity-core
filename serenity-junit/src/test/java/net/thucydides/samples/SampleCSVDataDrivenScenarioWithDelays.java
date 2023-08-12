package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.serenitybdd.annotations.Managed;
import net.thucydides.junit.annotations.UseTestDataFrom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityParameterizedRunner.class)
@UseTestDataFrom(value = "test-data/simple-data.csv")
public class SampleCSVDataDrivenScenarioWithDelays extends SampleCSVDataDrivenScenario {
    @Managed
    WebDriver driver;

    @Test
    public void data_driven_test() {
        steps.stepWithParameters(this.getName(), Integer.valueOf(this.getAge()), true);
    }

    @Test
    public void another_data_driven_test() {
        steps.stepWithParameters(this.getName(), Integer.valueOf(this.getAge()), true);
    }
}

package sample.listeners;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.steps.StepListenerAdapter;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class SampleStepListener extends StepListenerAdapter {


    public List<TestOutcome> getTestOutcomes() {
        return null;  
    }

    public WebDriver getDriver() {
        return null;  
    }
}

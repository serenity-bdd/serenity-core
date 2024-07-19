package net.thucydides.core;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.steps.StepListenerAdapter;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class ListenerInWrongPackage extends StepListenerAdapter {

    public List<TestOutcome> getTestOutcomes() {
        return null;  
    }

    public WebDriver getDriver() {
        return null;  
    }
}

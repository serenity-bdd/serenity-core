package net.serenitybdd.core.photography;

import net.thucydides.core.model.TestResult;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_ALWAYS_STORE_HTML;

public class SoundEngineer {

    private final EnvironmentVariables environmentVariables;
    private boolean recordPageSource = true;

    public SoundEngineer(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public SoundEngineer ifRequiredForResult(TestResult result) {
        recordPageSource = (SERENITY_ALWAYS_STORE_HTML.booleanFrom(environmentVariables, false))
                || ((result == TestResult.FAILURE || result == TestResult.ERROR));
        return this;
    }

    public PageSourceRecorder recordPageSourceUsing(WebDriver driver) {
        return (recordPageSource) ? new PageSourceRecorder(driver) : new DisabledPageSourceRecorder(driver);
    }
}

package net.serenitybdd.core.photography;

import net.thucydides.core.model.TestResult;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.core.photography.StoreHTML.ALWAYS;
import static net.serenitybdd.core.photography.StoreHTML.NEVER;
import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_STORE_HTML;

public class SoundEngineer {

    private final EnvironmentVariables environmentVariables;
    private boolean recordPageSource = true;

    public SoundEngineer(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public SoundEngineer ifRequiredForResult(TestResult result) {
        StoreHTML storeHTML = StoreHTML.valueOf(SERENITY_STORE_HTML.optionalFrom(environmentVariables).orElse("FAILURES"));
        if (storeHTML == NEVER) {
            recordPageSource = false;
        } else if (storeHTML == ALWAYS) {
            recordPageSource = true;
        } else {
            recordPageSource = (result == TestResult.FAILURE || result == TestResult.ERROR || result == TestResult.UNDEFINED);
        }
        return this;
    }

    public PageSourceRecorder recordPageSourceUsing(WebDriver driver) {
        return (recordPageSource) ? new PageSourceRecorder(driver) : new DisabledPageSourceRecorder(driver);
    }
}

package net.serenitybdd.core.photography;

import net.thucydides.core.model.TestResult;
import org.openqa.selenium.WebDriver;

public class SoundEngineer {

    private boolean recordPageSource = true;

    public SoundEngineer ifRequiredForResult(TestResult result) {
        recordPageSource = (result == TestResult.FAILURE || result == TestResult.ERROR);
        return this;
    }

    public PageSourceRecorder recordPageSourceUsing(WebDriver driver) {
        return (recordPageSource) ? new PageSourceRecorder(driver) : new DisabledPageSourceRecorder(driver);
    }
}

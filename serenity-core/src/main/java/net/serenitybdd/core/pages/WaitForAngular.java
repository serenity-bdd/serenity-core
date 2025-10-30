package net.serenitybdd.core.pages;

import com.paulhammant.ngwebdriver.NgWebDriver;
import org.openqa.selenium.JavascriptExecutor;

public class WaitForAngular {
    private final JavascriptExecutor driver;

    public WaitForAngular(JavascriptExecutor driver) {
        this.driver = driver;
    }

    public static WaitForAngular withDriver(JavascriptExecutor driver) {
        return new WaitForAngular(driver);
    }

    public void untilAngularRequestsHaveFinished() {
        new NgWebDriver(driver).waitForAngularRequestsToFinish();
    }
}

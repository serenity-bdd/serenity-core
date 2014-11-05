package net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.Alert;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TargetLocatorStub implements WebDriver.TargetLocator {
    
    private WebDriver webDriver;
    
    public TargetLocatorStub(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public WebDriver frame(int index) {
        return webDriver;  
    }

    @Override
    public WebDriver frame(String nameOrId) {
        return webDriver;
    }

    @Override
    public WebDriver frame(WebElement frameElement) {
        return webDriver;
    }

    @Override
    public WebDriver parentFrame() {
        return webDriver;
    }

    @Override
    public WebDriver window(String nameOrHandle) {
        return webDriver;
    }

    @Override
    public WebDriver defaultContent() {
        return webDriver;
    }

    @Override
    public WebElement activeElement() {
        throw new ElementNotVisibleException("No active element found (a previous step has failed)");
    }

    @Override
    public Alert alert() {
        return new AlertStub();
    }
}

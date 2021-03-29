package serenitycore.net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebDriverStub implements WebDriver {
    @Override
    public void get(String s) {
    }

    @Override
    public String getCurrentUrl() {
        return "";
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public List<WebElement> findElements(By by) {
        return new ArrayList<>();
    }

    @Override
    public WebElement findElement(By by) {
        return new WebElementFacadeStub();
    }

    @Override
    public String getPageSource() {
        return "";
    }

    @Override
    public void close() {

    }

    @Override
    public void quit() {

    }

    @Override
    public Set<String> getWindowHandles() {
        return new HashSet<>();
    }

    @Override
    public String getWindowHandle() {
        return "";
    }

    @Override
    public TargetLocator switchTo() {
        return new TargetLocatorStub(this);
    }

    @Override
    public Navigation navigate() {
        return new NavigationStub();
    }

    @Override
    public Options manage() {
        return new ManageStub();
    }


}

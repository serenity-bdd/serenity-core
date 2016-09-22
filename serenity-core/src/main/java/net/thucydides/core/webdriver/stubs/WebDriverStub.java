package net.thucydides.core.webdriver.stubs;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;

public class WebDriverStub implements WebDriver {
    @Override
    public void get(String s) {
    }

    @Override
    public String getCurrentUrl() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getTitle() {
        return StringUtils.EMPTY;
    }

    @Override
    public List<WebElement> findElements(By by) {
        return Lists.newArrayList();
    }

    @Override
    public WebElement findElement(By by) {
        return new WebElementFacadeStub();
    }

    @Override
    public String getPageSource() {
        return StringUtils.EMPTY;
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
        return StringUtils.EMPTY;
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

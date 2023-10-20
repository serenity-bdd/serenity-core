package net.thucydides.core.webdriver.stubs;

import io.appium.java_client.ExecutesMethod;
import io.appium.java_client.HidesKeyboardWithKeyName;
import io.appium.java_client.LocksDevice;
import io.appium.java_client.PushesFiles;
import io.appium.java_client.ios.PerformsTouchID;
import io.appium.java_client.ios.ShakesDevice;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Response;

import java.util.*;

public class IOSWebDriverStub extends WebDriverStub implements HidesKeyboardWithKeyName, ShakesDevice,
        PerformsTouchID, PushesFiles, LocksDevice {

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


    @Override
    public Response execute(String s, Map<String, ?> map) {
        return new Response();
    }

    @Override
    public Response execute(String s) {
        return new Response();
    }

    @Override
    public void hideKeyboard(String keyName) {

    }

    @Override
    public void hideKeyboard() {

    }

    @Override
    public void shake() {

    }

    void nativeWebTap(Boolean enabled) {

    }

    @Override
    public ExecutesMethod assertExtensionExists(String s) {
        return new ExecutesMethodStub();
    }

    @Override
    public ExecutesMethod markExtensionAbsence(String s) {
        return new ExecutesMethodStub();
    }
}

package net.thucydides.core.webdriver.stubs;

import io.appium.java_client.ExecutesMethod;
import io.appium.java_client.LocksDevice;
import io.appium.java_client.PushesFiles;
import io.appium.java_client.android.HasAndroidDeviceDetails;
import io.appium.java_client.android.HasSupportedPerformanceDataType;
import io.appium.java_client.android.StartsActivity;
import io.appium.java_client.android.connection.HasNetworkConnection;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Response;

import java.time.Duration;
import java.util.*;

public class AndroidWebDriverStub extends WebDriverStub implements HasNetworkConnection,
        PushesFiles, StartsActivity,
        HasAndroidDeviceDetails, HasSupportedPerformanceDataType, LocksDevice {

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

    void nativeWebTap(Boolean enabled) {

    }

    void ignoreUnimportantViews(Boolean compress) {
    }

    void configuratorSetWaitForIdleTimeout(Duration timeout) {
    }

    void configuratorSetWaitForSelectorTimeout(Duration timeout) {
    }

    void configuratorSetScrollAcknowledgmentTimeout(Duration timeout) {
    }

    void configuratorSetKeyInjectionDelay(Duration delay) {
    }

    void configuratorSetActionAcknowledgmentTimeout(Duration timeout) {
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

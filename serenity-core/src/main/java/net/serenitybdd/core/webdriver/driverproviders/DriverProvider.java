package net.serenitybdd.core.webdriver.driverproviders;

import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;

public interface DriverProvider {
    WebDriver newInstance() throws MalformedURLException;
}

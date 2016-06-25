package net.thucydides.core.webdriver.strategies;

import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;

public interface DriverBuilder {
    WebDriver newInstance() throws MalformedURLException;
}

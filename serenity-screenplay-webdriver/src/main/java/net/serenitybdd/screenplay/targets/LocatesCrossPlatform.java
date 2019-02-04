package net.serenitybdd.screenplay.targets;

import org.openqa.selenium.By;

/**
 * For the fluent builder when giving unique locators for iOS and Android
 */
public interface LocatesCrossPlatform {
    Target locatedForIOS(By iosLocator);
}

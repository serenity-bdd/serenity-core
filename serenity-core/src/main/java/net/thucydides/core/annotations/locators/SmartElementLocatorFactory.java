package net.thucydides.core.annotations.locators;

import java.lang.reflect.Field;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.thucydides.core.webdriver.MobilePlatform;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public final class SmartElementLocatorFactory implements ElementLocatorFactory {
    private final SearchContext searchContext;
    private int timeoutInSeconds;
    private MobilePlatform platform;

    public SmartElementLocatorFactory(SearchContext searchContext, MobilePlatform platform, int timeoutInSeconds) {
    	this.searchContext = searchContext;
    	this.timeoutInSeconds = timeoutInSeconds;
    	this.platform = platform;
    }

    public ElementLocator createLocator(Field field) {
        // FIXME: Need to pass through the appium platform either here, or in both ElementLocator instances
        return new SmartAjaxElementLocator(searchContext, field, platform, timeoutInSeconds);
    }
}

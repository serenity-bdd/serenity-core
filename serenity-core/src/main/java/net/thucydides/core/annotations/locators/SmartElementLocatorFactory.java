package net.thucydides.core.annotations.locators;

import net.thucydides.core.webdriver.MobilePlatform;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;

public final class SmartElementLocatorFactory implements ElementLocatorFactory {
    private final SearchContext searchContext;
    private MobilePlatform platform;

    public SmartElementLocatorFactory(SearchContext searchContext, MobilePlatform platform) {
        this.searchContext = searchContext;
        this.platform = platform;
    }

    public ElementLocator createLocator(Field field) {
        // FIXME: Need to pass through the appium platform either here, or in both ElementLocator instances
        return new SmartAjaxElementLocator(searchContext, field, platform);
    }
}
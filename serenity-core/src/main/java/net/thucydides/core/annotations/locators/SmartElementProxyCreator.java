package net.thucydides.core.annotations.locators;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.java8.TriConsumer;
import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.ElementLocatorFactorySelector;
import net.thucydides.core.webdriver.ElementProxyCreator;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class SmartElementProxyCreator implements ElementProxyCreator {


    interface ElementInitialiser extends BiConsumer<WebDriver, PageObject>{}
    interface ElementInitialiserWithTimeout extends TriConsumer<WebDriver, PageObject, Integer> {}

    private ElementInitialiser appiumInitialisationStrategy
            = (driver, pageObject) ->  PageFactory.initElements(new AppiumFieldDecorator(driver), this);

    private ElementInitialiser defaultInitialisationStrategy
            = (driver, pageObject) ->  PageFactory.initElements(new SmartFieldDecorator(locatorFactories().getLocatorFor(driver), driver, pageObject), pageObject);

    private ElementInitialiserWithTimeout appiumInitialisationStrategyWithTimeout
            = (driver, pageObject, timeoutInSeconds) ->  PageFactory.initElements(new AppiumFieldDecorator(driver, timeoutInSeconds, TimeUnit.SECONDS), this);

    private ElementInitialiserWithTimeout defaultInitialisationStrategyWithTimeout
            = (driver, pageObject, timeoutInSeconds) ->  {
                                                         ElementLocatorFactory finder = locatorFactories().withTimeout(timeoutInSeconds).getLocatorFor(driver);
                                                         PageFactory.initElements(new SmartFieldDecorator(finder, driver, pageObject), pageObject);
    };


    @Override
    public void proxyElements(PageObject pageObject, WebDriver driver) {
        usingInitialisationStrategyFor(driver).accept(driver, pageObject);
    }

    @Override
    public void proxyElements(PageObject pageObject, WebDriver driver, int timeoutInSeconds) {
        usingInitialisationStrategyWithTimeoutFor(driver).accept(driver, pageObject, timeoutInSeconds);
    }

    private ElementInitialiser usingInitialisationStrategyFor(WebDriver driver) {
        if ((driver instanceof WebDriverFacade) && ((WebDriverFacade) driver).isAProxyFor(AppiumDriver.class)) {
            return appiumInitialisationStrategy;
        } else {
            return defaultInitialisationStrategy;
        }
    }

    private ElementInitialiserWithTimeout usingInitialisationStrategyWithTimeoutFor(WebDriver driver) {
        if ((driver instanceof WebDriverFacade) && ((WebDriverFacade) driver).isAProxyFor(AppiumDriver.class)) {
            return appiumInitialisationStrategyWithTimeout;
        } else {
            return defaultInitialisationStrategyWithTimeout;
        }
    }

    private ElementLocatorFactorySelector locatorFactories() {
        Configuration configuration = ConfiguredEnvironment.getConfiguration();
        return new ElementLocatorFactorySelector(configuration);
    }

}

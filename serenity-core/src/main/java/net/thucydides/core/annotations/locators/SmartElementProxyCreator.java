package net.thucydides.core.annotations.locators;

import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.serenitybdd.core.pages.PageObject;
import net.thucydides.model.webdriver.Configuration;
import net.thucydides.core.webdriver.ElementLocatorFactorySelector;
import net.thucydides.core.webdriver.ElementProxyCreator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public class SmartElementProxyCreator implements ElementProxyCreator {

    @Override
    public void proxyElements(PageObject pageObject, WebDriver driver) {
        PageFactory.initElements(new SmartFieldDecorator(locatorFactories().getLocatorFor(driver), driver, pageObject), pageObject);
    }

    @Override
    public void proxyElements(PageObject pageObject, WebDriver driver, int timeoutInSeconds) {
        ElementLocatorFactory finder = locatorFactories().withTimeout(timeoutInSeconds).getLocatorFor(driver);
        PageFactory.initElements(new SmartFieldDecorator(finder, driver, pageObject), pageObject);
    }

    private ElementLocatorFactorySelector locatorFactories() {
        Configuration configuration = ConfiguredEnvironment.getConfiguration();
        return new ElementLocatorFactorySelector(configuration);
    }

}

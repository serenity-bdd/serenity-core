package net.serenitybdd.core.webdriver.driverproviders.webdrivermanager;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.webdriver.enhancers.WebDriverManagerEnhancer;

public class SampleWebDriverManagerEnhancer implements WebDriverManagerEnhancer {

    public static boolean WAS_ENHANCED = false;

    @Override
    public WebDriverManager apply(WebDriverManager webDriverManager) {
        WAS_ENHANCED = true;
        return webDriverManager;
    }
}

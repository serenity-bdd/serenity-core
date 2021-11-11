package net.serenitybdd.core.webdriver.enhancers;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Implement this class and place the implementation in a package defined in the serenity.extension.packages
 * to customise the WebDriverManager configuration.
 */
public interface WebDriverManagerEnhancer {
    WebDriverManager apply(WebDriverManager webDriverManager);
}

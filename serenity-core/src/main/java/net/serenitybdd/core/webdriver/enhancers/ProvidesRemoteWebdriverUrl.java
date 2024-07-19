package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.model.util.EnvironmentVariables;

import java.util.Optional;

/**
 * Allow a fixture class (i.e. one that implements BeforeAWebdriverScenario) to derive a remote URL to be used
 * from the current environment variables. Useful when connecting to selenium grid services such as LambdaTest and BrowserStack.
 * Note that if webdriver.remote.url is defined in serenity.conf, that value will take precedence over any calculated values.
 */
public interface ProvidesRemoteWebdriverUrl {
    Optional<String> remoteUrlDefinedIn(EnvironmentVariables environmentVariables);
}

package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class AtTheEndOfAWebDriverTest {
    public static void invokeCustomTeardownLogicWithDriver(EnvironmentVariables environmentVariables,
                                                           TestOutcome testOutcome,
                                                           WebDriver driver) {

        List<Class<?>> webdriverTeardown = ClassFinder.loadClasses().thatImplement(AfterAWebdriverScenario.class)
                                                      .fromPackage("net.serenitybdd");

        String extensionPackage = ThucydidesSystemProperty.SERENITY_EXTENSION_PACKAGES.from(environmentVariables);
        if (extensionPackage != null) {
            webdriverTeardown.addAll(ClassFinder.loadClasses().thatImplement(AfterAWebdriverScenario.class)
                    .fromPackage(extensionPackage));
        }

        webdriverTeardown.forEach(
                teardown -> {
                    try {
                        ((AfterAWebdriverScenario) teardown.newInstance()).apply(environmentVariables, testOutcome, driver);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}

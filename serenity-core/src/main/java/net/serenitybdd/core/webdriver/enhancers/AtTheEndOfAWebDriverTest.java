package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AtTheEndOfAWebDriverTest {

    private static List<Class<?>> baseWebdriverTeardownClasses;

    private static List<Class<?>> getBaseWebdriverTeardownClasses() {
        if (baseWebdriverTeardownClasses == null) {
            baseWebdriverTeardownClasses = ClassFinder.loadClasses()
                    .thatImplement(AfterAWebdriverScenario.class)
                    .fromPackage("net.serenitybdd");
        }

        return new ArrayList<>(baseWebdriverTeardownClasses);
    }

    private static List<AfterAWebdriverScenario> afterAWebdriverScenarios = null;

    private static List<AfterAWebdriverScenario> afterAWebdriverScenarios(String extensionPackage) {
        if (afterAWebdriverScenarios == null) {
            List<Class<?>> webdriverTeardown = getBaseWebdriverTeardownClasses();
            if (extensionPackage != null) {
                webdriverTeardown.addAll(ClassFinder.loadClasses().thatImplement(AfterAWebdriverScenario.class)
                        .fromPackage(extensionPackage));
            }

            afterAWebdriverScenarios = webdriverTeardown.stream()
                    .map(AtTheEndOfAWebDriverTest::newTeardown)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        return afterAWebdriverScenarios;
    }

    private static Optional<AfterAWebdriverScenario> newTeardown(Class<?> teardownClass) {
        try {
            return Optional.of((AfterAWebdriverScenario) teardownClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static void invokeCustomTeardownLogicWithDriver(EnvironmentVariables environmentVariables,
                                                           TestOutcome testOutcome,
                                                           WebDriver driver) {

        String extensionPackage = ThucydidesSystemProperty.SERENITY_EXTENSION_PACKAGES.from(environmentVariables);
        afterAWebdriverScenarios(extensionPackage).forEach(
                teardown -> teardown.apply(environmentVariables, testOutcome, driver)
        );
    }
}

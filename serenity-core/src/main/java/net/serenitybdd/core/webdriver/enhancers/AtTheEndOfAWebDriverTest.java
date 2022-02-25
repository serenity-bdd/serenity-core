package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
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

    private static List<AfterAWebdriverScenario> afterAWebdriverScenarios = Collections.synchronizedList(new ArrayList<>());;
    private static AtomicBoolean fixturesLoaded = new AtomicBoolean(false);

    private static List<AfterAWebdriverScenario> afterAWebdriverScenarios(String extensionPackage) {
        if (!fixturesLoaded.get()) {
            synchronized (afterAWebdriverScenarios) {
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
            fixturesLoaded.set(true);
        }
        return afterAWebdriverScenarios;
    }

    private static Optional<AfterAWebdriverScenario> newTeardown(Class<?> teardownClass) {
        try {
            return Optional.of((AfterAWebdriverScenario) teardownClass.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static void invokeCustomTeardownLogicWithDriver(EnvironmentVariables environmentVariables,
                                                           TestOutcome testOutcome,
                                                           WebDriver driver) {

        String extensionPackage = ThucydidesSystemProperty.SERENITY_EXTENSION_PACKAGES.from(environmentVariables);
        afterAWebdriverScenarios(extensionPackage).forEach(
                teardown -> {
                    if (teardown.isActivated(environmentVariables)) {
                        teardown.apply(environmentVariables, testOutcome, driver);
                    }
                }
        );
    }
}

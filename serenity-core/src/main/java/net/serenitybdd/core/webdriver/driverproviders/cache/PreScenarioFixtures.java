package net.serenitybdd.core.webdriver.driverproviders.cache;

import net.serenitybdd.core.webdriver.driverproviders.WebDriverInitialisationException;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.reflection.ClassFinder;
import net.thucydides.model.util.EnvironmentVariables;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class PreScenarioFixtures {

    private static final List<BeforeAWebdriverScenario> CACHED_BEFORE_A_WEBDRIVER_SCENARIOS = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicBoolean beforeScenariosLoaded = new AtomicBoolean(false);

    public static List<BeforeAWebdriverScenario> executeBeforeAWebdriverScenario() {
        if (!beforeScenariosLoaded.get()) {
            synchronized (beforeScenariosLoaded) {
                CACHED_BEFORE_A_WEBDRIVER_SCENARIOS.addAll(loadBeforeScenarios());
                beforeScenariosLoaded.set(true);
            }
        }
        return CACHED_BEFORE_A_WEBDRIVER_SCENARIOS;
    }

    private static List<BeforeAWebdriverScenario> loadBeforeScenarios() {

        List<BeforeAWebdriverScenario> enhancers = new ArrayList<>();
        List<Class<BeforeAWebdriverScenario>> capabilityEnhancers
                = ClassFinder.loadClasses()
                .thatImplement(BeforeAWebdriverScenario.class)
                .fromPackage("net.serenitybdd.plugins")
                .stream().map(extension -> (Class<BeforeAWebdriverScenario>) extension).collect(Collectors.toList());

        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
        capabilityEnhancers.addAll(customEnhancers(environmentVariables));

        capabilityEnhancers.forEach(
                enhancerType -> {
                    try {
                        enhancers.add(enhancerType.getDeclaredConstructor().newInstance());
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                             InvocationTargetException e) {
                        throw new WebDriverInitialisationException("Failed to instantiate custom capability enhancer " + enhancerType.getName(), e);
                    }
                }
        );

        return enhancers;
    }

    private static List<Class<BeforeAWebdriverScenario>> customEnhancers(EnvironmentVariables environmentVariables) {
        List<String> extensionPackages = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getListOfValues(ThucydidesSystemProperty.SERENITY_EXTENSION_PACKAGES);
        return extensionPackages.stream()
                .filter(packageName -> !packageName.isEmpty())
                .flatMap(
                        extensionPackage -> ClassFinder.loadClasses()
                                .thatImplement(BeforeAWebdriverScenario.class)
                                .fromPackage(extensionPackage)
                                .stream().map(extension -> (Class<BeforeAWebdriverScenario>) extension)
                ).collect(Collectors.toList());
    }

}

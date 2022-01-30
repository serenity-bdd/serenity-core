package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddCustomDriverCapabilities {

    private final EnvironmentVariables environmentVariables;
    private SupportedWebDriver driver;
    private TestOutcome testOutcome;

    private AddCustomDriverCapabilities(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public AddCustomDriverCapabilities withTestDetails(SupportedWebDriver driver, TestOutcome testOutcome) {
        this.driver = driver;
        this.testOutcome = testOutcome;
        return this;
    }

    public static AddCustomDriverCapabilities from(EnvironmentVariables environmentVariables) {
        return new AddCustomDriverCapabilities(environmentVariables);
    }

    public MutableCapabilities to(MutableCapabilities capabilities) {

        List<Class<BeforeAWebdriverScenario>> capabilityEnhancers
                = new ArrayList<>(
                ClassFinder.loadClasses()
                        .thatImplement(BeforeAWebdriverScenario.class)
                        .fromPackage("net.serenitybdd")
                        .stream().map(extension -> (Class<BeforeAWebdriverScenario>) extension)
                        .collect(Collectors.toList()));

        capabilityEnhancers.addAll(customEnhancers());

        capabilityEnhancers.forEach(
                enhancerType -> {
                    try {
                        BeforeAWebdriverScenario beforeScenario = enhancerType.getDeclaredConstructor().newInstance();
                        if (beforeScenario.isActivated(environmentVariables)) {
                            beforeScenario.apply(environmentVariables, driver, testOutcome, capabilities);
                        }
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        throw new WebDriverInitialisationException("Failed to instantiate custom capability enhancer " + enhancerType.getName(), e);
                    }
                }
        );
        return capabilities;
    }

    private List<Class<BeforeAWebdriverScenario>> customEnhancers() {
        List<String> extensionPackages = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getListOfValues(ThucydidesSystemProperty.SERENITY_EXTENSION_PACKAGES);
        return extensionPackages.stream().flatMap(
                extensionPackage -> ClassFinder.loadClasses()
                        .thatImplement(BeforeAWebdriverScenario.class)
                        .fromPackage(extensionPackage)
                        .stream().map(extension -> (Class<BeforeAWebdriverScenario>) extension)
        ).collect(Collectors.toList());
    }
}

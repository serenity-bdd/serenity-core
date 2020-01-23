package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public DesiredCapabilities to(DesiredCapabilities capabilities) {

        List<Class<?>> customCapabilityEnhancers
                = ClassFinder.loadClasses()
                             .thatImplement(BeforeAWebdriverScenario.class)
                             .fromPackage("net.serenitybdd");

        String extensionPackageList = ThucydidesSystemProperty.SERENITY_EXTENSION_PACKAGES.from(environmentVariables);
        if (extensionPackageList != null) {
            List<String> extensionPackages = Arrays.asList(extensionPackageList.split(","));
            extensionPackages.forEach(
                    extensionPackage ->
                            customCapabilityEnhancers.addAll(ClassFinder.loadClasses().thatImplement(BeforeAWebdriverScenario.class)
                                    .fromPackage(extensionPackage))
            );
        }

        customCapabilityEnhancers.forEach(
                enhancerType -> {
                    try {
                        ((BeforeAWebdriverScenario)enhancerType.newInstance()).apply(environmentVariables, driver, testOutcome, capabilities);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );

        return capabilities;
    }
}

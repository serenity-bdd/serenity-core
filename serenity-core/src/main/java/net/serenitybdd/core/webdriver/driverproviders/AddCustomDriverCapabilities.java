package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Arrays;
import java.util.List;

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

    public Capabilities to(Capabilities capabilities) {

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities(capabilities);
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

        for(Class enhancerType : customCapabilityEnhancers) {
            try {
                desiredCapabilities = ((BeforeAWebdriverScenario)enhancerType.newInstance()).apply(environmentVariables, driver, testOutcome, desiredCapabilities);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return desiredCapabilities;
    }
}

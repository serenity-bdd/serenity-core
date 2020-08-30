package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.enhancers.CustomChromeOptions;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Arrays;
import java.util.List;

public class ConfigureChromeOptions {

    private final EnvironmentVariables environmentVariables;

    private ConfigureChromeOptions(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static ConfigureChromeOptions from(EnvironmentVariables environmentVariables) {
        return new ConfigureChromeOptions(environmentVariables);
    }

    public ChromeOptions to(ChromeOptions chromeOptions) {

        List<Class<?>> customChromeOptions
                = ClassFinder.loadClasses()
                             .thatImplement(CustomChromeOptions.class)
                             .fromPackage("net.serenitybdd");

        String extensionPackageList = ThucydidesSystemProperty.SERENITY_EXTENSION_PACKAGES.from(environmentVariables);
        if (extensionPackageList != null) {
            List<String> extensionPackages = Arrays.asList(extensionPackageList.split(","));
            extensionPackages.forEach(
                    extensionPackage ->
                            customChromeOptions.addAll(ClassFinder.loadClasses()
                                                                  .thatImplement(CustomChromeOptions.class)
                                                                  .fromPackage(extensionPackage))
            );
        }

        customChromeOptions.forEach(
                enhancerType -> {
                    try {
                        ((CustomChromeOptions)enhancerType.newInstance()).apply(environmentVariables, chromeOptions);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );

        return chromeOptions;
    }
}

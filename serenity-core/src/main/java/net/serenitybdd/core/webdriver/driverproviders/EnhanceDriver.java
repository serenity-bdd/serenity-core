package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.enhancers.CustomDriverEnhancer;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.List;

public class EnhanceDriver {

    private final EnvironmentVariables environmentVariables;

    private EnhanceDriver(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static EnhanceDriver from(EnvironmentVariables environmentVariables) {
        return new EnhanceDriver(environmentVariables);
    }

    public WebDriver to(WebDriver driver) {

        List<Class<?>> customDriverEnhancers
                = ClassFinder.loadClasses()
                             .thatImplement(CustomDriverEnhancer.class)
                             .fromPackage("net.serenitybdd");

        String extensionPackageList = ThucydidesSystemProperty.SERENITY_EXTENSION_PACKAGES.from(environmentVariables);
        if (extensionPackageList != null) {
            List<String> extensionPackages = Arrays.asList(extensionPackageList.split(","));
            extensionPackages.forEach(
                    extensionPackage ->
                            customDriverEnhancers.addAll(ClassFinder.loadClasses()
                                                                  .thatImplement(CustomDriverEnhancer.class)
                                                                  .fromPackage(extensionPackage))
            );
        }

        customDriverEnhancers.forEach(
                enhancerType -> {
                    try {
                        ((CustomDriverEnhancer)enhancerType.newInstance()).apply(environmentVariables, driver);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );

        return driver;
    }
}

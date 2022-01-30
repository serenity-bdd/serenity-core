package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.enhancers.CustomDriverEnhancer;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnhanceDriver {

    private final EnvironmentVariables environmentVariables;

    private EnhanceDriver(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static EnhanceDriver from(EnvironmentVariables environmentVariables) {
        return new EnhanceDriver(environmentVariables);
    }

    public WebDriver to(WebDriver driver) {

        List<Class<CustomDriverEnhancer>> customDriverEnhancers
                = new ArrayList<>(ClassFinder.loadClasses()
                .thatImplement(CustomDriverEnhancer.class)
                .fromPackage("net.serenitybdd")
                .stream().map(extension -> (Class<CustomDriverEnhancer>) extension)
                .collect(Collectors.toList()));

        customDriverEnhancers.addAll(customEnhancers());

        customDriverEnhancers.forEach(
                enhancerType -> {
                    try {
                        (enhancerType.getDeclaredConstructor().newInstance()).apply(environmentVariables, driver);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        throw new WebDriverInitialisationException("Failed to instantiate custom driver enhancer " + enhancerType.getName(), e);
                    }
                }
        );

        return driver;
    }

    private List<Class<CustomDriverEnhancer>> customEnhancers() {
        List<String> extensionPackages = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getListOfValues(ThucydidesSystemProperty.SERENITY_EXTENSION_PACKAGES);
        return extensionPackages.stream().flatMap(
                extensionPackage -> ClassFinder.loadClasses()
                        .thatImplement(CustomDriverEnhancer.class)
                        .fromPackage(extensionPackage)
                        .stream().map(extension -> (Class<CustomDriverEnhancer>) extension)
        ).collect(Collectors.toList());
    }

}

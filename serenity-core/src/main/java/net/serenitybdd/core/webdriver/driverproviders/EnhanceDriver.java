package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.enhancers.CustomDriverEnhancer;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.reflection.ClassFinder;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class EnhanceDriver {

    private final EnvironmentVariables environmentVariables;
    private static final List<Class<CustomDriverEnhancer>> ENHANCERS = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicBoolean enhancersLoaded = new AtomicBoolean(false);

    private EnhanceDriver(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static EnhanceDriver from(EnvironmentVariables environmentVariables) {
        return new EnhanceDriver(environmentVariables);
    }

    public WebDriver to(WebDriver driver) {

        enhancers().forEach(
                enhancerType -> {
                    try {
                        (enhancerType.getDeclaredConstructor().newInstance()).apply(environmentVariables, driver);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                             InvocationTargetException e) {
                        throw new WebDriverInitialisationException("Failed to instantiate custom driver enhancer " + enhancerType.getName(), e);
                    }
                }
        );

        return driver;
    }

    private List<Class<CustomDriverEnhancer>> enhancers() {
        if (!enhancersLoaded.get()) {
            synchronized (enhancersLoaded) {
                ENHANCERS.addAll(
                        ClassFinder.loadClasses()
                                .thatImplement(CustomDriverEnhancer.class)
                                .fromPackage("net.serenitybdd.plugins")
                                .stream().map(extension -> (Class<CustomDriverEnhancer>) extension)
                                .collect(Collectors.toList())
                );
                ENHANCERS.addAll(customEnhancers());
                enhancersLoaded.set(true);
            }
        }
        return ENHANCERS;
    }

    private List<Class<CustomDriverEnhancer>> customEnhancers() {
        List<String> extensionPackages = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getListOfValues(ThucydidesSystemProperty.SERENITY_EXTENSION_PACKAGES);
        return extensionPackages.stream()
                .filter(extensionPackage -> !extensionPackage.isEmpty())
                .flatMap(
                        extensionPackage -> ClassFinder.loadClasses()
                                .thatImplement(CustomDriverEnhancer.class)
                                .fromPackage(extensionPackage)
                                .stream().map(extension -> (Class<CustomDriverEnhancer>) extension)
                ).collect(Collectors.toList());
    }

}

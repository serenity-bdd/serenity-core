package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.enhancers.CustomChromiumOptions;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.chromium.ChromiumOptions;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static net.thucydides.core.ThucydidesSystemProperty.ACCEPT_INSECURE_CERTIFICATES;
import static net.thucydides.core.ThucydidesSystemProperty.HEADLESS_MODE;

public class ConfigureChromiumOptions {

    private final EnvironmentVariables environmentVariables;

    private ConfigureChromiumOptions(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static ConfigureChromiumOptions from(EnvironmentVariables environmentVariables) {
        return new ConfigureChromiumOptions(environmentVariables);
    }

    public <T extends ChromiumOptions<?>> T into(ChromiumOptions<?> chromeOptions) {
        applyCustomChromeOptionsTo(chromeOptions);
        return (T) chromeOptions;
    }

    private void applyCustomChromeOptionsTo(ChromiumOptions<?> chromeOptions) {
        List<Class<?>> customChromeOptions
                = ClassFinder.loadClasses()
                .thatImplement(CustomChromiumOptions.class)
                .fromPackage("net.serenitybdd.plugins");

        String extensionPackageList = ThucydidesSystemProperty.SERENITY_EXTENSION_PACKAGES.from(environmentVariables);
        if (extensionPackageList != null) {
            List<String> extensionPackages = Arrays.asList(extensionPackageList.split(","));
            extensionPackages.forEach(
                    extensionPackage ->
                            customChromeOptions.addAll(ClassFinder.loadClasses()
                                    .thatImplement(CustomChromiumOptions.class)
                                    .fromPackage(extensionPackage))
            );
        }

        customChromeOptions.forEach(
                enhancerType -> {
                    try {
                        ((CustomChromiumOptions)enhancerType.getDeclaredConstructor().newInstance()).apply(environmentVariables, chromeOptions);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
        );

        //
        // Headless mode from command line or headless.mode property
        //
        chromeOptions.setHeadless(HEADLESS_MODE.booleanFrom(environmentVariables,false));
        //
        // Legacy accept.insecure.certificates switch
        //
        chromeOptions.setAcceptInsecureCerts(ACCEPT_INSECURE_CERTIFICATES.booleanFrom(environmentVariables,false));

        //
        // Cater for legacy chrome.switches options
        //
        EnvironmentSwitches.from(environmentVariables).addEnvironmentSwitchesTo(chromeOptions);

    }
}

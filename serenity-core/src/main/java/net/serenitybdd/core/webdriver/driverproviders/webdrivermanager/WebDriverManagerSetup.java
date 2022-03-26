package net.serenitybdd.core.webdriver.driverproviders.webdrivermanager;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.webdriver.enhancers.WebDriverManagerEnhancer;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.Arrays;
import java.util.List;

/**
 * A central place to integrate with WebDriverManager
 */
public class WebDriverManagerSetup {
    private final EnvironmentVariables environmentVariables;

    public WebDriverManagerSetup(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    protected WebDriverManager enhance(WebDriverManager webDriverManager) {

        List<Class<?>> customEnhancers = ClassFinder.loadClasses()
                .thatImplement(WebDriverManagerEnhancer.class)
                .fromPackage("net.serenitybdd");

        String extensionPackageList = ThucydidesSystemProperty.SERENITY_EXTENSION_PACKAGES.from(environmentVariables);
        if (extensionPackageList != null) {
            List<String> extensionPackages = Arrays.asList(extensionPackageList.split(","));
            extensionPackages.forEach(
                    extensionPackage ->
                            customEnhancers.addAll(ClassFinder.loadClasses().thatImplement(WebDriverManagerEnhancer.class)
                                    .fromPackage(extensionPackage))
            );
        }

        customEnhancers.forEach(
                enhancerType -> {
                    try {
                        WebDriverManagerEnhancer enhancer = (WebDriverManagerEnhancer) enhancerType.newInstance();
                        enhancer.apply(webDriverManager);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );
        return webDriverManager;
    }

    public void forFirefox() {
        enhance(WebDriverManager.firefoxdriver()).setup();
    }

    public void forChrome() {
        enhance(WebDriverManager.chromedriver()).setup();
    }

    public void forEdge() {
        enhance(WebDriverManager.edgedriver()).setup();
    }

    public void forIE() {
        enhance(WebDriverManager.iedriver()).setup();
    }

    public void forOpera() {
        enhance(WebDriverManager.operadriver()).setup();
    }

    public void forSafari() {
        enhance(WebDriverManager.safaridriver()).setup();
    }

    public static WebDriverManagerSetup usingEnvironmentVariables(EnvironmentVariables environmentVariables) {
        return new WebDriverManagerSetup(environmentVariables);
    }
}

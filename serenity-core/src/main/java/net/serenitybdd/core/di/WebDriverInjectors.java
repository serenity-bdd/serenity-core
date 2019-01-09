package net.serenitybdd.core.di;

import com.google.inject.*;
import net.thucydides.core.guice.*;
import net.thucydides.core.guice.webdriver.*;

public class WebDriverInjectors {

    public static synchronized Injector getInjector() {
        return Injectors.getInjector(new WebDriverModule());
    }

    public static synchronized Injector getInjector(com.google.inject.Module module) {
        return Injectors.getInjector(module);
    }
}

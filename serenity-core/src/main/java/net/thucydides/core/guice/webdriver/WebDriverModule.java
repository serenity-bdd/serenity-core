package net.thucydides.core.guice.webdriver;

import com.google.inject.Singleton;
import net.serenitybdd.core.annotations.findby.di.ClasspathCustomFindByAnnotationProviderService;
import net.serenitybdd.core.annotations.findby.di.CustomFindByAnnotationProviderService;
import net.thucydides.core.annotations.locators.SmartElementProxyCreator;
import net.thucydides.core.annotations.locators.SmartWidgetProxyCreator;
import net.thucydides.core.configuration.WebDriverConfiguration;
import net.thucydides.core.fixtureservices.ClasspathFixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.guice.ThucydidesModule;
import net.thucydides.core.webdriver.*;

public class WebDriverModule extends ThucydidesModule {
    @Override
    protected void configure() {
        super.configure();
        bind(ElementProxyCreator.class).to(SmartElementProxyCreator.class).in(Singleton.class);
        bind(WidgetProxyCreator.class).to(SmartWidgetProxyCreator.class).in(Singleton.class);
        bind(CloseBrowser.class).to(WebdriverCloseBrowser.class).in(Singleton.class);
        bind(FixtureProviderService.class).to(ClasspathFixtureProviderService.class).in(Singleton.class);
        bind(CustomFindByAnnotationProviderService.class).to(ClasspathCustomFindByAnnotationProviderService.class).in(Singleton.class);
        bind(DriverConfiguration.class).to(WebDriverConfiguration.class).in(Singleton.class);
    }

}

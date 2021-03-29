package serenitycore.net.thucydides.core.guice.webdriver;

import com.google.inject.Singleton;
import serenitycore.net.serenitybdd.core.annotations.findby.di.ClasspathCustomFindByAnnotationProviderService;
import serenitycore.net.serenitybdd.core.annotations.findby.di.CustomFindByAnnotationProviderService;
import serenitycore.net.thucydides.core.annotations.locators.SmartElementProxyCreator;
import serenitycore.net.thucydides.core.annotations.locators.SmartWidgetProxyCreator;
import serenitycore.net.thucydides.core.configuration.WebDriverConfiguration;
import serenitycore.net.thucydides.core.fixtureservices.ClasspathFixtureProviderService;
import serenitycore.net.thucydides.core.fixtureservices.FixtureProviderService;
import serenitymodel.net.thucydides.core.guice.ThucydidesModule;
import serenitycore.net.thucydides.core.webdriver.CloseBrowser;
import serenitycore.net.thucydides.core.webdriver.DriverConfiguration;
import serenitycore.net.thucydides.core.webdriver.ElementProxyCreator;
import serenitycore.net.thucydides.core.webdriver.WebdriverCloseBrowser;
import serenitycore.net.thucydides.core.webdriver.WidgetProxyCreator;

public class WebDriverModule extends ThucydidesModule {
    @Override
    protected void configure() {
        super.configure();
        bind(ElementProxyCreator.class).to(SmartElementProxyCreator.class).in(Singleton.class);
        bind(WidgetProxyCreator.class).to(SmartWidgetProxyCreator.class).in(Singleton.class);
        bind(CloseBrowser.class).to(WebdriverCloseBrowser.class).in(Singleton.class);
        bind(FixtureProviderService.class).to(ClasspathFixtureProviderService.class).in(Singleton.class);
        bind(CustomFindByAnnotationProviderService.class).to(
            ClasspathCustomFindByAnnotationProviderService.class).in(Singleton.class);
        bind(DriverConfiguration.class).to(WebDriverConfiguration.class).in(Singleton.class);
    }

}

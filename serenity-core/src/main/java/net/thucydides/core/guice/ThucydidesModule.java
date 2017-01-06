package net.thucydides.core.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.buildinfo.PropertyBasedDriverCapabilityRecord;
import net.serenitybdd.core.time.InternalSystemClock;
import net.serenitybdd.core.time.SystemClock;
import net.thucydides.core.annotations.locators.SmartElementProxyCreator;
import net.thucydides.core.annotations.locators.SmartWidgetProxyCreator;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.batches.BatchManagerProvider;
import net.thucydides.core.fixtureservices.ClasspathFixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.logging.ThucydidesLogging;
import net.thucydides.core.reports.ExecutorServiceProvider;
import net.thucydides.core.reports.MultithreadExecutorServiceProvider;
import net.thucydides.core.reports.json.JSONConverter;
import net.thucydides.core.reports.json.gson.GsonJSONConverter;
import net.thucydides.core.reports.renderer.Asciidoc;
import net.thucydides.core.reports.renderer.AsciidocMarkupRenderer;
import net.thucydides.core.reports.renderer.MarkupRenderer;
import net.thucydides.core.reports.saucelabs.LinkGenerator;
import net.thucydides.core.reports.saucelabs.SaucelabsLinkGenerator;
import net.thucydides.core.reports.templates.FreeMarkerTemplateManager;
import net.thucydides.core.reports.templates.TemplateManager;
import net.thucydides.core.requirements.ClasspathRequirementsProviderService;
import net.thucydides.core.requirements.RequirementsProviderService;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.MultiSourceRequirementsService;
import net.thucydides.core.statistics.AtomicTestCount;
import net.thucydides.core.statistics.TestCount;
import net.thucydides.core.statistics.service.ClasspathTagProviderService;
import net.thucydides.core.statistics.service.TagProviderService;
import net.thucydides.core.steps.ConsoleLoggingListener;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.steps.di.ClasspathDependencyInjectorService;
import net.thucydides.core.steps.di.DependencyInjectorService;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import net.thucydides.core.webdriver.*;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;

public class ThucydidesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SystemClock.class).to(InternalSystemClock.class).in(Singleton.class);
        bind(TemplateManager.class).to(FreeMarkerTemplateManager.class).in(Singleton.class);
        bind(Configuration.class).to(SystemPropertiesConfiguration.class).in(Singleton.class);
        bind(IssueTracking.class).to(SystemPropertiesIssueTracking.class).in(Singleton.class);
        //bind(WebdriverManager.class).to(SerenityWebdriverManager.class).in(Singleton.class);
        bind(BatchManager.class).toProvider(BatchManagerProvider.class).in(Singleton.class);
        bind(LinkGenerator.class).to(SaucelabsLinkGenerator.class).in(Singleton.class);
//        bind(ScreenshotProcessor.class).to(SingleThreadScreenshotProcessor.class).in(Singleton.class);
        bind(JSONConverter.class).to(GsonJSONConverter.class).in(Singleton.class);

        bind(TagProviderService.class).to(ClasspathTagProviderService.class).in(Singleton.class);
        bind(RequirementsProviderService.class).to(ClasspathRequirementsProviderService.class).in(Singleton.class);
        bind(RequirementsService.class).to(MultiSourceRequirementsService.class).in(Singleton.class);
        bind(DependencyInjectorService.class).to(ClasspathDependencyInjectorService.class).in(Singleton.class);
        bind(FixtureProviderService.class).to(ClasspathFixtureProviderService.class).in(Singleton.class);

        bind(StepListener.class).annotatedWith(ThucydidesLogging.class).to(ConsoleLoggingListener.class).in(Singleton.class);
        bind(ElementProxyCreator.class).to(SmartElementProxyCreator.class).in(Singleton.class);
        bind(WidgetProxyCreator.class).to(SmartWidgetProxyCreator.class).in(Singleton.class);

        bind(TestCount.class).to(AtomicTestCount.class).in(Singleton.class);

        bind(MarkupRenderer.class).annotatedWith(Asciidoc.class).to(AsciidocMarkupRenderer.class).in(Singleton.class);
        bind(DriverCapabilityRecord.class).to(PropertyBasedDriverCapabilityRecord.class);
        bind(ExecutorServiceProvider.class).to(MultithreadExecutorServiceProvider.class).in(Singleton.class);
        bind(CloseBrowser.class).to(WebdriverCloseBrowser.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public EnvironmentVariables provideEnvironmentVariables() {
        return SystemEnvironmentVariables.createEnvironmentVariables();
    }
}

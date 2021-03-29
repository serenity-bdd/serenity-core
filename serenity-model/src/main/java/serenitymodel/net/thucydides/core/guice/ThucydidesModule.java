package serenitymodel.net.thucydides.core.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import serenitymodel.net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import serenitymodel.net.serenitybdd.core.buildinfo.PropertyBasedDriverCapabilityRecord;
import serenitymodel.net.serenitybdd.core.history.FileSystemTestOutcomeSummaryRecorder;
import serenitymodel.net.serenitybdd.core.history.HistoricalFlagProvider;
import serenitymodel.net.serenitybdd.core.history.TestOutcomeSummaryRecorder;
import serenitymodel.net.serenitybdd.core.time.InternalSystemClock;
import serenitymodel.net.serenitybdd.core.time.SystemClock;
import serenitymodel.net.thucydides.core.batches.BatchManager;
import serenitymodel.net.thucydides.core.batches.BatchManagerProvider;
import serenitymodel.net.thucydides.core.configuration.SystemPropertiesConfiguration;
import serenitymodel.net.thucydides.core.issues.IssueTracking;
import serenitymodel.net.thucydides.core.issues.SystemPropertiesIssueTracking;
import serenitymodel.net.thucydides.core.logging.ConsoleLoggingListener;
import serenitymodel.net.thucydides.core.logging.ThucydidesLogging;
import serenitymodel.net.thucydides.core.model.flags.FlagProvider;
import serenitymodel.net.thucydides.core.reports.json.JSONConverter;
import serenitymodel.net.thucydides.core.reports.json.gson.GsonJSONConverter;
import serenitymodel.net.thucydides.core.reports.remoteTesting.LinkGenerator;
import serenitymodel.net.thucydides.core.reports.remoteTesting.RemoteTestingLinkManager;
import serenitymodel.net.thucydides.core.reports.renderer.Asciidoc;
import serenitymodel.net.thucydides.core.reports.renderer.AsciidocMarkupRenderer;
import serenitymodel.net.thucydides.core.reports.renderer.MarkupRenderer;
import serenitymodel.net.thucydides.core.reports.templates.FreeMarkerTemplateManager;
import serenitymodel.net.thucydides.core.reports.templates.TemplateManager;
import serenitymodel.net.thucydides.core.requirements.ClasspathRequirementsProviderService;
import serenitymodel.net.thucydides.core.requirements.MultiSourceRequirementsService;
import serenitymodel.net.thucydides.core.requirements.RequirementsProviderService;
import serenitymodel.net.thucydides.core.requirements.RequirementsService;
import serenitymodel.net.thucydides.core.statistics.AtomicTestCount;
import serenitymodel.net.thucydides.core.statistics.TestCount;
import serenitymodel.net.thucydides.core.statistics.service.ClasspathTagProviderService;
import serenitymodel.net.thucydides.core.statistics.service.TagProviderService;
import serenitymodel.net.thucydides.core.steps.StepListener;
import serenitymodel.net.thucydides.core.steps.di.ClasspathDependencyInjectorService;
import serenitymodel.net.thucydides.core.steps.di.DependencyInjectorService;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import serenitymodel.net.thucydides.core.util.SystemEnvironmentVariables;
import serenitymodel.net.thucydides.core.webdriver.Configuration;

public class ThucydidesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SystemClock.class).to(InternalSystemClock.class).in(Singleton.class);
        bind(TemplateManager.class).to(FreeMarkerTemplateManager.class).in(Singleton.class);
        bind(Configuration.class).to(SystemPropertiesConfiguration.class).in(Singleton.class);
        bind(IssueTracking.class).to(SystemPropertiesIssueTracking.class).in(Singleton.class);
        bind(BatchManager.class).toProvider(BatchManagerProvider.class).in(Singleton.class);
        bind(LinkGenerator.class).to(RemoteTestingLinkManager.class).in(Singleton.class);
        bind(JSONConverter.class).to(GsonJSONConverter.class).in(Singleton.class);

        bind(TagProviderService.class).to(ClasspathTagProviderService.class).in(Singleton.class);
        bind(RequirementsProviderService.class).to(ClasspathRequirementsProviderService.class).in(Singleton.class);
        bind(RequirementsService.class).to(MultiSourceRequirementsService.class).in(Singleton.class);
        bind(DependencyInjectorService.class).to(ClasspathDependencyInjectorService.class).in(Singleton.class);

        bind(StepListener.class).annotatedWith(ThucydidesLogging.class).to(ConsoleLoggingListener.class).in(Singleton.class);

        bind(DriverCapabilityRecord.class).to(PropertyBasedDriverCapabilityRecord.class);

        bind(TestCount.class).to(AtomicTestCount.class).in(Singleton.class);

        bind(MarkupRenderer.class).annotatedWith(Asciidoc.class).to(AsciidocMarkupRenderer.class).in(Singleton.class);

        bind(FlagProvider.class).to(HistoricalFlagProvider.class).in(Singleton.class);
        bind(TestOutcomeSummaryRecorder.class).to(FileSystemTestOutcomeSummaryRecorder.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public EnvironmentVariables provideEnvironmentVariables() {
        return SystemEnvironmentVariables.createEnvironmentVariables();
    }
}

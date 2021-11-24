package net.thucydides.core.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.buildinfo.PropertyBasedDriverCapabilityRecord;
import net.serenitybdd.core.history.FileSystemTestOutcomeSummaryRecorder;
import net.serenitybdd.core.history.HistoricalFlagProvider;
import net.serenitybdd.core.history.TestOutcomeSummaryRecorder;
import net.serenitybdd.core.time.InternalSystemClock;
import net.serenitybdd.core.time.SystemClock;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.batches.BatchManagerProvider;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.logging.ConsoleLoggingListener;
import net.thucydides.core.logging.ThucydidesLogging;
import net.thucydides.core.model.flags.FlagProvider;
import net.thucydides.core.reports.json.JSONConverter;
import net.thucydides.core.reports.json.gson.GsonJSONConverter;
import net.thucydides.core.reports.remoteTesting.LinkGenerator;
import net.thucydides.core.reports.remoteTesting.RemoteTestingLinkManager;
import net.thucydides.core.reports.templates.FreeMarkerTemplateManager;
import net.thucydides.core.reports.templates.TemplateManager;
import net.thucydides.core.requirements.ClasspathRequirementsProviderService;
import net.thucydides.core.requirements.MultiSourceRequirementsService;
import net.thucydides.core.requirements.RequirementsProviderService;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.statistics.AtomicTestCount;
import net.thucydides.core.statistics.TestCount;
import net.thucydides.core.statistics.service.ClasspathTagProviderService;
import net.thucydides.core.statistics.service.TagProviderService;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.steps.di.ClasspathDependencyInjectorService;
import net.thucydides.core.steps.di.DependencyInjectorService;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;

public class ThucydidesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SystemClock.class).to(InternalSystemClock.class).in(Singleton.class);
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

        bind(FlagProvider.class).to(HistoricalFlagProvider.class).in(Singleton.class);
        bind(TestOutcomeSummaryRecorder.class).to(FileSystemTestOutcomeSummaryRecorder.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public EnvironmentVariables provideEnvironmentVariables() {
        return SystemEnvironmentVariables.createEnvironmentVariables();
    }
}

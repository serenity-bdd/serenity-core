package net.serenitybdd.model.di;

import net.serenitybdd.model.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.model.buildinfo.PropertyBasedDriverCapabilityRecord;
import net.serenitybdd.model.history.FileSystemTestOutcomeSummaryRecorder;
import net.serenitybdd.model.history.HistoricalFlagProvider;
import net.serenitybdd.model.history.TestOutcomeSummaryRecorder;
import net.serenitybdd.model.time.InternalSystemClock;
import net.serenitybdd.model.time.SystemClock;
import net.thucydides.model.configuration.SystemPropertiesConfiguration;
import net.thucydides.model.domain.flags.FlagProvider;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.issues.SystemPropertiesIssueTracking;
import net.thucydides.model.reports.json.JSONConverter;
import net.thucydides.model.reports.json.gson.GsonJSONConverter;
import net.thucydides.model.reports.remoteTesting.LinkGenerator;
import net.thucydides.model.reports.remoteTesting.RemoteTestingLinkManager;
import net.thucydides.model.reports.templates.TemplateManager;
import net.thucydides.model.requirements.ClasspathRequirementsProviderService;
import net.thucydides.model.requirements.MultiSourceRequirementsService;
import net.thucydides.model.requirements.RequirementsProviderService;
import net.thucydides.model.requirements.RequirementsService;
import net.thucydides.model.statistics.service.ClasspathTagProviderService;
import net.thucydides.model.statistics.service.TagProviderService;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.webdriver.Configuration;

public class ModelInfrastructure {

    private static final TagProviderService tagProviderService = new ClasspathTagProviderService();

    private static final RequirementsProviderService requirementsProviderService = new ClasspathRequirementsProviderService(tagProviderService);

    private static final RequirementsService requirementsService = new MultiSourceRequirementsService();

    private static final IssueTracking issueTracking = new SystemPropertiesIssueTracking();

    private static final DriverCapabilityRecord driverCapabilityRecord
            = new PropertyBasedDriverCapabilityRecord(getConfiguration());

    private static final LinkGenerator linkGenerator = new RemoteTestingLinkManager(getEnvironmentVariables());

    private static final TestOutcomeSummaryRecorder testOutcomeSummaryRecorder = new FileSystemTestOutcomeSummaryRecorder(getEnvironmentVariables());

    private static final FlagProvider flagProvider
            = new HistoricalFlagProvider(getEnvironmentVariables(), testOutcomeSummaryRecorder);

    private static final JSONConverter jsonConverter = new GsonJSONConverter(getEnvironmentVariables());

    private static final SystemClock clock = new InternalSystemClock();

    private static final TemplateManager templateManager = new TemplateManager();

    private static Configuration configuration;

    public static RequirementsService getRequirementsService() {
        return requirementsService;
    }

    public static RequirementsProviderService getRequirementsProviderService() {
        return requirementsProviderService;
    }

    public static TagProviderService getTagProviderService() {
        return tagProviderService;
    }

    public static IssueTracking getIssueTracking() {
        return issueTracking;
    }

    public static DriverCapabilityRecord getDriverCapabilityRecord() {
        return driverCapabilityRecord;
    }

    public static EnvironmentVariables getEnvironmentVariables() {
        return SystemEnvironmentVariables.currentEnvironmentVariables();
    }

    public static Configuration getConfiguration() {
        if (configuration == null) {
            synchronized (ModelInfrastructure.class) {
                configuration = new SystemPropertiesConfiguration(getEnvironmentVariables());
            }
        }
        return configuration;
    }

    public static FlagProvider getFlagProvider() {
        return flagProvider;
    }

    public static LinkGenerator getLinkGenerator() {
        return linkGenerator;
    }

    public static JSONConverter getJsonConverter() {
        return jsonConverter;
    }

    public static SystemClock getClock() {
        return clock;
    }

    public static TemplateManager getTemplateManager() {
        return templateManager;
    }
}

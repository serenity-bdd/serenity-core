package net.serenitybdd.core.di;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.buildinfo.PropertyBasedDriverCapabilityRecord;
import net.serenitybdd.core.history.FileSystemTestOutcomeSummaryRecorder;
import net.serenitybdd.core.history.HistoricalFlagProvider;
import net.serenitybdd.core.history.TestOutcomeSummaryRecorder;
import net.serenitybdd.core.time.InternalSystemClock;
import net.serenitybdd.core.time.SystemClock;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.model.flags.FlagProvider;
import net.thucydides.core.reports.json.JSONConverter;
import net.thucydides.core.reports.json.gson.GsonJSONConverter;
import net.thucydides.core.reports.remoteTesting.LinkGenerator;
import net.thucydides.core.reports.remoteTesting.RemoteTestingLinkManager;
import net.thucydides.core.reports.templates.TemplateManager;
import net.thucydides.core.requirements.ClasspathRequirementsProviderService;
import net.thucydides.core.requirements.MultiSourceRequirementsService;
import net.thucydides.core.requirements.RequirementsProviderService;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.statistics.service.ClasspathTagProviderService;
import net.thucydides.core.statistics.service.TagProviderService;
import net.thucydides.core.util.EnvironmentVariables;

public class ModelInfrastructure {

    private static final TagProviderService tagProviderService = new ClasspathTagProviderService();

    private static final RequirementsProviderService requirementsProviderService = new ClasspathRequirementsProviderService(tagProviderService);

    private static final RequirementsService requirementsService = new MultiSourceRequirementsService();

    private static IssueTracking issueTracking = new SystemPropertiesIssueTracking();

    private static DriverCapabilityRecord driverCapabilityRecord
            = new PropertyBasedDriverCapabilityRecord(getConfiguration());

    private static LinkGenerator linkGenerator = new RemoteTestingLinkManager(getEnvironmentVariables());

    private static TestOutcomeSummaryRecorder testOutcomeSummaryRecorder = new FileSystemTestOutcomeSummaryRecorder(getEnvironmentVariables());

    private static FlagProvider flagProvider
            = new HistoricalFlagProvider(getEnvironmentVariables(), testOutcomeSummaryRecorder);

    private static JSONConverter jsonConverter = new GsonJSONConverter(getEnvironmentVariables());

    private static final SystemClock clock = new InternalSystemClock();

    private static final TemplateManager templateManager = new TemplateManager();

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

    public static SystemPropertiesConfiguration getConfiguration() {
        return new SystemPropertiesConfiguration(getEnvironmentVariables());
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

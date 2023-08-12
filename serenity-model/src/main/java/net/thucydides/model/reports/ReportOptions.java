package net.thucydides.model.reports;

import com.google.common.base.Splitter;
import net.serenitybdd.model.di.ModelInfrastructure;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.requirements.RequirementsService;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.List;

import static net.thucydides.model.ThucydidesSystemProperty.*;

/**
 * Encapsulates user-specified formatting options for the generated reports.
 */
public class ReportOptions {

    final private boolean showStepDetails;
    final private boolean showManualTests;
    final private boolean showReleases;
    final private boolean showProgress;
    final private boolean showHistory;
    final private boolean showTagMenus;
    final private boolean showRelatedTags;
    final private String projectName;
    final private String projectSubTitle;
    final private RequirementsService requirementsService;
    final private boolean displayPiechart;
    final private List<String> firstClassTagTypes;

//    private static final Map<EnvironmentVariables, ReportOptions> REPORT_OPTIONS = new HashMap<>();

    public static ReportOptions forEnvironment(EnvironmentVariables environmentVariables) {
        return new ReportOptions(SystemEnvironmentVariables.currentEnvironmentVariables());
//        if (REPORT_OPTIONS.get(environmentVariables) == null) {
//            REPORT_OPTIONS.put(environmentVariables, new ReportOptions(environmentVariables));
//        }
//        return REPORT_OPTIONS.get(environmentVariables);
    }
    public ReportOptions(EnvironmentVariables environmentVariables) {
        this(environmentVariables, ModelInfrastructure.getRequirementsService());
    }

    public ReportOptions(EnvironmentVariables environmentVariables, RequirementsService requirementsService) {
        showStepDetails = Boolean.parseBoolean(SERENITY_REPORTS_SHOW_STEP_DETAILS.from(environmentVariables, "false"));
        showManualTests = Boolean.parseBoolean(SERENITY_REPORT_SHOW_MANUAL_TESTS.from(environmentVariables, "true"));
        showReleases = Boolean.parseBoolean(SERENITY_REPORT_SHOW_RELEASES.from(environmentVariables, "false"));
        showProgress = Boolean.parseBoolean(SERENITY_REPORT_SHOW_PROGRESS.from(environmentVariables, "false"));
        showHistory = Boolean.parseBoolean(SERENITY_REPORT_SHOW_HISTORY.from(environmentVariables, "false"));
        showTagMenus = Boolean.parseBoolean(SERENITY_REPORT_SHOW_TAG_MENUS.from(environmentVariables, "false"));
        showRelatedTags = Boolean.parseBoolean(SHOW_RELATED_TAGS.from(environmentVariables, "true"));
        displayPiechart = Boolean.parseBoolean(SHOW_PIE_CHARTS.from(environmentVariables, "true"));
        projectName = SERENITY_PROJECT_NAME.from(environmentVariables,"Serenity BDD Report");
        projectSubTitle = REPORT_SUBTITLE.from(environmentVariables,"");
        this.requirementsService = requirementsService;
        firstClassTagTypes = Splitter.on(",").omitEmptyStrings().splitToList(SERENITY_REPORT_TAG_MENUS.from(environmentVariables,""));
    }

    public List<String> getFirstClassTagTypes() {
        return firstClassTagTypes;
    }

    public boolean isShowStepDetails() {
        return showStepDetails;
    }

    public boolean isShowManualTests() {
        return showManualTests;
    }

    public boolean isShowReleases() {
        return showReleases && (!requirementsService.getReleasesFromRequirements().isEmpty());
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public boolean isShowHistory() {
        return showHistory;
    }

    public boolean isShowTagMenus() {
        return showTagMenus;
    }

    public boolean isShowRelatedTags() {
        return showRelatedTags;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectSubTitle() {
        return projectSubTitle;
    }

    public boolean isDisplayPiechart() {
        return displayPiechart;
    }
}

package net.thucydides.core.reports;

import com.google.common.base.Splitter;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.thucydides.core.ThucydidesSystemProperty.*;

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

    private static final Map<EnvironmentVariables, ReportOptions> REPORT_OPTIONS = new HashMap<>();

    public static ReportOptions forEnvironment(EnvironmentVariables environmentVariables) {
        if (REPORT_OPTIONS.get(environmentVariables) == null) {
            REPORT_OPTIONS.put(environmentVariables, new ReportOptions(environmentVariables));
        }
        return REPORT_OPTIONS.get(environmentVariables);
    }
    public ReportOptions(EnvironmentVariables environmentVariables) {
        this(environmentVariables, Injectors.getInjector().getInstance(RequirementsService.class));
    }

    public ReportOptions(EnvironmentVariables environmentVariables, RequirementsService requirementsService) {
        showStepDetails = Boolean.valueOf(THUCYDIDES_REPORTS_SHOW_STEP_DETAILS.from(environmentVariables, "false"));
        showManualTests = Boolean.valueOf(THUCYDIDES_REPORT_SHOW_MANUAL_TESTS.from(environmentVariables, "true"));
        showReleases = Boolean.valueOf(THUCYDIDES_REPORT_SHOW_RELEASES.from(environmentVariables, "false"));
        showProgress = Boolean.valueOf(THUCYDIDES_REPORT_SHOW_PROGRESS.from(environmentVariables, "false"));
        showHistory = Boolean.valueOf(THUCYDIDES_REPORT_SHOW_HISTORY.from(environmentVariables, "false"));
        showTagMenus = Boolean.valueOf(THUCYDIDES_REPORT_SHOW_TAG_MENUS.from(environmentVariables, "false"));
        showRelatedTags = Boolean.valueOf(SHOW_RELATED_TAGS.from(environmentVariables, "true"));
        displayPiechart = Boolean.valueOf(SHOW_PIE_CHARTS.from(environmentVariables, "true"));
        projectName = SERENITY_PROJECT_NAME.from(environmentVariables,"");
        projectSubTitle = REPORT_SUBTITLE.from(environmentVariables,"");
        this.requirementsService = requirementsService;
        firstClassTagTypes = Splitter.on(",").omitEmptyStrings().splitToList(THUCYDIDES_REPORT_TAG_MENUS.from(environmentVariables,""));
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

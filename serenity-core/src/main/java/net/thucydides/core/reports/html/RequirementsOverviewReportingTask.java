package net.thucydides.core.reports.html;

import com.google.common.base.Objects;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.ReportOptions;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.tags.BreadcrumbTagFilter;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.core.environment.ConfiguredEnvironment.getEnvironmentVariables;
import static net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;

class RequirementsOverviewReportingTask extends BaseReportingTask implements ReportingTask {

    private static final String DEFAULT_REQUIREMENTS_REPORT = "freemarker/requirements.ftl";
    private static final String REPORT_NAME = "capabilities.html";

    protected static final Logger LOGGER = LoggerFactory.getLogger(RequirementsOverviewReportingTask.class);

    private final ReportNameProvider reportNameProvider;
    private final RequirementsOutcomes requirementsOutcomes;
    private final RequirementsService requirementsService;
    private final TestOutcomes testOutcomes;
    private final String relativeLink;
    private final String reportName;

    public RequirementsOverviewReportingTask(FreemarkerContext freemarker,
                                             EnvironmentVariables environmentVariables,
                                             File outputDirectory,
                                             ReportNameProvider reportNameProvider,
                                             RequirementsService requirementsService,
                                             RequirementsOutcomes requirementsOutcomes,
                                             String relativeLink,
                                             TestOutcomes testOutcomes) {
        super(freemarker, environmentVariables, outputDirectory);
        this.reportNameProvider = reportNameProvider;
        this.requirementsOutcomes = requirementsOutcomes;
        this.requirementsService = requirementsService;
        this.testOutcomes = testOutcomes;
        this.relativeLink = relativeLink;
        this.reportName = REPORT_NAME;
    }

    public RequirementsOverviewReportingTask(FreemarkerContext freemarker,
                                             EnvironmentVariables environmentVariables,
                                             File outputDirectory,
                                             ReportNameProvider reportNameProvider,
                                             RequirementsService requirementsService,
                                             RequirementsOutcomes requirementsOutcomes,
                                             String relativeLink,
                                             TestOutcomes testOutcomes,
                                             String reportName) {
        super(freemarker, environmentVariables, outputDirectory);
        this.reportNameProvider = reportNameProvider;
        this.requirementsOutcomes = requirementsOutcomes;
        this.requirementsService = requirementsService;
        this.testOutcomes = testOutcomes;
        this.relativeLink = relativeLink;
        this.reportName = reportName;
    }

    @Override
    public void generateReports() throws IOException {

        Stopwatch stopwatch = Stopwatch.started();

        Map<String, Object> context = freemarker.getBuildContext(requirementsOutcomes.getTestOutcomes(), reportNameProvider, true);

        context.put("requirements", requirementsOutcomes.withoutUnrelatedRequirements());
        context.put("requirementTypes", requirementsService.getRequirementTypes());
        context.put("testOutcomes", requirementsOutcomes.getTestOutcomes());
        context.put("allTestOutcomes", testOutcomes);
        context.put("timestamp", TestOutcomeTimestamp.from(testOutcomes));
        context.put("reportName", new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirementsService));
        context.put("absoluteReportName", new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirementsService));
        context.put("reportOptions", new ReportOptions(getEnvironmentVariables()));
        context.put("relativeLink", relativeLink);

        addBreadcrumbs(requirementsOutcomes, context);

        generateReportPage(context, DEFAULT_REQUIREMENTS_REPORT, reportName);
        LOGGER.trace("Requirements report generated: {} in {} ms", reportName, stopwatch.stop());

    }

    private void addBreadcrumbs(RequirementsOutcomes requirementsOutcomes, Map<String, Object> context) {
        List<TestTag> breadcrumbs = new BreadcrumbTagFilter().getRequirementBreadcrumbsFrom(requirementsOutcomes);
        context.put("breadcrumbs", breadcrumbs);
    }

    @Override
    public String toString() {
        return "Requirements report " + reportName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequirementsOverviewReportingTask that = (RequirementsOverviewReportingTask) o;
        return Objects.equal(reportName, that.reportName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reportName);
    }
}
package net.thucydides.core.reports.html;

import com.google.common.base.Preconditions;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.NumericalFormatter;
import net.thucydides.core.reports.ReportOptions;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.util.Inflector;
import net.thucydides.core.util.VersionProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HtmlRequirementsReporter extends HtmlReporter {

    private static final String DEFAULT_REQUIREMENTS_REPORT = "freemarker/requirements.ftl";
    private static final String REPORT_NAME = "capabilities.html";

    private final IssueTracking issueTracking;
    private final RequirementsService requirementsService;
    private final String relativeLink;

    public HtmlRequirementsReporter() {
        this("", Injectors.getInjector().getInstance(IssueTracking.class),
            Injectors.getInjector().getInstance(RequirementsService.class));
    }

    public HtmlRequirementsReporter(final String relativeLink) {
        this(relativeLink, Injectors.getInjector().getInstance(IssueTracking.class), Injectors.getInjector().getInstance(RequirementsService.class));
    }

    public HtmlRequirementsReporter(final String relativeLink, final IssueTracking issueTracking,
                                    final RequirementsService requirementsService) {
        this.relativeLink = relativeLink;
        this.issueTracking = issueTracking;
        this.requirementsService = requirementsService;
    }

    public File generateReportFor(final RequirementsOutcomes requirementsOutcomes) throws IOException {
        return generateReportFor(requirementsOutcomes, requirementsOutcomes.getTestOutcomes(), REPORT_NAME);

    }

    public File generateReportFor(final RequirementsOutcomes requirementsOutcomes,
                                  final TestOutcomes testOutcomes,
                                  final String filename) throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        Map<String,Object> context = new HashMap<String,Object>();

        context.put("requirements", requirementsOutcomes);
        context.put("requirementTypes", requirementsService.getRequirementTypes());
        context.put("testOutcomes", requirementsOutcomes.getTestOutcomes());
        context.put("allTestOutcomes", testOutcomes);
        context.put("timestamp", timestampFrom(testOutcomes));
        context.put("reportName", new ReportNameProvider());
        context.put("absoluteReportName", new ReportNameProvider());
        context.put("reportOptions", new ReportOptions(getEnvironmentVariables()));
        context.put("relativeLink", relativeLink);

        VersionProvider versionProvider = new VersionProvider(getEnvironmentVariables());
        context.put("thucydidesVersionNumber", versionProvider.getVersion());
        context.put("buildNumber", versionProvider.getBuildNumberText());

        addFormattersToContext(context);

        String htmlContents = mergeTemplate(DEFAULT_REQUIREMENTS_REPORT).usingContext(context);
        copyResourcesToOutputDirectory();

        return writeReportToOutputDirectory(filename, htmlContents);
    }

    private void addFormattersToContext(final Map<String, Object> context) {
        Formatter formatter = new Formatter(issueTracking);
        context.put("formatter", formatter);

        context.put("formatted", new NumericalFormatter());
        context.put("inflection", Inflector.getInstance());
    }
}

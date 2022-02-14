package net.thucydides.core.reports.html;

import com.google.common.base.Preconditions;
import net.serenitybdd.core.reports.styling.TagStylist;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.*;
import net.thucydides.core.model.formatters.ReportFormatter;
import net.thucydides.core.model.screenshots.Screenshot;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.OutcomeFormat;
import net.thucydides.core.reports.ReportOptions;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.tags.BreadcrumbTagFilter;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import net.thucydides.core.util.TagInflector;
import net.thucydides.core.util.VersionProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static net.thucydides.core.model.ReportType.HTML;
import static net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;

/**
 * Generates acceptance test results in HTML form.
 */
public class HtmlAcceptanceTestReporter extends HtmlReporter implements AcceptanceTestReporter{

    private static final String DEFAULT_ACCEPTANCE_TEST_REPORT = "freemarker/default.ftl";
    private static final String DEFAULT_ACCEPTANCE_TEST_SCREENSHOT = "freemarker/screenshots.ftl";
    private static final int MAXIMUM_SCREENSHOT_WIDTH = 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger("serenity.reporting");

    private String qualifier;

    private final IssueTracking issueTracking;
    private RequirementsService requirementsService;

    public void setQualifier(final String qualifier) {
        this.qualifier = qualifier;
    }

    public HtmlAcceptanceTestReporter() {
        super();
        this.issueTracking = Injectors.getInjector().getInstance(IssueTracking.class);
        this.requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
    }

    public HtmlAcceptanceTestReporter(RequirementsService requirementsService) {
        super();
        this.requirementsService = requirementsService;
        this.issueTracking = Injectors.getInjector().getInstance(IssueTracking.class);
    }

    public HtmlAcceptanceTestReporter(final EnvironmentVariables environmentVariables,
                                      final IssueTracking issueTracking) {
        super(environmentVariables);
        this.issueTracking = issueTracking;
        this.requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
    }

    HtmlAcceptanceTestReporter(final EnvironmentVariables environmentVariables,
                               final RequirementsService requirementsService,
                               final IssueTracking issueTracking) {
        super(environmentVariables);
        this.issueTracking = issueTracking;
        this.requirementsService = requirementsService;
    }

    private ReportNameProvider getReportNameProvider() {
        return new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirementsService);
    }

    public String getName() {
        return "html";
    }

    /**
     * Generate an HTML report for a given test run.
     */
    public File generateReportFor(final TestOutcome testOutcome) throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        TestOutcome storedTestOutcome = testOutcome.withQualifier(qualifier);

        Map<String, Object> context = new HashMap<>();
        addTestOutcomeToContext(storedTestOutcome, context);

        if (containsScreenshots(storedTestOutcome)) {
            generateScreenshotReportsFor(storedTestOutcome);
        }

        addFormattersToContext(context);
        addTimestamp(testOutcome, context);

        String reportFilename = reportFor(storedTestOutcome);

        if(verboseReporting()) {
            String testName = storedTestOutcome.getName() + (StringUtils.isNotEmpty(qualifier) ? "/" + qualifier : "");
            String storyName = storedTestOutcome.getStoryTitle();
            String result =  coloredResult(testOutcome.getResult(), testOutcome.getResult().getAdjective());
            URI htmlReport = getOutputDirectory().toPath().resolve(reportFilename).toUri();
            String underline = underscores("| TEST NAME:   "  + testName);
            String message = underline + System.lineSeparator()
                            +"| TEST NAME:   " + colored.bold(testName) + System.lineSeparator()
                            +"| RESULT:      " + result + System.lineSeparator()
                            +"| REQUIREMENT: " + storyName + System.lineSeparator()
                            +"| REPORT:      " + colored.cyan(htmlReport.toString()) + System.lineSeparator()
                            + underline;

            LOGGER.info(System.lineSeparator() + message);
        }

        copyResourcesToOutputDirectory();

        return generateReportPage(context, DEFAULT_ACCEPTANCE_TEST_REPORT, reportFilename);
    }

    private String underscores(String message) {
        return StringUtils.repeat("-", message.length());
    }
    private String coloredResult(TestResult result, String text) {
        switch(result) {
            case SUCCESS: return colored.green(text);
            case PENDING: return colored.cyan(text);
            case IGNORED:
            case SKIPPED: return colored.grey(text);
            case FAILURE:
            case ERROR: return colored.red(text);
            case COMPROMISED: return colored.purple(text);
            default: return text;
        }
    }

    private File generateReportPage(final Map<String, Object> context,
                                      final String template,
                                      final String outputFile) throws IOException {

        Stopwatch stopwatch = Stopwatch.started();

        LOGGER.trace("Generating report in {}", outputFile);

        Path outputPath = getOutputDirectory().toPath().resolve(outputFile);
        try(BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            mergeTemplate(template).withContext(context).to(writer);
        }

        LOGGER.trace("Generated report {} in {} ms", outputFile, stopwatch.stop());
        return outputPath.toFile();
    }

    private boolean containsScreenshots(TestOutcome testOutcome) {

        return testOutcome.getFlattenedTestSteps()
                .stream()
                .anyMatch(
                        step -> step.getScreenshots() != null && (!step.getScreenshots().isEmpty())
                );
    }

    private void addTestOutcomeToContext(final TestOutcome testOutcome, final Map<String, Object> context) {
        context.put("testOutcome", testOutcome);
        context.put("currentTag", TestTag.EMPTY_TAG);
        context.put("reportNameInContext", getReportNameProvider());
        context.put("inflection", Inflector.getInstance());
        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        context.put("tagInflector", new TagInflector(environmentVariables));
        context.put("styling", TagStylist.from(environmentVariables));
        context.put("requirementTypes", requirementsService.getRequirementTypes());

        addParentRequirmentFieldToContext(testOutcome, context);
        addTimestamp(testOutcome, context);

    }

    private void addParentRequirmentFieldToContext(TestOutcome testOutcome, Map<String, Object> context) {
        java.util.Optional<Requirement> parentRequirement = requirementsService.getParentRequirementFor(testOutcome);
        java.util.Optional<Story> featureOrStory = java.util.Optional.ofNullable(testOutcome.getUserStory());
        String parentTitle = null;

        if (parentRequirement.isPresent()) {
            parentTitle = parentRequirement.get().getName();
            context.put("parentRequirement", parentRequirement);
            context.put("featureOrStory", Optional.empty());
            context.put("parentTitle", parentTitle);
            context.put("parentLink", getReportNameProvider().forRequirement(parentRequirement.get()));
        } else if (featureOrStory.isPresent()) {
            parentTitle = featureOrStory.get().getName();
            context.put("parentRequirement", Optional.empty());
            context.put("featureOrStory",featureOrStory);
            context.put("parentTitle", parentTitle);
            context.put("parentLink", getReportNameProvider().forTag(featureOrStory.get().asTag()));
        }

        addBreadcrumbs(testOutcome, context);
        addTags(testOutcome, context, parentTitle);
    }

    private void addTags(TestOutcome testOutcome, Map<String, Object> context, String parentTitle) {
        TagFilter tagFilter = new TagFilter(getEnvironmentVariables());
        Set<TestTag> filteredTags = (parentTitle != null) ? tagFilter.removeTagsWithName(testOutcome.getTags(), parentTitle) : testOutcome.getTags();
        filteredTags = tagFilter.removeHiddenTagsFrom(filteredTags);
        context.put("filteredTags", filteredTags);
    }

    private void addBreadcrumbs(TestOutcome testOutcome, Map<String, Object> context) {
        requirementsService.getAncestorRequirementsFor(testOutcome);
        List<TestTag> breadcrumbs = new BreadcrumbTagFilter(requirementsService).getRequirementBreadcrumbsFrom(testOutcome);
        context.put("breadcrumbs", breadcrumbs);
    }

    private void addFormattersToContext(final Map<String, Object> context) {
        Formatter formatter = new Formatter();
        ReportFormatter reportFormatter = new ReportFormatter();
        context.put("reportOptions", ReportOptions.forEnvironment(getEnvironmentVariables()));
        context.put("formatter", formatter);
        context.put("reportFormatter", reportFormatter);
        context.put("reportName", new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirementsService));
        context.put("absoluteReportName", new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirementsService));

        VersionProvider versionProvider = new VersionProvider(getEnvironmentVariables());
        context.put("serenityVersionNumber", versionProvider.getVersion());
        context.put("buildNumber", versionProvider.getBuildNumberText());
    }

    private void generateScreenshotReportsFor(final TestOutcome testOutcome) throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        List<Screenshot> screenshots = testOutcome.getStepScreenshots();

        String screenshotReport = testOutcome.getReportName() + "_screenshots.html";

        Map<String, Object> context = new HashMap<>();
        addTestOutcomeToContext(testOutcome, context);
        addFormattersToContext(context);
        context.put("screenshots", screenshots);
        context.put("narrativeView", testOutcome.getReportName());

        generateReportPage(context, DEFAULT_ACCEPTANCE_TEST_SCREENSHOT, screenshotReport);

    }

    private boolean isValidScreenshotFile(File screenshotFile) {
        return screenshotFile.isFile() && screenshotFile.length() > 0;
    }

    private int maxHeightOf(int maxHeight, File screenshotFile) throws IOException {
        int height = ResizableImage.loadFrom(screenshotFile).getHeight();
        int width = ResizableImage.loadFrom(screenshotFile).getWidth();
        if (width > MAXIMUM_SCREENSHOT_WIDTH) {
            height = (int) ((height * 1.0) * (MAXIMUM_SCREENSHOT_WIDTH * 1.0 / width));
        }
        if (height > maxHeight) {
            maxHeight = height;
        }
        return maxHeight;
    }

    private String reportFor(final TestOutcome testOutcome) {
        return testOutcome.withQualifier(qualifier).getReportName(HTML);
    }

    @Override
    public java.util.Optional<OutcomeFormat> getFormat() {
        return java.util.Optional.of(OutcomeFormat.HTML);
    }

}

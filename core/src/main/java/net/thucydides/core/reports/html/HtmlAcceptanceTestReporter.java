package net.thucydides.core.reports.html;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.Screenshot;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.OutcomeFormat;
import net.thucydides.core.reports.ReportOptions;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.screenshots.ScreenshotFormatter;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import net.thucydides.core.util.VersionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;
import static com.google.common.collect.Iterables.any;
import static net.thucydides.core.ThucydidesSystemProperty.THUCYDIDES_KEEP_UNSCALED_SCREENSHOTS;
import static net.thucydides.core.model.ReportType.HTML;

/**
 * Generates acceptance test results in HTML form.
 * 
 */
public class HtmlAcceptanceTestReporter extends HtmlReporter implements AcceptanceTestReporter {

    private static final String DEFAULT_ACCEPTANCE_TEST_REPORT = "freemarker/default.ftl";
    private static final String DEFAULT_ACCEPTANCE_TEST_SCREENSHOT = "freemarker/screenshots.ftl";
    private static final int MAXIMUM_SCREENSHOT_WIDTH = 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAcceptanceTestReporter.class);

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

    public HtmlAcceptanceTestReporter(final EnvironmentVariables environmentVariables,
                                      final IssueTracking issueTracking) {
        super(environmentVariables);
        this.issueTracking = issueTracking;
        this.requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
    }

    public String getName() {
        return "html";
    }

    /**
     * Generate an HTML report for a given test run.
     */
    public File generateReportFor(final TestOutcome testOutcome, TestOutcomes allTestOutcomes) throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        TestOutcome storedTestOutcome = testOutcome.withQualifier(qualifier);

        Map<String,Object> context = new HashMap<String,Object>();
        addTestOutcomeToContext(storedTestOutcome, allTestOutcomes, context);

        if (containsScreenshots(storedTestOutcome)) {
            generateScreenshotReportsFor(storedTestOutcome, allTestOutcomes);
        }

        addFormattersToContext(context);
        addTimestamp(testOutcome, context);

        String htmlContents = mergeTemplate(DEFAULT_ACCEPTANCE_TEST_REPORT).usingContext(context);
        copyResourcesToOutputDirectory();

        String reportFilename = reportFor(storedTestOutcome);
        LOGGER.info("GENERATING HTML REPORT FOR " + storedTestOutcome.getCompleteName() + (qualifier != null? "/" + qualifier : "") + " => " + reportFilename);

        return writeReportToOutputDirectory(reportFilename, htmlContents);
    }

    private boolean containsScreenshots(TestOutcome testOutcome) {
        return any(testOutcome.getFlattenedTestSteps(), hasScreenshot());
    }

    private Predicate<TestStep> hasScreenshot() {
        return new Predicate<TestStep>() {
            public boolean apply(TestStep testStep) {
                return ((testStep.getScreenshots() != null) && (!testStep.getScreenshots().isEmpty()));
            }
        };
    }

    private void addTestOutcomeToContext(final TestOutcome testOutcome, final TestOutcomes allTestOutcomes, final Map<String,Object> context) {
        context.put("allTestOutcomes", allTestOutcomes);
        context.put("testOutcome", testOutcome);
        context.put("currentTag", TestTag.EMPTY_TAG);
        context.put("inflection", Inflector.getInstance());
        context.put("parentRequirement", requirementsService.getParentRequirementFor(testOutcome));
        context.put("featureOrStory", Optional.fromNullable(testOutcome.getUserStory()));
        context.put("requirementTypes", requirementsService.getRequirementTypes());
        addTimestamp(testOutcome, context);
    }

    private void addFormattersToContext(final Map<String,Object> context) {
        Formatter formatter = new Formatter(issueTracking);
        context.put("reportOptions", new ReportOptions(getEnvironmentVariables()));
        context.put("formatter", formatter);
        context.put("reportName", new ReportNameProvider());
        context.put("absoluteReportName", new ReportNameProvider());
        context.put("reportOptions", new ReportOptions(getEnvironmentVariables()));

        VersionProvider versionProvider = new VersionProvider(getEnvironmentVariables());
        context.put("thucydidesVersionNumber", versionProvider.getVersion());
        context.put("buildNumber", versionProvider.getBuildNumberText());
    }

    private void generateScreenshotReportsFor(final TestOutcome testOutcome, final TestOutcomes allTestOutcomes) throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        List<Screenshot> screenshots = expandScreenshots(testOutcome.getScreenshots());

        String screenshotReport = testOutcome.getReportName() + "_screenshots.html";

        Map<String,Object> context = new HashMap<String,Object>();
        addTestOutcomeToContext(testOutcome, allTestOutcomes, context);
        addFormattersToContext(context);
        context.put("screenshots", screenshots);
        context.put("narrativeView", testOutcome.getReportName());
        String htmlContents = mergeTemplate(DEFAULT_ACCEPTANCE_TEST_SCREENSHOT).usingContext(context);
        writeReportToOutputDirectory(screenshotReport, htmlContents);

    }

    private List<Screenshot> expandScreenshots(List<Screenshot> screenshots) throws IOException {
        return convert(screenshots, new ExpandedScreenshotConverter(maxScreenshotHeightIn(screenshots)));
    }

    private class ExpandedScreenshotConverter implements Converter<Screenshot, Screenshot> {
        private final int maxHeight;

        public ExpandedScreenshotConverter(int maxHeight) {
            this.maxHeight = maxHeight;
        }

        public Screenshot convert(Screenshot screenshot) {
            try {
                return ScreenshotFormatter.forScreenshot(screenshot)
                                          .inDirectory(getOutputDirectory())
                                          .keepOriginals(shouldKeepOriginalScreenshots())
                                          .expandToHeight(maxHeight);
            } catch (IOException e) {
                LOGGER.warn("Failed to write scaled screenshot for {}: {}", screenshot, e);
                return screenshot;
            }
        }
    }

    private boolean shouldKeepOriginalScreenshots() {
        return getEnvironmentVariables().getPropertyAsBoolean(THUCYDIDES_KEEP_UNSCALED_SCREENSHOTS, false);
    }

    private int maxScreenshotHeightIn(List<Screenshot> screenshots) throws IOException {
        int maxHeight = 0;
        for (Screenshot screenshot : screenshots) {
            File screenshotFile = new File(getOutputDirectory(),screenshot.getFilename());
            if (screenshotFile.exists()) {
                maxHeight = maxHeightOf(maxHeight, screenshotFile);
            }
        }
        return maxHeight;
    }

    private int maxHeightOf(int maxHeight, File screenshotFile) throws IOException {
        int height = ResizableImage.loadFrom(screenshotFile).getHeight();
        int width = ResizableImage.loadFrom(screenshotFile).getWitdh();
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
    public Optional<OutcomeFormat> getFormat() {
        return Optional.of(OutcomeFormat.HTML);
    }
}

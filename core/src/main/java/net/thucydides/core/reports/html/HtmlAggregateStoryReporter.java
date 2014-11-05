package net.thucydides.core.reports.html;

import com.beust.jcommander.internal.Lists;
import net.thucydides.core.ThucydidesSystemProperties;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.NumericalFormatter;
import net.thucydides.core.model.Release;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.releases.ReleaseManager;
import net.thucydides.core.reports.*;
import net.thucydides.core.reports.csv.CSVReporter;
import net.thucydides.core.requirements.RequirementsProviderService;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.requirements.reports.RequirementOutcome;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.requirements.reports.RequirmentsOutcomeFactory;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import net.thucydides.core.util.VersionProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates an aggregate acceptance test report in HTML form.
 * Reads all the reports from the output directory to generates aggregate HTML reports
 * summarizing the results.
 */
public class HtmlAggregateStoryReporter extends HtmlReporter implements UserStoryTestReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAggregateStoryReporter.class);

    private static final String HISTORY_TEMPLATE_PATH = "freemarker/history.ftl";
    private static final String TEST_OUTCOME_TEMPLATE_PATH = "freemarker/home.ftl";
    private static final String RELEASES_TEMPLATE_PATH = "freemarker/releases.ftl";
    private static final String RELEASE_TEMPLATE_PATH = "freemarker/release.ftl";
    private static final String TAGTYPE_TEMPLATE_PATH = "freemarker/results-by-tagtype.ftl";
    private static final String REQUIREMENT_TYPE_TEMPLATE_PATH = "freemarker/requirement-type.ftl";

//    private TestHistory testHistory;
    private String projectName;
    private String relativeLink;
    private ReportNameProvider reportNameProvider;
    private final IssueTracking issueTracking;
    private final RequirementsService requirementsService;
    private final RequirmentsOutcomeFactory requirementsFactory;
    private final HtmlRequirementsReporter htmlRequirementsReporter;
    private final RequirementsConfiguration requirementsConfiguration;
    private final EnvironmentVariables environmentVariables;
    private FormatConfiguration formatConfiguration;

    public HtmlAggregateStoryReporter(final String projectName) {
        this(projectName, "");
    }

    public HtmlAggregateStoryReporter(final String projectName, final String relativeLink) {
        this(projectName, relativeLink,
             Injectors.getInjector().getInstance(IssueTracking.class),
             Injectors.getInjector().getInstance(RequirementsService.class),
             Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    public HtmlAggregateStoryReporter(final String projectName,
                                      final IssueTracking issueTracking) {
        this(projectName, "", issueTracking, Injectors.getInjector().getInstance(RequirementsService.class),
             Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    public HtmlAggregateStoryReporter(final String projectName,
                                      final String relativeLink,
                                      final IssueTracking issueTracking,
                                      final RequirementsService requirementsService,
                                      final EnvironmentVariables environmentVariables) {
        this.projectName = projectName;
        this.relativeLink = relativeLink;
        this.issueTracking = issueTracking;
//        this.testHistory = testHistory;
        this.reportNameProvider = new ReportNameProvider();
        this.htmlRequirementsReporter = new HtmlRequirementsReporter(relativeLink);

        RequirementsProviderService requirementsProviderService = Injectors.getInjector().getInstance(RequirementsProviderService.class);
        this.requirementsFactory = new RequirmentsOutcomeFactory(requirementsProviderService.getRequirementsProviders(), issueTracking);
        this.requirementsService = requirementsService;
        this.requirementsConfiguration = new RequirementsConfiguration(getEnvironmentVariables());
        this.environmentVariables = environmentVariables;
        this.formatConfiguration = new FormatConfiguration(environmentVariables);
    }

    public OutcomeFormat getFormat() {
       return formatConfiguration.getPreferredFormat();
    }

    public String getProjectName() {
        return projectName;
    }

    private void addFormattersToContext(final Map<String, Object> context) {
        Formatter formatter = new Formatter(issueTracking);
        context.put("formatter", formatter);
        context.put("formatted", new NumericalFormatter());
        context.put("inflection", Inflector.getInstance());
        context.put("relativeLink", relativeLink);
        context.put("reportOptions", new ReportOptions(getEnvironmentVariables()));
    }

    public TestOutcomes generateReportsForTestResultsFrom(final File sourceDirectory) throws IOException {
        TestOutcomes allTestOutcomes = loadTestOutcomesFrom(sourceDirectory);
        copyScreenshotsFrom(sourceDirectory);
        generateReportsForTestResultsIn(allTestOutcomes);
        return allTestOutcomes;
    }

    private void copyScreenshotsFrom(File sourceDirectory) {
        if ((getOutputDirectory() != null) && (getOutputDirectory() != sourceDirectory)) {
            CopyOption[] options = new CopyOption[]{ StandardCopyOption.COPY_ATTRIBUTES };

            Path targetPath = Paths.get(getOutputDirectory().toURI());
            Path sourcePath = Paths.get(sourceDirectory.toURI());
            try {

                DirectoryStream<Path> directoryContents = Files.newDirectoryStream(sourcePath);
                for(Path sourceFile : directoryContents) {
                    Path destinationFile = targetPath.resolve(sourceFile.getFileName());
                    if (Files.notExists(destinationFile)) {
                        Files.copy(sourceFile, destinationFile, options);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Error during copying files to the target directory", e);
            }
        }
    }

    public void generateReportsForTestResultsIn(TestOutcomes testOutcomes) throws IOException {
        RequirementsOutcomes requirementsOutcomes = requirementsFactory.buildRequirementsOutcomesFrom(testOutcomes.withRequirementsTags());

        copyResourcesToOutputDirectory();
        copyTestResultsToOutputDirectory();

        generateAggregateReportFor(testOutcomes);
        generateTagReportsFor(testOutcomes);
        generateTagTypeReportsFor(testOutcomes);
        for (String name : testOutcomes.getTagNames()) {
            generateTagTypeReportsFor(testOutcomes.withTag(name), new ReportNameProvider(name));
        }
        generateRequirementTypeReports(requirementsOutcomes);
        generateResultReportsFor(testOutcomes);
//        generateHistoryReportFor(testOutcomes);
//        generateCoverageReportsFor(testOutcomes);

        generateRequirementsReportsFor(requirementsOutcomes);

        generateReleasesReportFor(testOutcomes, requirementsOutcomes);

    }

    private void generateRequirementTypeReports(RequirementsOutcomes requirementsOutcomes) throws IOException {
        List<String> requirementTypes = requirementsOutcomes.getTypes();
        for (String requirementType : requirementTypes) {
            generateRequirementTypeReportFor(requirementType,
                    requirementsOutcomes.requirementsOfType(requirementType),
                    new ReportNameProvider());
        }
    }

    private void generateRequirementTypeReportFor(String requirementType,
                                                  RequirementsOutcomes requirementsOutcomes,
                                                  ReportNameProvider reporter) throws IOException {
        Map<String, Object> context = buildContext(requirementsOutcomes.getTestOutcomes(), getReportNameProvider());
        context.put("report", ReportProperties.forAggregateResultsReport());
        context.put("requirementType", requirementType);
        context.put("requirements", requirementsOutcomes);

        String reportName = reporter.forRequirementType(requirementType);
        generateReportPage(context, REQUIREMENT_TYPE_TEMPLATE_PATH, reportName);

    }

    private void generateCSVReportFor(TestOutcomes testOutcomes, String reportName) throws IOException {
        CSVReporter csvReporter = new CSVReporter(getOutputDirectory(), getEnvironmentVariables());
        csvReporter.generateReportFor(testOutcomes, reportName);
    }

    List<Requirement> reportTally = Lists.newArrayList();

    public void generateRequirementsReportsFor(RequirementsOutcomes requirementsOutcomes) throws IOException {

        htmlRequirementsReporter.setOutputDirectory(getOutputDirectory());
        htmlRequirementsReporter.generateReportFor(requirementsOutcomes);

//        htmlProgressReporter.setOutputDirectory(getOutputDirectory());
//        htmlProgressReporter.generateReportFor(requirementsOutcomes);

        clearReportTally();
        generateRequirementsReportsForChildRequirements(requirementsOutcomes);
    }

    private void clearReportTally() {
        reportTally.clear();
    }

    private void generateRequirementsReportsForChildRequirements(RequirementsOutcomes requirementsOutcomes) throws IOException {
        List<RequirementOutcome> requirementOutcomes = requirementsOutcomes.getRequirementOutcomes();
        for (RequirementOutcome outcome : requirementOutcomes) {
            Requirement requirement = outcome.getRequirement();
            if (!reportTally.contains(requirement)) {
                TestOutcomes testOutcomesForThisRequirement = outcome.getTestOutcomes().withTag(requirement.asTag());
                RequirementsOutcomes requirementOutcomesForThisRequirement = requirementsFactory.buildRequirementsOutcomesFrom(requirement, testOutcomesForThisRequirement);
                generateNestedRequirementsReportsFor(requirement, requirementOutcomesForThisRequirement);
            }
        }
    }

    private void generateNestedRequirementsReportsFor(Requirement parentRequirement, RequirementsOutcomes requirementsOutcomes) throws IOException {
        htmlRequirementsReporter.setOutputDirectory(getOutputDirectory());
        String reportName = reportNameProvider.forRequirement(parentRequirement);
        if (!reportTally.contains(parentRequirement)) {
            reportTally.add(parentRequirement);
            htmlRequirementsReporter.generateReportFor(requirementsOutcomes, requirementsOutcomes.getTestOutcomes(), reportName);
        }

        generateRequirementsReportsForChildRequirements(requirementsOutcomes);

    }

    private TestOutcomes loadTestOutcomesFrom(File sourceDirectory) throws IOException {
        return TestOutcomeLoader.loadTestOutcomes().inFormat(getFormat()).from(sourceDirectory).withHistory().withRequirementsTags();
    }

    private void generateAggregateReportFor(TestOutcomes testOutcomes) throws IOException {

        ReportNameProvider defaultNameProvider = new ReportNameProvider();
        Map<String, Object> context = buildContext(testOutcomes, defaultNameProvider, true);
        context.put("report", ReportProperties.forAggregateResultsReport());
        context.put("csvReport", "results.csv");

        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, "index.html");
        generateCSVReportFor(testOutcomes, "results.csv");
    }

    private ReleaseManager releaseManager;

    private ReleaseManager getReleaseManager() {
        if (releaseManager == null) {
            ReportNameProvider defaultNameProvider = new ReportNameProvider();
            releaseManager = new ReleaseManager(getEnvironmentVariables(), defaultNameProvider);
        }
        return releaseManager;
    }

    private ReportNameProvider defaultNameProvider;

    private ReportNameProvider getReportNameProvider() {
        if (defaultNameProvider == null) {
            defaultNameProvider = new ReportNameProvider();
        }
        return defaultNameProvider;
    }

    private void generateReleasesReportFor(TestOutcomes testOutcomes,
                                           RequirementsOutcomes requirementsOutcomes) throws IOException {
        Map<String, Object> context = buildContext(testOutcomes, getReportNameProvider());
        context.put("report", ReportProperties.forAggregateResultsReport());
        List<Release> releases = getReleaseManager().getReleasesFrom(testOutcomes);
        LOGGER.info("Generating release reports for: " + releases);
        if (!releases.isEmpty()) {
            String releaseData = getReleaseManager().getJSONReleasesFrom(testOutcomes);
            context.put("releases", releases);
            context.put("releaseData", releaseData);
            context.put("requirements", requirementsOutcomes);

            generateReportPage(context, RELEASES_TEMPLATE_PATH, "releases.html");
            generateReleaseDetailsReportsFor(testOutcomes, requirementsOutcomes);
        }
    }

    private void generateReleaseDetailsReportsFor(TestOutcomes testOutcomes,
                                                  RequirementsOutcomes requirementsOutcomes) throws IOException {
        List<Release> allReleases = getReleaseManager().getFlattenedReleasesFrom(testOutcomes);
        List<String> requirementsTypes = getRequirementTypes();
        String topLevelRequirementType = requirementsTypes.get(0);
        String secondLevelRequirementType = "";
        String secondLevelRequirementTypeTitle = "";
        String topLevelRequirementTypeTitle = Inflector.getInstance().of(topLevelRequirementType)
                .inPluralForm().asATitle().toString();

        if (requirementsTypes.size() > 1) {
            secondLevelRequirementType = requirementsTypes.get(1);
            secondLevelRequirementTypeTitle = Inflector.getInstance().of(secondLevelRequirementType)
                    .inPluralForm().asATitle().toString();
        }
        for (Release release : allReleases) {
            RequirementsOutcomes releaseRequirements = requirementsOutcomes.getReleasedRequirementsFor(release);
            Map<String, Object> context = buildContext(testOutcomes, getReportNameProvider());

            context.put("report", ReportProperties.forAggregateResultsReport());
            context.put("release", release);

            context.put("releaseData", getReleaseManager().getJSONReleasesFrom(release));
            context.put("releaseRequirementOutcomes", releaseRequirements.getRequirementOutcomes());
            context.put("releaseTestOutcomes", testOutcomes.withTag(release.getReleaseTag()));

            context.put("requirementType", topLevelRequirementTypeTitle);
            if (StringUtils.isNotBlank(secondLevelRequirementTypeTitle)) {
                context.put("secondLevelRequirementType", secondLevelRequirementTypeTitle);
            }

            // capability | features | total automated tests | %automated pass | total manual | % manual
            String reportName = getReportNameProvider().forRelease(release);
            generateReportPage(context, RELEASE_TEMPLATE_PATH, reportName);
        }
    }

    private void generateTagReportsFor(TestOutcomes testOutcomes) throws IOException {

        for (TestTag tag : testOutcomes.getTags()) {
            generateTagReport(testOutcomes, reportNameProvider, tag);
            generateAssociatedTagReportsForTag(testOutcomes.withTag(tag), tag.getName());
        }
    }

    private void generateTagTypeReportsFor(TestOutcomes testOutcomes) throws IOException {
        generateTagTypeReportsFor(testOutcomes, reportNameProvider);
    }

    private void generateTagTypeReportsFor(TestOutcomes testOutcomes, ReportNameProvider reportNameProvider) throws IOException {

        for (String tagType : testOutcomes.getTagTypes()) {
            generateTagTypeReport(testOutcomes, reportNameProvider, tagType);
        }
    }

    private void generateResultReportsFor(TestOutcomes testOutcomes) throws IOException {
        generateResultReports(testOutcomes, reportNameProvider);

        for (TestTag tag : testOutcomes.getTags()) {
            generateResultReports(testOutcomes.withTag(tag), new ReportNameProvider(tag.getName()), tag);
        }
    }

//    private void generateCoverageReportsFor(TestOutcomes testOutcomes) throws IOException {
//
//        for (String tagType : testOutcomes.getTagTypes()) {
//            generateCoverageData(testOutcomes, tagType);
//        }
//    }
    private void generateResultReports(TestOutcomes testOutcomes, ReportNameProvider reportName) throws IOException {
        generateResultReports(testOutcomes,reportName, TestTag.EMPTY_TAG);
    }

    private void generateResultReports(TestOutcomes testOutcomesForThisTag, ReportNameProvider reportName, TestTag tag) throws IOException {
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.SUCCESS) > 0) {
            generateResultReport(testOutcomesForThisTag.getPassingTests(), reportName, tag, "success");
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.PENDING) > 0) {
            generateResultReport(testOutcomesForThisTag.getPendingTests(), reportName, tag, "pending");
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.FAILURE) > 0) {
            generateResultReport(testOutcomesForThisTag.getFailingTests(), reportName, tag, "failure");
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.ERROR) > 0) {
            generateResultReport(testOutcomesForThisTag.getErrorTests(), reportName, tag, "error");
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.IGNORED) > 0) {
            generateResultReport(testOutcomesForThisTag.havingResult(TestResult.IGNORED), reportName, tag, "ignored");
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.SKIPPED) > 0) {
            generateResultReport(testOutcomesForThisTag.havingResult(TestResult.SKIPPED), reportName, tag, "skipped");
        }
    }

    private void generateResultReport(TestOutcomes testOutcomes, ReportNameProvider reportName, TestTag tag, String testResult) throws IOException {
        Map<String, Object> context = buildContext(testOutcomes, reportName);
        context.put("report", ReportProperties.forTestResultsReport());
        context.put("currentTagType", tag.getType());
        context.put("currentTag", tag);

        String csvReport = reportName.forCSVFiles().forTestResult(testResult);
        context.put("csvReport", csvReport);
        String report = reportName.withPrefix(tag).forTestResult(testResult);
        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, report);
        generateCSVReportFor(testOutcomes, csvReport);
    }

    private void generateTagReport(TestOutcomes testOutcomes, ReportNameProvider reportName, TestTag tag) throws IOException {
        TestOutcomes testOutcomesForTag = testOutcomes.withTag(tag);
        Map<String, Object> context = buildContext(testOutcomesForTag, reportName);
        context.put("report", ReportProperties.forTagResultsReport());
        context.put("currentTagType", tag.getType());
        context.put("currentTag", tag);

        String csvReport = reportName.forCSVFiles().forTag(tag);
        context.put("csvReport", csvReport);

        String report = reportName.forTag(tag);
        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, report);
        generateCSVReportFor(testOutcomesForTag, csvReport);
    }


    private void generateTagTypeReport(TestOutcomes testOutcomes, ReportNameProvider reportName, String tagType) throws IOException {

        TestOutcomes testOutcomesForTagType = testOutcomes.withTagType(tagType);

        Map<String, Object> context = buildContext(testOutcomesForTagType, reportName);
        context.put("report", ReportProperties.forTagTypeResultsReport());
        context.put("tagType", tagType);

        String csvReport = reportName.forCSVFiles().forTagType(tagType);
        context.put("csvReport", csvReport);

        String report = reportName.forTagType(tagType);
        generateReportPage(context, TAGTYPE_TEMPLATE_PATH, report);
        generateCSVReportFor(testOutcomesForTagType, csvReport);
    }

    private void generateAssociatedTagReportsForTag(TestOutcomes testOutcomes, String sourceTag) throws IOException {
        ReportNameProvider reportName = new ReportNameProvider(sourceTag);
        for (TestTag tag : testOutcomes.getTags()) {
            generateTagReport(testOutcomes, reportName, tag);
        }
    }

    private Map<String, Object> buildContext(TestOutcomes testOutcomesForTagType,
                                             ReportNameProvider reportName) {
        return buildContext(testOutcomesForTagType, reportName, false);
    }

    private Map<String, Object> buildContext(TestOutcomes testOutcomesForTagType,
                                             ReportNameProvider reportName,
                                             boolean useFiltering) {
        Map<String, Object> context = new HashMap<String, Object>();
        TagFilter tagFilter = new TagFilter(getEnvironmentVariables());
        context.put("testOutcomes", testOutcomesForTagType);
        context.put("allTestOutcomes", testOutcomesForTagType.getRootOutcomes());
        if (useFiltering) {
            context.put("tagTypes", tagFilter.filteredTagTypes(testOutcomesForTagType.getTagTypes()));
        } else {
            context.put("tagTypes", testOutcomesForTagType.getTagTypes());
        }
        context.put("currentTag", TestTag.EMPTY_TAG);
        context.put("reportName", reportName);
        context.put("absoluteReportName", new ReportNameProvider());

        context.put("reportOptions", new ReportOptions(getEnvironmentVariables()));
        //context.put("timestamp", timestampFrom(testOutcomesForTagType.getRootOutcomes()));
        context.put("timestamp", timestampFrom(currentTime()));
        context.put("requirementTypes", requirementsService.getRequirementTypes());
        addFormattersToContext(context);


        VersionProvider versionProvider = new VersionProvider(environmentVariables);
        context.put("thucydidesVersionNumber", versionProvider.getVersion());
        context.put("buildNumber", versionProvider.getBuildNumberText());

        return context;
    }

    private void generateReportPage(final Map<String, Object> context,
                                    final String template,
                                    final String outputFile) throws IOException {
        String htmlContents = mergeTemplate(template).usingContext(context);
        writeReportToOutputDirectory(outputFile, htmlContents);
    }

    protected ThucydidesSystemProperties getSystemProperties() {
        return ThucydidesSystemProperties.getProperties();
    }

    public void setIssueTrackerUrl(String issueTrackerUrl) {
        if (issueTrackerUrl != null) {
            getSystemProperties().setValue(ThucydidesSystemProperty.THUCYDIDES_ISSUE_TRACKER_URL, issueTrackerUrl);
        }
    }

    public void setJiraUrl(String jiraUrl) {
        if (jiraUrl != null) {
            getSystemProperties().setValue(ThucydidesSystemProperty.JIRA_URL, jiraUrl);
        }
    }

    public void setJiraProject(String jiraProject) {
        if (jiraProject != null) {
            getSystemProperties().setValue(ThucydidesSystemProperty.JIRA_PROJECT, jiraProject);
        }
    }

    public void setJiraUsername(String jiraUsername) {
        if (jiraUsername != null) {
            getSystemProperties().setValue(ThucydidesSystemProperty.JIRA_USERNAME, jiraUsername);
        }
    }

    public void setJiraPassword(String jiraPassword) {
        if (jiraPassword != null) {
            getSystemProperties().setValue(ThucydidesSystemProperty.JIRA_PASSWORD, jiraPassword);
        }
    }

    public List<String> getRequirementTypes() {
        List<String> types = requirementsService.getRequirementTypes();
        if (types.isEmpty()) {
            LOGGER.warn("No requirement types found in the test outcome requirements: using default requirements");
            return requirementsConfiguration.getRequirementTypes();
        } else {
            return types;
        }
    }

    /**
     * Check the test outcomes for failures or errors, and throw an appropriate exception if one is found.
     */
    public void checkOutcomes() {

    }
}


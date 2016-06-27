package net.thucydides.core.reports.html;

import com.google.common.collect.Lists;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.model.Release;
import net.thucydides.core.releases.ReleaseManager;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.requirements.reports.RequirementOutcome;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.requirements.reports.RequirementsOutcomeFactory;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RequirementsReportingTask extends BaseReportingTask implements ReportingTask  {

    private static final String REQUIREMENT_TYPE_TEMPLATE_PATH = "freemarker/requirement-type.ftl";
    private static final String RELEASE_TEMPLATE_PATH = "freemarker/release.ftl";
    private static final String RELEASES_TEMPLATE_PATH = "freemarker/releases.ftl";

    private ReportNameProvider reportNameProvider;
    private RequirementsOutcomeFactory requirementsFactory;
    private HtmlRequirementsReporter htmlRequirementsReporter;
    private ReleaseManager releaseManager;

    private final RequirementsService requirementsService;
    private final RequirementsConfiguration requirementsConfiguration;

    public RequirementsReportingTask(FreemarkerContext freemarker,
                                     EnvironmentVariables environmentVariables,
                                     File outputDirectory,
                                     ReportNameProvider reportNameProvider,
                                     RequirementsOutcomeFactory requirementsFactory,
                                     RequirementsService requirementsService,
                                     String relativeLink) {
        super(freemarker, environmentVariables, outputDirectory);
        this.reportNameProvider = reportNameProvider;

        this.requirementsFactory = requirementsFactory;
        this.requirementsService = requirementsService;
        this.requirementsConfiguration = new RequirementsConfiguration(environmentVariables);

        this.htmlRequirementsReporter = new HtmlRequirementsReporter(relativeLink);
        this.htmlRequirementsReporter.setOutputDirectory(outputDirectory);

    }

    public void generateReportsFor(TestOutcomes testOutcomes) throws IOException {

        Stopwatch stopwatch = Stopwatch.started();

        RequirementsOutcomes requirementsOutcomes = requirementsFactory.buildRequirementsOutcomesFrom(testOutcomes);

        generateRequirementTypeReports(requirementsOutcomes);

        generateRequirementsReportsFor(requirementsOutcomes);

        generateReleasesReportFor(testOutcomes, requirementsOutcomes);

        LOGGER.trace("Requirements reports generated: {} ms", stopwatch.stop());
    }

    private void generateRequirementTypeReports(RequirementsOutcomes requirementsOutcomes) throws IOException {
        for (String requirementType : requirementsOutcomes.getTypes()) {
            generateRequirementTypeReportFor(requirementType,
                                             requirementsOutcomes.requirementsOfType(requirementType));
        }
    }

    private void generateRequirementTypeReportFor(String requirementType,
                                                  RequirementsOutcomes requirementsOutcomes) throws IOException {

        Map<String, Object> context = freemarker.getBuildContext(requirementsOutcomes.getTestOutcomes(), reportNameProvider, true);

        context.put("report", ReportProperties.forAggregateResultsReport());
        context.put("requirementType", requirementType);
        context.put("requirements", requirementsOutcomes);

        String reportName = reportNameProvider.forRequirementType(requirementType);
        generateReportPage(context, REQUIREMENT_TYPE_TEMPLATE_PATH, reportName);

    }

    List<Requirement> reportTally = Lists.newArrayList();

    public void generateRequirementsReportsFor(RequirementsOutcomes requirementsOutcomes) throws IOException {

        htmlRequirementsReporter.generateReportFor(requirementsOutcomes);

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
        String reportName = reportNameProvider.forRequirement(parentRequirement);
        String orphanReportName = reportNameProvider.forRequirement(parentRequirement.getName());
        if (!reportTally.contains(parentRequirement)) {
            reportTally.add(parentRequirement);
            htmlRequirementsReporter.generateReportFor(requirementsOutcomes, requirementsOutcomes.getTestOutcomes(), reportName);
            htmlRequirementsReporter.generateReportFor(requirementsOutcomes, requirementsOutcomes.getTestOutcomes(), orphanReportName);
        }

        generateRequirementsReportsForChildRequirements(requirementsOutcomes);

    }

    private void generateReleasesReportFor(TestOutcomes testOutcomes,
                                           RequirementsOutcomes requirementsOutcomes) throws IOException {
        Map<String, Object> context = freemarker.getBuildContext(requirementsOutcomes.getTestOutcomes(), reportNameProvider, true);
        context.put("report", ReportProperties.forAggregateResultsReport());
        List<Release> releases = getReleaseManager().getReleasesFrom(testOutcomes);

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
            Map<String, Object> context = freemarker.getBuildContext(testOutcomes, reportNameProvider, true);

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
            String reportName = reportNameProvider.forRelease(release);
            generateReportPage(context, RELEASE_TEMPLATE_PATH, reportName);
        }
    }

    public List<String> getRequirementTypes() {
        List<String> types = requirementsService.getRequirementTypes();
        if (types.isEmpty()) {
            return requirementsConfiguration.getRequirementTypes();
        } else {
            return types;
        }
    }

    private ReleaseManager getReleaseManager() {
        if (releaseManager == null) {
            releaseManager = new ReleaseManager(environmentVariables, reportNameProvider);
        }
        return releaseManager;
    }



}

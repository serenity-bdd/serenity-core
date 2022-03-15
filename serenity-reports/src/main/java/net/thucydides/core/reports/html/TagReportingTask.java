package net.thucydides.core.reports.html;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TagReportingTask extends BaseReportingTask implements ReportingTask {

    private static final String TEST_OUTCOME_TEMPLATE_PATH = "freemarker/home.ftl";

    protected final ReportNameProvider reportNameProvider;
    private final TestTag tag;
    private final List<TestTag> allTags;
    private final TestOutcomes testOutcomes;
    private final String reportName;

    TagReportingTask(final FreemarkerContext freemarker,
                     final EnvironmentVariables environmentVariables,
                     final File outputDirectory,
                     final ReportNameProvider reportNameProvider,
                     final String reportName,
                     final TestTag tag,
                     final List<TestTag> allTags,
                     final TestOutcomes testOutcomes) {
        super(freemarker, environmentVariables, outputDirectory);
        this.reportNameProvider = reportNameProvider;
        this.tag = tag;
        this.allTags = allTags;
        this.testOutcomes = testOutcomes;
        this.reportName = reportName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagReportingTask that = (TagReportingTask) o;
        return com.google.common.base.Objects.equal(reportName, that.reportName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reportName);
    }

    static TagReportBuilder tagReportsFor(TestOutcomes testOutcomes) {
        return new TagReportBuilder(testOutcomes);
    }

    public void generateReports() throws IOException {

        Stopwatch stopwatch = Stopwatch.started();
        generateTagReport(testOutcomes, reportNameProvider, tag);
        LOGGER.trace("Tag reports generated: {} ms", stopwatch.stop());
    }

    private void generateTagReport(TestOutcomes testOutcomes, ReportNameProvider reportNameProvider, TestTag tag) throws IOException {
        TestOutcomes testOutcomesForTag = testOutcomes.withTag(tag);
        Map<String, Object> context = freemarker.getBuildContext(testOutcomesForTag, reportNameProvider, true);
        context.put("report", ReportProperties.forTagResultsReport());
        context.put("currentTagType", tag.getType());
        context.put("currentTag", tag);
        context.put("reportName", reportNameProvider);
        context.put("reportNameInContext", reportNameProvider.inContext(tag.getCompleteName()));

        String csvReport = reportNameProvider.forCSVFiles().forTag(tag);
        context.put("csvReport", csvReport);

        context.put("breadcrumbs", Breadcrumbs.forRequirementsTag(tag).fromTagsIn(allTags));

        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, reportName);
        generateCSVReportFor(testOutcomesForTag, csvReport);

        String linkableReport = reportNameProvider.inLinkableForm().forTag(tag);
        if (!linkableReport.equals(reportName) && shouldGenerateLinkableReportsFor(tag, reportNameProvider)) {
            generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, linkableReport);
        }
    }

    private boolean shouldGenerateLinkableReportsFor(TestTag tag, ReportNameProvider reportName) {
        if (reportName.getContext().isEmpty()) {
            return false;
        }

        List<String> linkableTags = Splitter.on(",").trimResults().splitToList(
                ThucydidesSystemProperty.SERENITY_LINKED_TAGS.from(environmentVariables, "").toLowerCase());

        return containsTag(linkableTags, tag.getType().toLowerCase()) || containsTag(linkableTags, tag.getName().toLowerCase());
    }

    private boolean containsTag(List<String> linkableTags, String tag) {
        for (String linkableTag : linkableTags) {
            if (linkableTag.equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "TagReportingTask for " + tag;
    }

    public static class TagReportBuilder {
        private final TestOutcomes testOutcomes;

        private final Inflector inflection = Inflector.getInstance();

        TagReportBuilder(TestOutcomes testOutcomes) {
            this.testOutcomes = testOutcomes;
        }

        public List<ReportingTask> using(final FreemarkerContext freemarker,
                                         final EnvironmentVariables environmentVariables,
                                         final File outputDirectory,
                                         final ReportNameProvider reportName,
                                         final List<TestTag> allTags,
                                         final List<String> requirementTypes,
                                         final List<String> knownRequirementReportNames) {

            TagExclusions exclusions = TagExclusions.usingEnvironment(environmentVariables);
            Set<TestTag> reportedTags = testOutcomes.getTags().stream()
                    .filter(exclusions::doNotExclude)
                    .filter(tag -> !requirementTag(requirementTypes, tag))
                    .filter(tag -> !tag.getType().equals("Duration"))
                    .filter(tag -> !tag.isIssueTag())
                    .collect(Collectors.toSet());

            reportedTags.addAll(
                    testOutcomes.getFlags()
                            .stream()
                            .map(flag -> TestTag.withName(inflection.of(flag.getMessage()).asATitle().toString()).andType("flag"))
                            .collect(Collectors.toList())
            );

            return reportedTags
                    .stream()
                    .map(tag -> new TagReportingTask(
                            freemarker,
                            environmentVariables,
                            outputDirectory,
                            reportName,
                            reportName.forTag(tag),
                            tag,
                            allTags,
                            testOutcomes))
                    .collect(Collectors.toList());

        }

        private boolean requirementTag(List<String> requirementTypes, TestTag tag) {
            return requirementTypes.contains(tag.getType());
        }


    }

    @Override
    public String reportName() {
        return reportName;
    }
}

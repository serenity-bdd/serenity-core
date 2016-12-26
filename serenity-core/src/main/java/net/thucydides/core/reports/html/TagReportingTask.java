package net.thucydides.core.reports.html;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TagReportingTask extends BaseReportingTask implements ReportingTask {

    private static final String TEST_OUTCOME_TEMPLATE_PATH = "freemarker/home.ftl";

    protected ReportNameProvider reportNameProvider;
    private final TestTag tag;
    private final List<TestTag> allTags;
    private final TestOutcomes testOutcomes;
    private final String reportName;

    protected TagReportingTask(final FreemarkerContext freemarker,
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

    public static TagReportBuilder tagReportsFor(TestOutcomes testOutcomes) {
        return new TagReportBuilder(testOutcomes);
    }

    public void generateReports() throws IOException {

        Stopwatch stopwatch = Stopwatch.started();
        generateTagReport(testOutcomes, reportNameProvider, tag);
        LOGGER.trace("Tag reports generated: {} ms", stopwatch.stop());
    }

    void generateTagReport(TestOutcomes testOutcomes, ReportNameProvider reportNameProvider, TestTag tag) throws IOException {

        LOGGER.debug("GENERATE TAG REPORTS FOR " + tag);

        TestOutcomes testOutcomesForTag = testOutcomes.withTag(tag);
        Map<String, Object> context = freemarker.getBuildContext(testOutcomesForTag, reportNameProvider, true);
        context.put("report", ReportProperties.forTagResultsReport());
        context.put("currentTagType", tag.getType());
        context.put("currentTag", tag);

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

        public TagReportBuilder(TestOutcomes testOutcomes) {
            this.testOutcomes = testOutcomes;
        }

        public Set<ReportingTask> using(final FreemarkerContext freemarker,
                                        final EnvironmentVariables environmentVariables,
                                        final File outputDirectory,
                                        final ReportNameProvider reportNameProvider,
                                        final List<TestTag> allTags,
                                        final List<String> knownRequirementReportNames) {

            Set<ReportingTask> reportingTasks = new HashSet<>();

            for (TestTag tag : testOutcomes.getTags()) {
                String reportName = reportNameProvider.forTag(tag);
                if (!knownRequirementReportNames.contains(reportName)) {
                    reportingTasks.add(
                            new TagReportingTask(freemarker,
                                    environmentVariables,
                                    outputDirectory,
                                    reportNameProvider,
                                    reportName,
                                    tag,
                                    allTags,
                                    testOutcomes)
                    );
                }
            }
            return reportingTasks;
        }
    }

}

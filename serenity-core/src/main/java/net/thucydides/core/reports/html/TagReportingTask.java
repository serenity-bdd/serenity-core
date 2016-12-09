package net.thucydides.core.reports.html;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Splitter;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;

public class TagReportingTask extends BaseReportingTask implements ReportingTask {

    private static final String TEST_OUTCOME_TEMPLATE_PATH = "freemarker/home.ftl";

    protected ReportNameProvider reportNameProvider;
    private final TestTag tag;
    private final List<String> requirementTypes;
    private final List<TestTag> allTags;
    private final TestOutcomes testOutcomes;

    protected TagReportingTask(final FreemarkerContext freemarker,
                               final EnvironmentVariables environmentVariables,
                               final File outputDirectory,
                               final ReportNameProvider reportNameProvider,
                               final List<String> requirementTypes,
                               final TestTag tag,
                               final List<TestTag> allTags,
                               final TestOutcomes testOutcomes) {
        super(freemarker, environmentVariables, outputDirectory);
        this.reportNameProvider = reportNameProvider;
        this.tag = tag;
        this.requirementTypes = requirementTypes;
        this.allTags = allTags;
        this.testOutcomes = testOutcomes;
    }

    public static TagReportBuilder tagReportsFor(TestOutcomes testOutcomes) {
        return new TagReportBuilder(testOutcomes);
    }

    public void generateReports() throws IOException {

        Stopwatch stopwatch = Stopwatch.started();
        generateTagReport(testOutcomes, reportNameProvider, tag);
        LOGGER.trace("Tag reports generated: {} ms", stopwatch.stop());
    }

    void generateTagReport(TestOutcomes testOutcomes, ReportNameProvider reportName, TestTag tag) throws IOException {

        LOGGER.debug("GENERATE TAG REPORTS FOR " + tag);

        TestOutcomes testOutcomesForTag = testOutcomes.withTag(tag);
        Map<String, Object> context = freemarker.getBuildContext(testOutcomesForTag, reportName, true);
        context.put("report", ReportProperties.forTagResultsReport());
        context.put("currentTagType", tag.getType());
        context.put("currentTag", tag);

        String csvReport = reportName.forCSVFiles().forTag(tag);
        context.put("csvReport", csvReport);

        context.put("breadcrumbs", Breadcrumbs.forRequirementsTag(tag).fromTagsIn(allTags));

        String report = reportName.forTag(tag);

        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, report);
        generateDefaultRequirementsPage(context, reportName, tag, requirementTypes);

        generateCSVReportFor(testOutcomesForTag, csvReport);

        if (shouldGenerateLinkableReportsFor(tag, reportName)) {
            String linkableReport = reportName.inLinkableForm().forTag(tag);
            generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, linkableReport);
        }
    }

    private void generateDefaultRequirementsPage(Map<String, Object> context,
                                                 ReportNameProvider reportName,
                                                 TestTag tag,
                                                 List<String> requirementTypes) throws IOException {
        if (requirementTypes.contains(tag.getType())) {
            String defaultRequirementsReport = reportName.forRequirement(tag);
            generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, defaultRequirementsReport);
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

        public List<ReportingTask> using(final FreemarkerContext freemarker,
                                         final EnvironmentVariables environmentVariables,
                                         final File outputDirectory,
                                         final ReportNameProvider reportNameProvider,
                                         final List<String> requirementTypes,
                                         final List<TestTag> allTags) {
            return convert(testOutcomes.getTags(), toTagReports(freemarker,
                                                                environmentVariables,
                                                                outputDirectory,
                                                                reportNameProvider,
                                                                requirementTypes,
                                                                allTags));
        }

        private Converter<TestTag, ReportingTask> toTagReports(final FreemarkerContext freemarker,
                                                               final EnvironmentVariables environmentVariables,
                                                               final File outputDirectory,
                                                               final ReportNameProvider reportNameProvider,
                                                               final List<String> requirementTypes,
                                                               final List<TestTag> allTags) {
            return new Converter<TestTag, ReportingTask>() {
                @Override
                public ReportingTask convert(TestTag tag) {
                    return new TagReportingTask(freemarker, environmentVariables, outputDirectory, reportNameProvider, requirementTypes, tag, allTags, testOutcomes);
                }
            };
        }
    }
}

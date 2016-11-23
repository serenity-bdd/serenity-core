package net.thucydides.core.reports.html;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TagReportingTask extends BaseReportingTask implements ReportingTask {

    private static final String TEST_OUTCOME_TEMPLATE_PATH = "freemarker/home.ftl";

    protected ReportNameProvider reportNameProvider;

    public TagReportingTask(FreemarkerContext freemarker,
                            EnvironmentVariables environmentVariables,
                            File outputDirectory,
                            ReportNameProvider reportNameProvider) {
        super(freemarker, environmentVariables, outputDirectory);
        this.reportNameProvider = reportNameProvider;
    }

    public void generateReportsFor(TestOutcomes testOutcomes) throws IOException {

        Stopwatch stopwatch = Stopwatch.started();

        for (TestTag tag : testOutcomes.getTags()) {
            generateTagReport(testOutcomes, reportNameProvider, tag);
        }

        LOGGER.trace("Tag reports generated: {} ms", stopwatch.stop());
    }

    Set<String> tagReportTally = Sets.newConcurrentHashSet();

    void generateTagReport(TestOutcomes testOutcomes, ReportNameProvider reportName, TestTag tag) throws IOException {

        String tagName = reportName.getContext() + ":" + tag;

        if (!tagReportTally.contains(tagName)) {
            tagReportTally.add(tagName);
            TestOutcomes testOutcomesForTag = testOutcomes.withTag(tag);
            Map<String, Object> context = freemarker.getBuildContext(testOutcomesForTag, reportName, true);
            context.put("report", ReportProperties.forTagResultsReport());
            context.put("currentTagType", tag.getType());
            context.put("currentTag", tag);

            String csvReport = reportName.forCSVFiles().forTag(tag);
            context.put("csvReport", csvReport);

            AddBreadcrumbs.forRequirementsTag(context, testOutcomes, tag);

            String report = reportName.forTag(tag);
            generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, report);
            generateCSVReportFor(testOutcomesForTag, csvReport);

            if (shouldGenerateLinkableReportsFor(tag, reportName)) {
                String linkableReport = reportName.inLinkableForm().forTag(tag);
                generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, linkableReport);
            }
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
}

package net.thucydides.core.reports.html;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;

import java.io.File;
import java.io.IOException;
import java.util.*;
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

        private final Inflector inflection = Inflector.getInstance();

        TagReportBuilder(TestOutcomes testOutcomes) {
            this.testOutcomes = testOutcomes;
        }

        public Set<ReportingTask> using(final FreemarkerContext freemarker,
                                        final EnvironmentVariables environmentVariables,
                                        final File outputDirectory,
                                        final ReportNameProvider reportName,
                                        final List<TestTag> allTags,
                                        final List<String> knownRequirementReportNames) {

            Set<TestTag> reportedTags = new HashSet<>(testOutcomes.getTags());

            Set<TestTag> reportedFlagTags = testOutcomes.getFlags()
                    .stream()
                    .map(flag -> TestTag.withName(inflection.of(flag.getMessage()).asATitle().toString()).andType("flag"))
                    .collect(Collectors.toSet());

            Set<TestTag> allReportedTags = concat(reportedTags, reportedFlagTags);

            return allReportedTags
                    .stream()
                    .filter(tag -> noReportHasBeenGeneratedFor(reportName, knownRequirementReportNames, tag))
                    .map(tag -> new TagReportingTask(
                            freemarker,
                            environmentVariables,
                            outputDirectory,
                            reportName,
                            reportName.forTag(tag),
                            tag,
                            allTags,
//                            concat(allTags, tag),
                            testOutcomes))
                    .collect(Collectors.toSet());

        }

        private boolean noReportHasBeenGeneratedFor(ReportNameProvider reportName, List<String> knownRequirementReportNames, TestTag tag) {
            return !knownRequirementReportNames.contains(reportName.forTag(tag));
        }

        private List<TestTag> concat(List<TestTag> allTags, TestTag tag) {
            if (allTags.contains(tag)) {
                return allTags;
            }
            List<TestTag> tags = new ArrayList(allTags);
            tags.add(tag);
            return tags;
        }

        private Set<TestTag> concat(Set<TestTag> setA, Set<TestTag> setB) {
            Set<TestTag> concatenatedTags = new HashSet<>(setA);
            concatenatedTags.addAll(setB);
            return concatenatedTags;
        }
    }
}

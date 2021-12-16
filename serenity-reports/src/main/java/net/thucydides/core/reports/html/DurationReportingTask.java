package net.thucydides.core.reports.html;

import com.google.common.base.Objects;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DurationReportingTask extends BaseReportingTask implements ReportingTask {

    private static final String TEST_OUTCOME_TEMPLATE_PATH = "freemarker/outcomes-with-result.ftl";

    final FreemarkerContext freemarker;
    final EnvironmentVariables environmentVariables;
    final File outputDirectory;
    final TestOutcomes testOutcomes;
    final ReportNameProvider reportNameProvider;
    final TestTag tag;
    final String duration;
    final String reportName;

    public DurationReportingTask(FreemarkerContext freemarker,
                                 EnvironmentVariables environmentVariables,
                                 File outputDirectory,
                                 TestOutcomes testOutcomes,
                                 ReportNameProvider reportNameProvider,
                                 TestTag tag,
                                 String duration) {
        super(freemarker, environmentVariables, outputDirectory);
        this.freemarker = freemarker;
        this.environmentVariables = environmentVariables;
        this.outputDirectory = outputDirectory;
        this.testOutcomes = testOutcomes;
        this.reportNameProvider = reportNameProvider;
        this.tag = tag;
        this.duration = duration;
        this.reportName = reportNameProvider.withPrefix(tag).forTag(TestTag.withName(duration).andType("Duration"));

    }

    @Override
    public void generateReports() throws IOException {
        Map<String, Object> context = freemarker.getBuildContext(testOutcomes, reportNameProvider, true);
        context.put("report", ReportProperties.forTestResultsReport());
        context.put("currentTagType", tag.getType());
        context.put("currentTag", tag);

        String csvReport = reportNameProvider.forCSVFiles().forTag(TestTag.withName(duration).andType("Duration"));
        context.put("csvReport", csvReport);
        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, reportName);
        generateCSVReportFor(testOutcomes, csvReport);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DurationReportingTask that = (DurationReportingTask) o;
        return Objects.equal(reportName, that.reportName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reportName);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DurationReportingTask{");
        sb.append("reportName='").append(reportName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String reportName() {
        return reportName;
    }
}

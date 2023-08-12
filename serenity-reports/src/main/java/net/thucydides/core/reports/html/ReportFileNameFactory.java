package net.thucydides.core.reports.html;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.steps.TestSourceType;

public class ReportFileNameFactory {

    private static final String DEFAULT_ACCEPTANCE_TEST_REPORT = "freemarker/default.ftl";
    private static final String DEFAULT_ACCEPTANCE_TEST_SCREENSHOT = "freemarker/screenshots.ftl";

    public static String getAcceptanceTestReportTemplateName(TestOutcome testOutcome) {
        if (TestSourceType.TEST_SOURCE_JUNIT5.getValue().equals(testOutcome.getTestSource())) {
            return DEFAULT_ACCEPTANCE_TEST_REPORT;
        }
        return DEFAULT_ACCEPTANCE_TEST_REPORT;
    }

    public static String getAcceptanceTestReportScreenshotName(TestOutcome testOutcome) {
        if (TestSourceType.TEST_SOURCE_JUNIT5.getValue().equals(testOutcome.getTestSource())) {
            return DEFAULT_ACCEPTANCE_TEST_SCREENSHOT;
        }
        return DEFAULT_ACCEPTANCE_TEST_SCREENSHOT;
    }
}

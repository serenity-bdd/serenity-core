package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;


@JsonIgnoreProperties({"testResult", "htmlReport", "reportName", "screenshotReportName", "descriptionText",
        "screenshots", "screenshotAndHtmlSources","qualifiedMethodName","testCase", "qualified","unqualified",
        "environmentVariables", "overallStability", "statistics",
        "recentStability", "recentTestRunCount", "recentPassCount", "recentFailCount", "recentPendingCount", "originalTestFailureCause",
        "dataDriven", "stepCount", "nestedStepCount", "successCount", "failureCount", "errorCount", "errorMessage",
        "ignoredCount", "skippedOrIgnoredCount", "skippedCount", "pendingCount",
        "titleWithLinks", "testCount", "startTimeNotDefined", "completeName",
        "flattenedTestSteps", "leafTestSteps", "formattedIssues", "issueKeys",
        "success","error","failure","pending","skipped",
        "path","pathId","storyTitle",
        "durationInSeconds", "videoLinks", "implementedTestCount", "exampleFields", "dataDrivenSampleScenario"})
@JsonInclude(NON_EMPTY)
public abstract class JSONTestOutcomeMixin {
    public JSONTestOutcomeMixin(@JsonProperty("name") String methodName) {
    }

    @JsonProperty("name")
    public abstract String getMethodName();
}

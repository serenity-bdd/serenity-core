package net.thucydides.core.reports.html;

import net.thucydides.core.model.TestResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResultIconFormatter {

    private final Map<TestResult, String> resultIcons = new HashMap();
    {
        resultIcons.put(TestResult.COMPROMISED, "<a href='${report}#beforetable'><i class='bi bi-slash-circle-fill ${iconStyle} ${qualifier}' title='${result}'></i></a>");
        resultIcons.put(TestResult.ERROR,  "<a href='${report}#beforetable'><i class='bi bi-exclamation-triangle-fill ${iconStyle} ${qualifier}' title='${result}'></i></a>");
        resultIcons.put(TestResult.FAILURE, "<a href='${report}#beforetable'><i class='bi bi-x-circle-fill ${iconStyle} ${qualifier}' title='${result}'></i></a>");
        resultIcons.put(TestResult.SUCCESS, "<a href='${report}#beforetable'><i class='bi bi-check-circle-fill ${iconStyle} ${qualifier}' title='${result}'></i></a>");
        resultIcons.put(TestResult.PENDING, "<a href='${report}#beforetable'><i class='bi bi-hourglass-top ${iconStyle} ${qualifier}' title='${result}'></i></a>");
        resultIcons.put(TestResult.ABORTED, "<a href='${report}#beforetable'><i class='bi bi-exclamation-octagon-fill ${iconStyle} ${qualifier}' title='${result}'></i></a>");
        resultIcons.put(TestResult.IGNORED, "<a href='${report}#beforetable'><i class='bi bi-slash-circle ${iconStyle} ${qualifier}' title='${result}'></i></a>");
        resultIcons.put(TestResult.SKIPPED, "<a href='${report}#beforetable'><i class='bi bi-skip-forward ${iconStyle} ${qualifier}' title='${result}'></i></a>");
        resultIcons.put(TestResult.UNDEFINED, "<i class='bi bi-pause-circle ${iconStyle} ${qualifier}' title='No test has been implemented yet'></i>");
    }

    private final Map<TestResult, String> resultIconStyles = new HashMap();
    {
        resultIconStyles.put(TestResult.COMPROMISED, "compromised-icon");
        resultIconStyles.put(TestResult.ERROR, "error-icon");
        resultIconStyles.put(TestResult.FAILURE, "failure-icon");
        resultIconStyles.put(TestResult.SUCCESS, "success-icon");
        resultIconStyles.put(TestResult.PENDING, "pending-icon");
        resultIconStyles.put(TestResult.ABORTED, "aborted-icon");
        resultIconStyles.put(TestResult.IGNORED, "ignored-icon");
        resultIconStyles.put(TestResult.SKIPPED, "skipped-icon");
        resultIconStyles.put(TestResult.UNDEFINED, "undefined-icon");
    }

    private String qualifier = "";

    public ResultIconFormatter inLarge() {
        this.qualifier = "fa-2x";
        return this;
    }

    public ResultIconFormatter inExtraLarge() {
        this.qualifier = "fa-3x";
        return this;
    }

    public String forResult(String result) {
        return forResult(TestResult.valueOf(result), "#");
    }

    public String forResult(TestResult result) {
        return forResult(result,"#");
    }
    public String forResult(TestResult result, String htmlReport) {

        TestResult testResult = Optional.ofNullable(result).orElse(TestResult.PENDING);
        return resultIcons.get(testResult)
                .replace("${report}", htmlReport)
                .replace("${iconStyle}", resultIconStyles.get(testResult))
                .replace("${qualifier}", qualifier)
                .replace("${result}", testResult.toString());
    }

    public String colorFor(TestResult result) {
        return resultIconStyles.get(Optional.ofNullable(result).orElse(TestResult.PENDING));
    }

}

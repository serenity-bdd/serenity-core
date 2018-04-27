package net.thucydides.core.reports.html;

import net.thucydides.core.model.TestResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResultIconFormatter {

    private final Map<TestResult, String> resultIcons = new HashMap();
    {
        resultIcons.put(TestResult.COMPROMISED, "<i class='fa fa-chain-broken ${iconStyle} ${qualifier}' title='${result}'></i>");
        resultIcons.put(TestResult.ERROR,  "<i class='fa fa-exclamation-triangle ${iconStyle} ${qualifier}' title='${result}'></i>");
        resultIcons.put(TestResult.FAILURE, "<i class='fa fa-times-circle ${iconStyle} ${qualifier}' title='${result}'></i>");
        resultIcons.put(TestResult.SUCCESS, "<i class='fa fa-check-square-o ${iconStyle} ${qualifier}' title='${result}'></i>");
        resultIcons.put(TestResult.PENDING, "<i class='fa fa-square-o ${iconStyle} ${qualifier}' title='${result}'></i>");
        resultIcons.put(TestResult.IGNORED, "<i class='fa fa-ban ${iconStyle} ${qualifier}' title='${result}'></i>");
        resultIcons.put(TestResult.SKIPPED, "<i class='fa fa-fast-forward ${iconStyle} ${qualifier}' title='${result}'></i>");
        resultIcons.put(TestResult.UNDEFINED, "<i class='fa-exclamation ${iconStyle} ${qualifier}' title='${result}'></i>");
    }

    private final Map<TestResult, String> resultIconStyles = new HashMap();
    {
        resultIconStyles.put(TestResult.COMPROMISED, "compromised-icon");
        resultIconStyles.put(TestResult.ERROR, "error-icon");
        resultIconStyles.put(TestResult.FAILURE, "failure-icon");
        resultIconStyles.put(TestResult.SUCCESS, "success-icon");
        resultIconStyles.put(TestResult.PENDING, "pending-icon");
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
        return forResult(TestResult.valueOf(result));
    }

    public String forResult(TestResult result) {

        TestResult testResult = Optional.ofNullable(result).orElse(TestResult.PENDING);
        return resultIcons.get(testResult)
                .replace("${iconStyle}", resultIconStyles.get(testResult))
                .replace("${qualifier}", qualifier)
                .replace("${result}", testResult.toString());
    }

    public String colorFor(TestResult result) {
        return resultIconStyles.get(Optional.ofNullable(result).orElse(TestResult.PENDING));
    }

}

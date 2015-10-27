package net.thucydides.core.reports.html;

import net.thucydides.core.model.TestResult;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class ResultIconFormatter {
    private final Map<TestResult, String> resultIcons = new HashMap<>();
    {
        resultIcons.put(TestResult.ERROR, "fa-exclamation-triangle");
        resultIcons.put(TestResult.FAILURE, "fa-times-circle");
        resultIcons.put(TestResult.SUCCESS, "fa-check-square-o");
        resultIcons.put(TestResult.PENDING, "fa-spinner");
        resultIcons.put(TestResult.IGNORED, "fa-eye-slash");
        resultIcons.put(TestResult.SKIPPED, "fa-fast-forward");
        resultIcons.put(TestResult.UNDEFINED, "fa-exclamation");
    }

    private final Map<TestResult, String> resultIconStyles = new HashMap<>();
    {
        resultIconStyles.put(TestResult.ERROR, "error-icon");
        resultIconStyles.put(TestResult.FAILURE, "failure-icon");
        resultIconStyles.put(TestResult.SUCCESS, "success-icon");
        resultIconStyles.put(TestResult.PENDING, "pending-icon");
        resultIconStyles.put(TestResult.IGNORED, "ignored-icon");
        resultIconStyles.put(TestResult.SKIPPED, "skipped-icon");
        resultIconStyles.put(TestResult.UNDEFINED, "undefined-icon");
    }

    private final static String HTML_ICON = "<i class='fa %s %s %s' title='%s'></i>";

    private String qualifier = "";

    public ResultIconFormatter inLarge() {
        this.qualifier = "fa-2x";
        return this;
    }

    public ResultIconFormatter inExtraLarge() {
        this.qualifier = "fa-3x";
        return this;
    }

    public String forResult(TestResult result) {
        return format(HTML_ICON, resultIcons.get(result), resultIconStyles.get(result), qualifier, result);
    }

    public String colorFor(TestResult result) {
        return resultIconStyles.get(result);
    }

}

package net.thucydides.core.model;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.formatters.ReportFormatter;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;


import java.util.HashMap;
import java.util.Map;

public class TitleBuilder {

    private final boolean qualified;
    private final TestOutcome testOutcome;
    private final IssueTracking issueTracking;
    private final EnvironmentVariables environmentVariables;
    private final boolean showContext;

    private final static Map<String, String> FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS = new HashMap();
    static {
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("chrome", "chrome");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("firefox", "firefox");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("safari", "safari");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("opera", "opera");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("ie", "internet-explorer");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("edge", "edge");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("phantomjs", "snapchat-ghost");

        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("linux", "linux");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("mac", "apple");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("windows", "windows");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("android", "android");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("iphone", "apple");
    }

    public TitleBuilder(TestOutcome testOutcome, IssueTracking issueTracking, EnvironmentVariables environmentVariables, boolean qualified, boolean showContext) {
        this.testOutcome = testOutcome;
        this.qualified = qualified;
        this.issueTracking = issueTracking;
        this.environmentVariables = environmentVariables;
        this.showContext = showContext;
    }

    public TitleBuilder(TestOutcome testOutcome, IssueTracking issueTracking, EnvironmentVariables environmentVariables, boolean qualified) {
        this(testOutcome, issueTracking, environmentVariables, qualified, false);
    }

    public TitleBuilder withContext() {
        return new TitleBuilder(testOutcome, issueTracking, environmentVariables, qualified, true);
    }

    public String getTitleWithLinks() {
        String title = Inflector.getInstance().of(getTitle()).toString();
        return getFormatter().addLinks(title);
    }

    public String getTitle() {
        return contextFor(testOutcome) + testOutcome.getTitle(qualified);
    }

    private ReportFormatter getFormatter() {
        return new ReportFormatter(issueTracking);
    }

    private String contextFor(TestOutcome testOutcome) {
        if (!showContext) { return ""; }

        if (!ThucydidesSystemProperty.THUCYDIDES_DISPLAY_CONTEXT.booleanFrom(environmentVariables, true)) {
            return "";
        }

        String context = testOutcome.getContext();
        if (context == null) {
            return "";
        }

        if (FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.containsKey(context.toLowerCase())) {
            return String.format("<i class=\"fa fa-%s\" aria-hidden=\"true\"></i> | ",
                    FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.get(context.toLowerCase()));
        }
        return context.toUpperCase() + " | ";
    }

}

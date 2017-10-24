package net.thucydides.core.model;

import com.google.common.collect.ImmutableMap;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.reports.html.Formatter;
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

    private final static Map<String, String> FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS = new HashMap<>();
    static {
        FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS.put("chrome", "<i class=\"fa fa-chrome\" aria-hidden=\"true\"></i>");
        FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS.put("firefox", "<i class=\"fa fa-firefox\" aria-hidden=\"true\"></i>");
        FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS.put("safari", "<i class=\"fa fa-safari\" aria-hidden=\"true\"></i>");
        FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS.put("opera", "<i class=\"fa fa-opera\" aria-hidden=\"true\"></i>");
        FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS.put("ie", "<i class=\"fa fa-ie\" aria-hidden=\"true\"></i>");
        FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS.put("phantomjs", "<i class=\"fa fa-snapchat-ghost\" aria-hidden=\"true\"></i>");

        FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS.put("linux", "<i class=\"fa fa-linux\" aria-hidden=\"true\"></i>");
        FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS.put("mac", "<i class=\"fa fa-apple\" aria-hidden=\"true\"></i>");
        FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS.put("windows", "<i class=\"fa fa-windows\" aria-hidden=\"true\"></i>");
        FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS.put("android", "<i class=\"fa fa-android\" aria-hidden=\"true\"></i>");
        FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS.put("iphone", "<i class=\"fa fa-apple\" aria-hidden=\"true\"></i>");
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

    private Formatter getFormatter() {
        return new Formatter(issueTracking);
    }

    private String contextFor(TestOutcome testOutcome) {
        if (!showContext) { return ""; }

        if (!ThucydidesSystemProperty.THUCYDIDES_DISPLAY_CONTEXT.booleanFrom(environmentVariables, true)) {
            return "";
        }

        if (testOutcome.getContext() == null) {
            return "";
        }

        if (FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS.containsKey(testOutcome.getContext().toLowerCase())) {
            return FONTAWESOME_ICONS_FOR_COMMON_CONTEXTS.get(testOutcome.getContext().toLowerCase()) + " | ";
        }
        return testOutcome.getContext().toUpperCase() + " | ";
    }

}

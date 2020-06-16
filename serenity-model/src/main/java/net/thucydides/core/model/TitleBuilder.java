package net.thucydides.core.model;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.formatters.ReportFormatter;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;

public class TitleBuilder {

    private final boolean qualified;
    private final TestOutcome testOutcome;
    private final IssueTracking issueTracking;
    private final EnvironmentVariables environmentVariables;
    private final boolean showContext;

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

        return ContextIcon.forOutcome(testOutcome);
    }

}

package net.thucydides.model.domain;

import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.formatters.ReportFormatter;
import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.util.Inflector;

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

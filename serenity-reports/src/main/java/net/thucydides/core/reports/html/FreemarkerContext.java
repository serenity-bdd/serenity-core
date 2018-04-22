package net.thucydides.core.reports.html;

import net.serenitybdd.core.buildinfo.BuildInfoProvider;
import net.serenitybdd.core.buildinfo.BuildProperties;
import net.serenitybdd.core.reports.styling.TagStylist;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.NumericalFormatter;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.ReportOptions;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import net.thucydides.core.util.VersionProvider;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

import static net.thucydides.core.reports.html.HtmlReporter.TIMESTAMP_FORMAT;
import static net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;

/**
 * Created by john on 21/06/2016.
 */
public class FreemarkerContext {

    private final EnvironmentVariables environmentVariables;
    private final RequirementsService requirements;
    private final IssueTracking issueTracking;
    private final String relativeLink;
    private final BuildProperties buildProperties;
    private final TestTag parentTag;


    public FreemarkerContext(EnvironmentVariables environmentVariables,
                             RequirementsService requirements,
                             IssueTracking issueTracking,
                             String relativeLink,
                             TestTag parentTag) {
        this.environmentVariables = environmentVariables;
        this.requirements = requirements;
        this.issueTracking = issueTracking;
        this.relativeLink = relativeLink;
        buildProperties = new BuildInfoProvider(environmentVariables).getBuildProperties();
        this.parentTag = parentTag;
    }


    public FreemarkerContext(EnvironmentVariables environmentVariables,
                             RequirementsService requirements,
                             IssueTracking issueTracking,
                             String relativeLink) {
        this(environmentVariables, requirements, issueTracking,relativeLink, TestTag.EMPTY_TAG);
    }

    public Map<String, Object> getBuildContext(TestOutcomes testOutcomesForTagType,
                                             ReportNameProvider reportName,
                                             boolean useFiltering) {
        Map<String, Object> context = new HashMap();
        TagFilter tagFilter = new TagFilter(environmentVariables);
        context.put("testOutcomes", testOutcomesForTagType);
        context.put("allTestOutcomes", testOutcomesForTagType.getRootOutcomes());
        if (useFiltering) {
            context.put("tagTypes", tagFilter.filteredTagTypes(testOutcomesForTagType.getTagTypes()));
        } else {
            context.put("tagTypes", testOutcomesForTagType.getTagTypes());
        }
        context.put("currentTag", TestTag.EMPTY_TAG);
        context.put("parentTag", parentTag);
        context.put("reportName", reportName);

        context.put("absoluteReportName", new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirements));

        context.put("reportOptions", new ReportOptions(environmentVariables));
        context.put("timestamp", timestampFrom(new DateTime()));
        context.put("requirementTypes", requirements.getRequirementTypes());
        addFormattersToContext(context);


        VersionProvider versionProvider = new VersionProvider(environmentVariables);
        context.put("serenityVersionNumber", versionProvider.getVersion());
        context.put("buildNumber", versionProvider.getBuildNumberText());
        context.put("build", buildProperties);

        return context;
    }

    private void addFormattersToContext(final Map<String, Object> context) {
        Formatter formatter = new Formatter();
        context.put("formatter", formatter);
        context.put("formatted", new NumericalFormatter());
        context.put("inflection", Inflector.getInstance());
        context.put("styling", TagStylist.from(environmentVariables));
        context.put("relativeLink", relativeLink);
        context.put("reportOptions", new ReportOptions(environmentVariables));
    }


    protected String timestampFrom(DateTime startTime) {
        return startTime == null ? "" : startTime.toString(TIMESTAMP_FORMAT);
    }

    public FreemarkerContext withParentTag(TestTag knownTag) {
        return new FreemarkerContext(environmentVariables, requirements, issueTracking, relativeLink, knownTag);
    }
}

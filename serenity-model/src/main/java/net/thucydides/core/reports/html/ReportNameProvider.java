package net.thucydides.core.reports.html;

import net.serenitybdd.core.SerenitySystemProperties;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.model.Release;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.model.formatters.ReportFormatter;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.NameConverter;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ReportNameProvider {

    private final Optional<String> context;
    private final ReportNamer reportNamer;
    private final RequirementsService requirementsService;
    private EnvironmentVariables environmentVariables;


    public ReportNameProvider(Optional<String> context, ReportNamer reportNamer) {
        this(context, reportNamer,Injectors.getInjector().getInstance(RequirementsService.class));
    }

    public ReportNameProvider(Optional<String> context, ReportNamer reportNamer, RequirementsService requirementsService) {
        this.context = context;
        this.reportNamer = reportNamer;
        this.requirementsService = requirementsService;
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    public ReportNameProvider(Optional<String> context, ReportType reportType, RequirementsService requirementsService) {
        this(context, ReportNamer.forReportType(reportType), requirementsService);
    }

    public ReportNameProvider getWithoutContext() {
        return new ReportNameProvider(NO_CONTEXT, reportNamer, requirementsService);
    }

    public final static Optional<String> NO_CONTEXT = Optional.empty();

    public ReportNameProvider() {
        this(NO_CONTEXT, ReportType.HTML);
    }

    public ReportNameProvider(String context) {
       this(Optional.ofNullable(context), ReportNamer.forReportType(ReportType.HTML), Injectors.getInjector().getInstance(RequirementsService.class));

    }

    public ReportNameProvider inContext(String context) {
        Optional<String> newContext = (isNotEmpty(context)) ? Optional.of(context) : NO_CONTEXT;
        return new ReportNameProvider(newContext, reportNamer, requirementsService);
    }

    protected ReportNameProvider(Optional<String> context, ReportType type) {
        this(context, ReportNamer.forReportType(type), Injectors.getInjector().getInstance(RequirementsService.class));
    }

    public String getContext() {
        return context.orElse("");
     }

    public ReportNameProvider forCSVFiles() {
        return new ReportNameProvider(this.context, ReportType.CSV);
    }

    public String forTestResult(String result) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "result_" + result);
    }

    public String forTag(String tag) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + tag.toLowerCase());
    }

    public String forErrorType(String errorType) {
        return reportNamer.getNormalizedTestNameFor("errortype_" + errorType.toLowerCase());
    }

    public String forTag(TestTag tag) {
        if (tag.getType().equalsIgnoreCase("issue")) {
            IssueTracking issueTracking = new SystemPropertiesIssueTracking(environmentVariables);
            ReportFormatter reportFormatter = new ReportFormatter(issueTracking, environmentVariables);
            return reportFormatter.asIssueLink(tag.getName());
        } else {
            return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + tag.getType().toLowerCase() + "_" + tag.getName().toLowerCase());
        }
    }


    public String forTagType(String tagType) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "tagtype_" + tagType.toLowerCase());
    }

    public String forRequirementType(String tagType) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "requirement_type_" + tagType.toLowerCase());
    }

    public ReportNameProvider withPrefix(String prefix) {
        return new ReportNameProvider(prefix);
    }

    public ReportNameProvider withPrefix(TestTag tag) {
        if (tag.equals(TestTag.EMPTY_TAG)) {
            return new ReportNameProvider(NO_CONTEXT, reportNamer, requirementsService);
        } else {
            return new ReportNameProvider(Optional.of(tag.getType().toLowerCase() + ":" + tag.getName().toLowerCase()),
                                          reportNamer,
                                          requirementsService);
        }
    }

    private String prefixUsing(Optional <String> context) {
        if (context.isPresent() && isNotEmpty(getContext())) {
            return "context_" + NameConverter.underscore(context.get()) + "_";
        } else {
            return "";
        }
    }

    public String forRequirement(Requirement requirement) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + requirement.getType() + "_" + requirement.qualifiedName());
    }

    public String forRequirement(TestTag tag) {
        return forRequirement(tag.getName(), tag.getType());
    }

    public String forRequirementOrTag(TestTag tag) {
        return (requirementsService.isRequirementsTag(tag))
                ? forRequirement(tag.getName(), tag.getType())
                : forTag(tag);
    }

    public String forRequirement(String requirementName, String requirementType) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + requirementType + "_" + requirementName);
    }

    public String forRelease(Release release) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "release_" + release.getName());
    }

    public String forRelease(String releaseName) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "release_" + releaseName);
    }

    public ReportNameProvider inLinkableForm() {
        return new ReportNameProvider(context, reportNamer.withNoCompression(), requirementsService);
    }

    @Override
    public String toString() {
        return "ReportNameProvider{" +
                "context=" + context +
                '}';
    }
}

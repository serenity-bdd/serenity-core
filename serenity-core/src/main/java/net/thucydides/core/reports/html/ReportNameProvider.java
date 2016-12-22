package net.thucydides.core.reports.html;

import com.google.common.base.Optional;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.Release;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.NameConverter;

public class ReportNameProvider {

    private final Optional<String> context;
    private final ReportNamer reportNamer;
    private final RequirementsService requirementsService;


    public ReportNameProvider(Optional<String> context, ReportNamer reportNamer, RequirementsService requirementsService) {
        this.context = context;
        this.reportNamer = reportNamer;
        this.requirementsService = requirementsService;
    }

    public final static Optional<String> NO_CONTEXT = Optional.absent();

    public ReportNameProvider() {
        this(NO_CONTEXT, ReportType.HTML);
    }

    public ReportNameProvider(String context) {
       this(Optional.fromNullable(context), ReportType.HTML, Injectors.getInjector().getInstance(RequirementsService.class));

    }

    protected ReportNameProvider(Optional<String> context, ReportType type) {
        this(context, type, Injectors.getInjector().getInstance(RequirementsService.class));
    }

    public ReportNameProvider(Optional<String> context, ReportType type, RequirementsService requirementsService) {
        this.context = context;
        this.reportNamer = ReportNamer.forReportType(type);
        this.requirementsService = requirementsService;
    }

    public String getContext() {
        if(context.isPresent()) {
            return context.get();
        } else {
            return "";
        }
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

    public String forTag(TestTag tag) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + tag.getType().toLowerCase() + "_" + tag.getName().toLowerCase());
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
        if (context.isPresent()) {
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
}

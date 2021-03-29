package serenitymodel.net.thucydides.core.tags;

import serenitymodel.net.serenitybdd.core.collect.NewList;
import serenitymodel.net.thucydides.core.guice.Injectors;
import serenitymodel.net.thucydides.core.model.TestOutcome;
import serenitymodel.net.thucydides.core.model.TestTag;
import serenitymodel.net.thucydides.core.requirements.RequirementsService;
import serenitymodel.net.thucydides.core.requirements.model.Requirement;
import serenitymodel.net.thucydides.core.requirements.reports.RequirementsOutcomes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BreadcrumbTagFilter {

    private final RequirementsService requirementsService;

    public BreadcrumbTagFilter(RequirementsService requirementsService) {
        this.requirementsService = requirementsService;
    }

    public BreadcrumbTagFilter() {
        this(Injectors.getInjector().getInstance(RequirementsService.class));
    }

    public boolean isRequirementTag(TestTag tag) {
        return requirementsService.isRequirementsTag(tag);
    }

    public List<TestTag> getRequirementBreadcrumbsFrom(Collection<TestTag> tags) {
        List<TestTag> requirementTypeTags = new ArrayList<>();
        for(String requirementType : requirementsService.getRequirementTypes()) {
            requirementTagOfType(requirementType).in(tags).ifPresent(requirementTypeTags::add);
        }
        return requirementTypeTags;
    }

    public List<TestTag> getRequirementBreadcrumbsFrom(TestTag tag) {
        List<TestTag> requirementTypeTags = new ArrayList<>();
        java.util.Optional<Requirement> displayedRequirement = requirementsService.getRequirementFor(tag);

        return displayedRequirement.isPresent()
                ? NewList.of(displayedRequirement.get().asTag()) : NewList.<TestTag>of();
    }

    public List<TestTag> getRequirementBreadcrumbsFrom(TestOutcome testOutcome) {

        List<Requirement> ancestors = requirementsService.getAncestorRequirementsFor(testOutcome);

        if (ancestors == null) { return new ArrayList<>(); }

        return ancestors.stream().map(Requirement::asTag).collect(Collectors.toList());
    }

    private RequirementTagFilter requirementTagOfType(String requirementType) {
        return new RequirementTagFilter(requirementType);
    }

    public List<TestTag> getRequirementBreadcrumbsFrom(RequirementsOutcomes requirementsOutcomes) {
        List<TestTag> breadcrumbs = new ArrayList<>();
        if (requirementsOutcomes.getParentRequirement().isPresent()) {
           breadcrumbs.add(requirementsOutcomes.getParentRequirement().get().asTag());
        }
        return breadcrumbs;
    }

    public static class RequirementTagFilter {
        private String requirementType;
        public RequirementTagFilter(String requirementType) {
            this.requirementType = requirementType;
        }

        public Optional<TestTag> in(Collection<TestTag> tags) {
            for(TestTag tag : tags) {
                if (tag.getType().equals(requirementType)) {
                    return Optional.of(tag);
                }
            }
            return Optional.empty();
        }
    }
}

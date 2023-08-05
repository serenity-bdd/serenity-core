package net.thucydides.model.tags;

import net.serenitybdd.model.di.ModelInfrastructure;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.requirements.RequirementsService;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.requirements.reports.RequirementsOutcomes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BreadcrumbTagFilter {

    private final RequirementsService requirementsService;

    public BreadcrumbTagFilter(RequirementsService requirementsService) {
        this.requirementsService = requirementsService;
    }

    public BreadcrumbTagFilter() {
        this(ModelInfrastructure.getRequirementsService());
    }

    public List<TestTag> getRequirementBreadcrumbsFrom(TestOutcome testOutcome) {
        List<TestTag> ancestors = new ArrayList<>();
        requirementsService.getParentRequirementFor(testOutcome).ifPresent(
                requirement -> {
                    ancestors.add(requirement.asTag());
                    requirementsService.getParentRequirementsOf(requirement).forEach(
                            parent -> ancestors.add(parent.asTag())
                    );
                }
        );
        return ancestors;
    }

    private Requirement last(List<Requirement> ancestors) {
        return (ancestors.isEmpty()) ? null : ancestors.get(ancestors.size() - 1);
    }

    private List<TestTag> tagsForParentRequirements(List<Requirement> parents) {
        return parents.stream().map(
                requirement -> TestTag.withName(requirement.getDisplayName()).andType(requirement.getType())
        ).collect(Collectors.toList());
    }


    public List<TestTag> getRequirementBreadcrumbsFrom(RequirementsOutcomes requirementsOutcomes) {
        List<TestTag> breadcrumbs = new ArrayList<>();
        if (requirementsOutcomes.getParentRequirement().isPresent()) {
            breadcrumbs.add(requirementsOutcomes.getParentRequirement().get().asTag());
        }
        return breadcrumbs;
    }
}

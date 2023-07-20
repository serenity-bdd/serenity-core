package net.thucydides.core.tags;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;

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
//        Optional<Requirement> parentRequirement = requirementsService.getParentRequirementFor(testOutcome);
//        if (parentRequirement.isPresent()) {
//            ancestors.add(parentRequirement.get().asTag());
//            requirementsService.getParentRequirementsOf(parentRequirement.get()).forEach(
//                    requirement -> ancestors.add(requirement.asTag())
//            );
////            return tagsForParentRequirements(ancestors);
//        } else {
//            return new ArrayList<>();
//        }
//        return ancestors;
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

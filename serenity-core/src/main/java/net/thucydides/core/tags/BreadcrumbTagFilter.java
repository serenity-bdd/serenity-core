package net.thucydides.core.tags;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;

import java.util.Collection;
import java.util.List;

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
        List<TestTag> requirementTypeTags = Lists.newArrayList();
        for(String requirementType : requirementsService.getRequirementTypes()) {
            requirementTypeTags.addAll(requirementTagOfType(requirementType).in(tags).asSet());

        }
        return requirementTypeTags;
    }

    private RequirementTagFilter requirementTagOfType(String requirementType) {
        return new RequirementTagFilter(requirementType);
    }

    public List<TestTag> getRequirementBreadcrumbsFrom(RequirementsOutcomes requirementsOutcomes) {
        List<TestTag> breadcrumbs = Lists.newArrayList();
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
            return Optional.absent();
        }
    }
}

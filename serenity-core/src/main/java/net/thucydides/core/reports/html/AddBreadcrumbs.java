package net.thucydides.core.reports.html;

import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.tags.BreadcrumbTagFilter;

import java.util.List;
import java.util.Map;

public class AddBreadcrumbs {
    public static void forRequirementsTag(Map<String, Object> context, TestOutcomes testOutcomes, TestTag tag) {
        BreadcrumbTagFilter breadcrumbTagFilter = new BreadcrumbTagFilter();
        if (breadcrumbTagFilter.isRequirementTag(tag)) {
            List<TestTag> breadcrumbs = breadcrumbTagFilter.getRequirementBreadcrumbsFrom(testOutcomes.getTags());
            context.put("breadcrumbs", breadcrumbs);
        }
    }
}
package net.thucydides.core.reports.html;

import com.google.common.base.Splitter;
import net.thucydides.core.model.PathElement;
import net.thucydides.core.model.RequirementCache;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.Requirement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Breadcrumbs {

    public static BreadcrumbsBuilder forRequirementsTag(TestTag tag) {
        return new BreadcrumbsBuilder(tag);
    }

    public static List<TestTag> forParentRequirements(List<Requirement> parents) {
        return parents.stream().map(Requirement::asTag).collect(Collectors.toList());
    }

    public static class BreadcrumbsBuilder {
        private final TestTag tag;

        public BreadcrumbsBuilder(TestTag tag) {
            this.tag = tag;
        }

        public List<TestTag> fromTagsIn(List<TestTag> allTags) {

            if (isAnOrphan(tag.getName())) {
                return new ArrayList<>();
            }

            String parentName = parentElementOf(tag.getName());
            Optional<TestTag> parentTag = parentTagNamed(parentName).from(allTags);

            if (!parentTag.isPresent()) {
                return new ArrayList<>();
            }

            List<TestTag> ancestorParents = removeTagFrom(allTags, parentTag.get());

            List<TestTag> ancestors = Breadcrumbs.forRequirementsTag(parentTag.get()).fromTagsIn(ancestorParents);

            ancestors.add(parentTag.get());

            return ancestors;
        }

        private List<TestTag> removeTagFrom(List<TestTag> allTags, TestTag testTag) {
            List<TestTag> trimmedTags = new ArrayList<>(allTags);
            trimmedTags.remove(testTag);
            return trimmedTags;
        }

        private boolean isAnOrphan(String name) {
            return elementsOf(name).size() == 1;
        }

        private List<String> elementsOf(String name) {
            return Splitter.on("/").splitToList(name);
        }

        private String parentElementOf(String name) {
            return elementsOf(name).get(0);
        }

        private ParentTagFinder parentTagNamed(String parentName) {
            return new ParentTagFinder(parentName);
        }

        private class ParentTagFinder {
            private final String parentName;

            public ParentTagFinder(String parentName) {
                this.parentName = parentName;
            }

            public Optional<TestTag> from(List<TestTag> allTags) {
                for (TestTag tag : allTags) {
                    if (tag.getName().equals(parentName) || tag.getName().endsWith("/" + parentName)) {
                        return Optional.of(tag);
                    }
                }
                return Optional.empty();
            }
        }
    }
}

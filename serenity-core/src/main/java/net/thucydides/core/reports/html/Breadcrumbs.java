package net.thucydides.core.reports.html;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import net.thucydides.core.model.TestTag;

import java.util.ArrayList;
import java.util.List;

public class Breadcrumbs {

    public static BreadcrumbsBuilder forRequirementsTag(TestTag tag) {
        return new BreadcrumbsBuilder(tag);
    }

    public static class BreadcrumbsBuilder {
        private final TestTag tag;

        public BreadcrumbsBuilder(TestTag tag) {
            this.tag = tag;
        }

        public List<TestTag> fromTagsIn(List<TestTag> allTags) {

            if (isAnOrphan(tag.getName())) { return new ArrayList<>(); }

            String parentName = parentElementOf(tag.getName());
            Optional<TestTag> parentTag = parentTagNamed(parentName).from(allTags);

            if (!parentTag.isPresent()) { return new ArrayList<>(); }

            List<TestTag> ancestors = Breadcrumbs.forRequirementsTag(parentTag.get()).fromTagsIn(allTags);
            ancestors.add(parentTag.get());

            return ancestors;
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
                for(TestTag tag : allTags) {
                    if (tag.getName().equals(parentName) || tag.getName().endsWith("/" + parentName)) {
                        return Optional.of(tag);
                    }
                }
                return Optional.absent();
            }
        }
    }
}
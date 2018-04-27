package net.thucydides.core.statistics.service;

import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.CoreTagProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Returns test tags based on the @WithTag, @WithTags and @WithTagValuesOf.
 * Since there is no implicit structure in tags declared this way, capabilities need to
 * be distinguished using a special 'capability' tag.
 */
public class AnnotationBasedTagProvider implements TagProvider, CoreTagProvider {

    public AnnotationBasedTagProvider() {
    }

    public Set<TestTag> getTagsFor(final TestOutcome testOutcome) {
        if (testOutcome.getTestCase() == null) {
            return Collections.emptySet();
        }
        List<TestTag> tags = TestAnnotations.forClass(testOutcome.getTestCase()).getTagsForMethod(testOutcome.getName());

        return new HashSet(tags);
    }
}

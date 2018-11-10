package net.thucydides.core.model;

import java.util.Collection;

public class TestTags {
    private Collection<TestTag> tags;

    public TestTags(Collection<TestTag> tags) {
        this.tags = tags;
    }

    public static TestTags of(Collection<TestTag> tags) {
        return new TestTags(tags);
    }

    public boolean containsTagMatchingOneOf(Collection<TestTag> specificTags) {
        return specificTags.stream().anyMatch( tag -> containsTagMatching(tag));
    }

    public boolean containsTagMatching(TestTag specificTag) {
        for (TestTag tag : tags) {
            if (specificTag.isAsOrMoreSpecificThan(tag)) {
                return true;
            }
        }
        return false;

    }
}
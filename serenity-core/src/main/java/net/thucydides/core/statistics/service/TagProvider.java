package net.thucydides.core.statistics.service;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;

import java.util.Set;

public interface TagProvider {

    /**
     * Returns the tags associated with a given test outcome.
     * @param testOutcome the outcome for a specific test
     * @return the tags associated with a given test outcome.
     */
    Set<TestTag> getTagsFor(final TestOutcome testOutcome);
}

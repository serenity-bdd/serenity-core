package net.thucydides.core.hamcrest;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Hamcrest matchers to compare XML structure and content.
 */
public class XMLMatchers {

    @Factory
    public static Matcher<String> isSimilarTo(final String expectedDocment, String... excludedNodes ) {
        return new XMLIsSimilarMatcher(expectedDocment, excludedNodes);
    }
}

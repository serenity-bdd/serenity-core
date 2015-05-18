package net.thucydides.core.matchers;

import net.thucydides.core.model.Screenshot;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * Does a list of screenshot objects contain a specified list of screenshot filenames?
 */
public class ScreenshotHasFilenamesMatcher extends TypeSafeMatcher<List<Screenshot>> {

    private final List<String> expectedFilenames;

    public ScreenshotHasFilenamesMatcher(final String... expectedFilenames) {
        this.expectedFilenames = Arrays.asList(expectedFilenames);
    }

    public boolean matchesSafely(final List<Screenshot> screenshots) {
        List<String> screenshotFilenames = extract(screenshots, on(Screenshot.class).getFilename());

        for(String expectedFilename : expectedFilenames) {
            if (!screenshotFilenames.contains(expectedFilename)) {
                return false;
            }
        }
        return true;
    }

    public void describeTo(Description description) {
        description.appendText("screenshots called ").appendText(Arrays.toString(expectedFilenames.toArray()));
    }

}

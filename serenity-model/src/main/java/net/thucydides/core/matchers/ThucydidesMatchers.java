package net.thucydides.core.matchers;

import net.thucydides.core.model.screenshots.Screenshot;

import org.hamcrest.Matcher;

import java.util.List;


public class ThucydidesMatchers {


    public static Matcher<List<Screenshot>> hasFilenames(String... screenshots) {
       return new ScreenshotHasFilenamesMatcher(screenshots);
    }
}

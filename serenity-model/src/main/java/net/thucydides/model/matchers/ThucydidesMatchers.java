package net.thucydides.model.matchers;

import net.thucydides.model.domain.screenshots.Screenshot;

import org.hamcrest.Matcher;

import java.util.List;


public class ThucydidesMatchers {


    public static Matcher<List<Screenshot>> hasFilenames(String... screenshots) {
       return new ScreenshotHasFilenamesMatcher(screenshots);
    }
}

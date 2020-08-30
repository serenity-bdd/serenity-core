package net.thucydides.core.model;

/**
 * Helper methods to deal with stories and related classes.
 */
public class Stories {

    /**
     * Find the Story defined directly or indirectly in a class.
     * It may be defined in the test case class using the @Story annotation, or it may be a class that represents
     * a story directly.
     *
     * @param testClass Typically a test class of some kind.
     */
    public static Story findStoryFrom(final Class<?> testClass) {
        if (storyIsDefinedIn(testClass)) {
            return storyFrom(testClass);
        } else {
            return Story.from(testClass);
        }
    }

    public static String reportFor(final Story story, final ReportType type) {
        ReportNamer reportNamer = ReportNamer.forReportType(type);
        return reportNamer.getNormalizedTestNameFor(story);
    }

    private static Story storyFrom(final Class<?> testClass) {
        Class<?> testedStoryClass = Story.testedInTestCase(testClass);
        if (testedStoryClass != null) {
            return Story.from(testedStoryClass);
        }
        return null;
    }

    private static boolean storyIsDefinedIn(final Class<?> testClass) {
         return (storyFrom(testClass) != null);
   }
}

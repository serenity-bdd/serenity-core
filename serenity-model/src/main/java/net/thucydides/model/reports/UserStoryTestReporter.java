package net.thucydides.model.reports;

import java.io.File;
import java.io.IOException;

/**
 * Generates an aggregate acceptance test report for each user story.
 * The class reads all the reports from the output directory and generates an aggregate report
 * summarizing the results using the generateReportsFor() method.
 */
public interface UserStoryTestReporter {

    /**
     * Where do report resources come from.
     * We don't need any resources for XML reports, so this does nothing by default.
     */
    void setResourceDirectory(final String resourceDirectoryPath);

    /**
     * Generates a set of user story reports from a given directory.
     */
    TestOutcomes generateReportsForTestResultsFrom(final File sourceDirectory) throws IOException;
    
    File getOutputDirectory();

    void setOutputDirectory(final File outputDirectory);

}

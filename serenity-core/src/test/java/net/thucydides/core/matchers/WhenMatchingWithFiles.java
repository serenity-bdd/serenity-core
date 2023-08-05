package net.thucydides.core.matchers;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static net.thucydides.model.matchers.FileMatchers.exists;
import static net.thucydides.model.util.TestResources.directoryInClasspathCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class WhenMatchingWithFiles {

    @Test
    public void should_check_if_file_exists() {
        File existingFile = new File(directoryInClasspathCalled("/reports"), "sample-report-1.xml");
        assertThat(existingFile, exists());
    }

    @Test
    public void should_check_if_file_does_not_exist() {
        try {
            File existingFile = new File(directoryInClasspathCalled("/reports"), "no-such-report.xml");
            assertThat(existingFile, exists());
        } catch (AssertionError expectedException ) {
            assertThat(expectedException.getMessage(), containsString("no-such-report.xml"));
            return;
        }
        Assert.fail();
 
    }
}

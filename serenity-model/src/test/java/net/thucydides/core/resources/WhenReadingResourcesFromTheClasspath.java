package net.thucydides.core.resources;

import net.thucydides.core.util.ExtendedTemporaryFolder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class WhenReadingResourcesFromTheClasspath {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_return_a_list_of_resources_on_the_classpath() {
        Pattern pattern = Pattern.compile(".*");
        Collection<String> resources = ResourceList.forResources("",pattern).list();
        assertThat(resources.isEmpty(), is(false));
    }

    @Test
    public void should_exclude_trailing_pom_files() {
        Pattern pattern = Pattern.compile(".*[\\\\/]resourcelist[\\\\/].*");
        Collection<String> resources = ResourceList.forResources("",pattern).list();
        //TODO: JDK
        // assertThat(resources, not(hasItem(endsWith("pom.xml"))));
    }
    @Test
    public void should_return_a_list_of_resources_in_a_given_package() {
        Pattern pattern = Pattern.compile(".*[\\\\/]resourcelist[\\\\/].*");
        Collection<String> resources = ResourceList.forResources("",pattern).list();
        assertThat(resources.size() ,greaterThan(0));
    }
    
    @Test
    public void should_return_a_list_of_resources_in_a_given_package_containing_matching_resources() {
        Pattern pattern = Pattern.compile(".*[\\\\/]resourcelist[\\\\/].*");
        Collection<String> resources = ResourceList.forResources("",pattern).list();
        assertThat(resources, hasItems(containsString("resourcelist"),endsWith("sample.css"),endsWith("sample.xsl")));
    }

    @Test
    public void should_return_a_list_of_resources_in_a_given_package_even_from_a_dependency() {
        Pattern pattern = Pattern.compile(".*/*.js");
        Collection<String> resources = ResourceList.forResources("",pattern).list();
        assertThat(resources.isEmpty(), is(false));
    }

    @Test
    public void should_transform_windows_source_path_into_relative_target_path() {

        String sourceResource = "C:\\Projects\\thucydides\\thucydides-report-resources\\target\\classes\\report-resources\\css\\core.css";

        String expectedTargetSubDirectory = "css";
        FileResources fileResource = FileResources.from("report-resources");
        String targetSubdirectory = fileResource.findTargetSubdirectoryFrom(sourceResource);
        assertThat(targetSubdirectory, is(expectedTargetSubDirectory));
    }

    @Test
    public void should_handle_a_nested_resource_directory() {

        String sourceResource = "C:\\Projects\\thucydides\\thucydides-report-resources\\target\\classes\\report-resources\\css\\core.css";

        String expectedTargetSubDirectory = "css";
        FileResources fileResource = FileResources.from("classes\\report-resources");
        String targetSubdirectory = fileResource.findTargetSubdirectoryFrom(sourceResource);
        assertThat(targetSubdirectory, is(expectedTargetSubDirectory));
    }


    @Test
    public void should_transform_unix_source_path_into_relative_target_path_without_subdirectory() {

        String sourceResource = "/Projects/thucydides/thucydides-report-resources/target/classes/report-resources/core.css";

        String expectedTargetSubDirectory = "";

        FileResources fileResource = FileResources.from("report-resources");
        assertThat(fileResource.findTargetSubdirectoryFrom(sourceResource), is(expectedTargetSubDirectory));
    }

    @Test
     public void should_handle_nested_subdirectories_in_unix() {

         String sourceResource = "/Projects/thucydides/thucydides-report-resources/target/classes/report-resources/css/core.css";

         String expectedTargetSubDirectory = "css";

         FileResources fileResource = FileResources.from("classes/report-resources");
         assertThat(fileResource.findTargetSubdirectoryFrom(sourceResource), is(expectedTargetSubDirectory));
     }

    @Test
    public void should_transform_unix_source_path_into_relative_target_path() {

        String sourceResource = "/Projects/thucydides/thucydides-report-resources/target/classes/report-resources/css/core.css";

        String expectedTargetSubDirectory = "css";
        FileResources fileResource = FileResources.from("report-resources");

        assertThat(fileResource.findTargetSubdirectoryFrom(sourceResource), is(expectedTargetSubDirectory));
    }

    @Rule
    public ExtendedTemporaryFolder temporaryDirectory = new ExtendedTemporaryFolder();


    @Test
    public void should_attempt_to_get_output_stream_for_target_till_timeout() throws Exception {
        File targetDir = temporaryDirectory.newFolder("target");
        String sourceResource = new File("src/test/resources/resourcelist/sample.css").getAbsolutePath();
        final TestTimer timer = new TestTimer(FileResources.getDefaultRetryTimeout() / 60) ;

        FileResources fileResource = new FileResources("resourcelist") {
            @Override
            protected FileOutputStream createOutputStream(File destinationFile) throws FileNotFoundException {
                timer.increment();
                if (timer.timeOut()) {
                    return new FileOutputStream(destinationFile);
                } else {
                    throw new FileNotFoundException("Destination file not found");
                }
            }
        };

        fileResource.copyResourceTo(sourceResource, targetDir);

        File destinationFile = new File(targetDir, "sample.css");
        assertThat(destinationFile.exists(), is(true));

    }

    @Test
    public void should_copy_resource_file_into_target_directory() throws Exception {
        File targetDir = temporaryDirectory.newFolder("target");
        String sourceResource = new File("src/test/resources/resourcelist/sample.css").getAbsolutePath();
        FileResources fileResource = FileResources.from("resourcelist");

        fileResource.copyResourceTo(sourceResource, targetDir);

        File destinationFile = new File(targetDir, "sample.css");
        assertThat(destinationFile.exists(), is(true));
    }

    @Test
    public void should_copy_resource_file_into_nested_target_directory() throws Exception {
        File targetDir = temporaryDirectory.newFolder("target");
        String sourceResource = new File("src/test/resources/resourcelist/stylesheets/sample.css").getAbsolutePath();
        FileResources fileResource = FileResources.from("resourcelist");

        fileResource.copyResourceTo(sourceResource, targetDir);

        File destinationFile = new File(new File(targetDir,"stylesheets"), "sample.css");
        assertThat(destinationFile.exists(), is(true));
    }

    static class ErrorProneResourceList extends ResourceList {

        protected ErrorProneResourceList(final Pattern pattern) {
            super("", pattern);
        }

        @Override
        protected ZipFile zipFileFor(File file) throws IOException {
            throw new IOException("Could not open file");
        }
    }

    @Mock
    ZipFile zipFile;

    @Mock
    java.util.Enumeration emptyEntries;

    class MockResourceList extends ResourceList {

        protected MockResourceList(final Pattern pattern) {
            super("", pattern);
        }

        @Override
        protected ZipFile zipFileFor(File file) throws IOException {
            return zipFile;
        }
    }

    @Test(expected = ResourceCopyingError.class)
    public void should_raise_an_appropriate_error_if_the_jar_file_cannot_be_read() throws Exception {
        Pattern pattern = Pattern.compile(".*");
        ErrorProneResourceList resourceList = new ErrorProneResourceList(pattern);
        resourceList.list();
    }

    @Test(expected = ResourceCopyingError.class)
    public void should_raise_an_appropriate_error_if_the_jar_file_cannot_be_closed() throws Exception {
        doThrow(new IOException("Could not close file")).when(zipFile).close();
        when(zipFile.entries()).thenReturn(emptyEntries);
        when(emptyEntries.hasMoreElements()).thenReturn(false);

        Pattern pattern = Pattern.compile(".*");
        MockResourceList resourceList = new MockResourceList(pattern);
        resourceList.list();
    }

    @Test
    public void should_create_directory_if_copied() throws Exception {
        File targetDir = temporaryDirectory.newFolder("target");
        String sourceResource = new File("src/test/resources/resourcelist/stylesheets").getAbsolutePath();
        FileResources fileResource = FileResources.from("resourcelist");

        fileResource.copyResourceTo(sourceResource, targetDir);

        File destinationFile = new File(targetDir,"stylesheets");
        assertThat(destinationFile.exists(), is(true));
        assertThat(destinationFile.isDirectory(), is(true));
    }

}


class TestTimer {
    private long timeout = FileResources.getDefaultRetryTimeout();
    private long elapsedTime = 0;
    private long startTime = 0;
    public TestTimer(long timeout ) {
        this.timeout = timeout;
        this.startTime = new Date().getTime();
    }

    public void increment() {
        elapsedTime = new Date().getTime() - startTime;
    }

    public boolean timeOut() {
        return elapsedTime >= timeout;
    }

    public String toString() {
        return "[" + timeout + " , " + startTime + " , " + elapsedTime + "]";
    }
}

package net.thucydides.core.reports.html.screenshots;

import net.thucydides.core.util.ExtendedTemporaryFolder;
import net.thucydides.model.images.ResizableImage;
import net.thucydides.model.domain.screenshots.Screenshot;
import net.thucydides.model.domain.stacktrace.FailureCause;
import net.thucydides.model.util.FileSystemUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenReformattingScreenshots {

    @Rule
    public ExtendedTemporaryFolder folder = new ExtendedTemporaryFolder();






    private File screenshotDirectory;

    @Before
    public void setupWorkingDirectory() throws IOException {
        screenshotDirectory = folder.newFolder("screenshots");
        File screenshotsSourceDirectory = FileSystemUtils.getResourceAsFile("screenshots");
        File[] screenshots = screenshotsSourceDirectory.listFiles();
        for(File screenshot : screenshots) {
            Path file = screenshot.toPath();
            Path to = screenshotDirectory.toPath();
            Files.copy(file, to.resolve(file.getFileName()));
        }
    }

    @Test
    public void should_not_keep_original_image_by_default() throws IOException {

        Screenshot screenshot = new Screenshot("google_page_1.png", "Google", 1200,0);
        ScreenshotFormatter.forScreenshot(screenshot).
                inDirectory(screenshotDirectory).expandToHeight(1500);

        File backupScreenshot = new File(screenshotDirectory, "original_google_page_1.png");
        assertThat(backupScreenshot.exists(), is(false));
    }


    @Test
    public void should_not_resize_image_if_target_height_is_larger_than_the_maximum_height() throws IOException {

        Screenshot screenshot = new Screenshot("wikipedia.png", "Wikipedia", 805,9);
        Screenshot expandedScreenshot = ScreenshotFormatter.forScreenshot(screenshot).inDirectory(screenshotDirectory).expandToHeight(4000);

        int resultingHeight = new ResizableImage(new File(screenshotDirectory, expandedScreenshot.getFilename())).getHeight();
        assertThat(resultingHeight, is(29107));
        assertThat(expandedScreenshot.getWidth(), is(805));
    }

    @Test
    public void should_not_resize_image_if_screenshot_is_empty() throws IOException {

        Screenshot screenshot = new Screenshot("empty.png", "Wikipedia", 0,0);
        Screenshot expandedScreenshot = ScreenshotFormatter.forScreenshot(screenshot).inDirectory(screenshotDirectory).expandToHeight(4000);

        int resultingHeight = new ResizableImage(new File(screenshotDirectory, expandedScreenshot.getFilename())).getHeight();
        assertThat(resultingHeight, is(0));
        assertThat(expandedScreenshot.getWidth(), is(0));
    }

    @Test
    public void should_only_display_the_first_line_of_an_error_message_in_the_UI() {
        String errorMessage = "<org.openqa.selenium.ElementNotVisibleException: Unable to locate element: {\"method\":\"name\",\"selector\":\"fieldDoesNotExist\"}; duration or timeout: 8 milliseconds\n" +
                "For documentation on this error, please visit: http://seleniumhq.org/exceptions/no_such_element.html\n" +
                "Build info: version: '2.6.0', revision: '13840', time: '2011-09-13 16:51:41'\n" +
                "System info: os.name: 'Mac OS X', os.arch: 'x86_64', os.version: '10.7.1', java.version: '1.6.0_26'\n" +
                "Driver info: driver.version: RemoteWebDriver\n" +
                "Build info: version: '2.6.0', revision: '13840', time: '2011-09-13 16:51:41'\n" +
                "System info: os.name: 'Mac OS X', os.arch: 'x86_64', os.version: '10.7.1', java.version: '1.6.0_26'\n" +
                "Driver info: driver.version: unknown>";

        Screenshot screenshot = new Screenshot("wikipedia.png", "Wikipedia", 805, 0, new FailureCause(new AssertionError(errorMessage)));

        assertThat(screenshot.getShortErrorMessage(), is("Unable to locate element: {'method':'name','selector':'fieldDoesNotExist'}; duration or timeout: 8 milliseconds"));
    }

    @Test
    public void should_only_display_the_first_line_of_a_simple_error_message_in_the_UI() {
        String errorMessage = "Something broke";

        Screenshot screenshot = new Screenshot("wikipedia.png", "Wikipedia", 805,0,  new FailureCause(new AssertionError(errorMessage)));

        assertThat(screenshot.getShortErrorMessage(), is("Something broke"));
    }

    @Test
    public void should_make_error_message_javascript_safe() {
        String errorMessage = "Expected: \"red\" but: was red's color(\"reddish\")";

        Screenshot screenshot = new Screenshot("wikipedia.png", "Wikipedia", 805, 0, new FailureCause(new AssertionError(errorMessage)));

        assertThat(screenshot.getShortErrorMessage(), is("Expected: &quot;red&quot; but: was red's color(&quot;reddish&quot;)"));
    }
}

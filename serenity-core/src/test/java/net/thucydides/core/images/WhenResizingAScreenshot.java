package net.thucydides.core.images;

import net.thucydides.core.util.ExtendedTemporaryFolder;
import net.thucydides.core.util.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenResizingAScreenshot {

    @Rule
    public ExtendedTemporaryFolder temporaryFolder = new ExtendedTemporaryFolder();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_be_able_to_determine_the_size_of_an_image() throws IOException {
        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");
        SimpleImageInfo info = new SimpleImageInfo(screenshotFile);
        assertThat(info.getHeight(), is(788));
        assertThat(info.getWidth(), is(1200));
    }


    @Test
    public void should_be_able_to_determine_the_dimensions_of_an_image() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        int expectedWidth = 1200;
        int expectedHeight = 788;

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        assertThat(image.getWitdh(), is(expectedWidth));
        assertThat(image.getHeight(), is(expectedHeight));
    }

    @Test
    public void should_be_able_to_determine_the_maximum_dimensions_from_a_set_of_screenshots() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        int expectedWidth = 1200;
        int expectedHeight = 788;

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        assertThat(image.getWitdh(), is(expectedWidth));
        assertThat(image.getHeight(), is(expectedHeight));
    }

    @Test
    public void should_not_rescale_if_target_height_is_equal_to_image_height() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        ResizableImage resizedImage = image.rescaleCanvas(788);

        assertThat(resizedImage, is(image));
    }

    @Test
    public void should_not_try_to_redimension_images_that_are_too_large() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/wikipedia.png");

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        ResizableImage resizedImage = image.rescaleCanvas(1200);

        assertThat(resizedImage.getWitdh(), is(805));
        assertThat(resizedImage.getHeight(), is(greaterThan(1200)));
    }

    @Mock
    Logger logger;

    class DodgyResizableImage extends ResizableImage {

        public DodgyResizableImage(final File screenshotFile) throws IOException {
            super(screenshotFile);
        }

        @Override
        protected ResizableImage resizeImage(int width, int targetHeight, BufferedImage image) throws IOException {
            throw new IllegalArgumentException();
        }

        @Override
        protected Logger getLogger() {
            return logger;
        }
    }

    @Test
    public void should_not_fail_if_cant_take_screenshot() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        DodgyResizableImage image = new DodgyResizableImage(screenshotFile);

        ResizableImage resizedImage = image.rescaleCanvas(1200);

        assertThat((DodgyResizableImage) resizedImage, is(image));
        verify(logger).warn(contains("Could not resize screenshot"), any(Exception.class));

    }

    @Test
    public void should_not_try_to_redimension_images_on_small_canvas() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/wikipedia.png");

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        ResizableImage resizedImage = image.rescaleCanvas(1200);

        assertThat(resizedImage.getWitdh(), is(805));
        assertThat(resizedImage.getHeight(), is(greaterThan(1200)));
    }

    @Test
    public void should_not_try_to_redimension_images_larger_than_the_specified_size() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/wikipedia.png");

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        ResizableImage resizedImage = image.rescaleCanvas(4000);

        assertThat(resizedImage.getWitdh(), is(805));
        assertThat(resizedImage.getHeight(), is(greaterThan(4000)));
    }

    @Mock
    BufferedImage bufferedImage;

    @Test
    public void should_get_width_of_a_resized_image_directly_from_the_image() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");
        when(bufferedImage.getWidth()).thenReturn(100);

        ResizedImage image = new ResizedImage(bufferedImage, screenshotFile);

        assertThat(image.getWitdh(), is(100));
    }

    @Test
    public void should_get_height_of_a_resized_image_directly_from_the_image() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");
        when(bufferedImage.getHeight()).thenReturn(200);

        ResizedImage image = new ResizedImage(bufferedImage, screenshotFile);

        assertThat(image.getHeight(), is(200));
    }

    @Test
    public void should_not_try_to_redimension_images_that_are_higher_than_the_requested_height() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        ResizableImage resizedImage = image.rescaleCanvas(400);

        assertThat(resizedImage.getHeight(), is(greaterThan(400)));
    }


    @Test
    public void should_be_able_to_redimension_an_image_by_reducing_its_size() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        int newHeight = 938;

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);
        ResizableImage resizedImage = image.rescaleCanvas(newHeight);

        assertThat(resizedImage.getHeight(), is(newHeight));
    }

    @Test
    public void should_be_able_to_redimension_an_image_by_filling_out_the_background() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        int newHeight = 1250;

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);
        ResizedImage resizedImage = (ResizedImage) image.rescaleCanvas(newHeight);

        File resizedImageFile = temporaryFolder.newFile("resized_google_page_1.png");
        resizedImage.saveTo(resizedImageFile);

        File expectedScreenshot = screenshotFileFrom("/screenshots/google-page-resized.png");

        if (System.getProperty("File.separator") == "/") {
            // We can only check this in non-Windows environments
            // In Windows, we have to force the image type which changes the image contents.
            assertThat(FileUtils.contentEquals(resizedImageFile, expectedScreenshot), is(true));
        }
    }

    @Test
    public void should_be_able_to_redimension_a_large_image_generated_by_chrome() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/wikipedia-search.png");

        int newHeight = 2000;

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);
        ResizableImage resizedImage = image.rescaleCanvas(newHeight);

        if (System.getProperty("File.separator") == "/") {
            assertThat(resizedImage.getHeight(), is(newHeight));
        }
    }

    private File screenshotFileFrom(final String screenshot) {
        return FileSystemUtils.getResourceAsFile(screenshot);
//        URL sourcePath = getClass().getResource(screenshot);
//        return new File(sourcePath.getPath());
    }
}

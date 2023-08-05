package net.thucydides.core.model;

import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestStep;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import net.thucydides.model.util.FileSystemUtils;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenWeCreateATestStep {

    @Rule
    public ExtendedTemporaryFolder temporaryFolder = new ExtendedTemporaryFolder();
    
    @Test
    public void the_test_step_has_a_description() {
        TestStep step = new TestStep("a narrative description");
        assertThat(step.getDescription(), is("a narrative description"));
    }
    
    @Test
    public void the_test_step_can_have_an_illustration() throws IOException {
        TestStep step = new TestStep("a narrative description");

        File screenshot = temporaryFolder.newFile("screenshot.png");
        File source = temporaryFolder.newFile("screenshot.html");

        assertThat(step.hasScreenshots(), is(false));

        step.addScreenshot(new ScreenshotAndHtmlSource(screenshot, source));

        assertThat(step.hasScreenshots(), is(true));
        assertThat(step.getScreenshots().get(0).getScreenshot(), is(screenshot));
        assertThat(step.getScreenshots().get(0).getHtmlSource().get(), is(source));
    }

    @Test
    public void the_test_step_can_have_more_than_one_illustration() throws IOException {
        TestStep step = new TestStep("a narrative description");

        step.addScreenshot(forScreenshotWithImage("/screenshots/google_page_1.png").and().withSource("screenshot.html"));
        step.addScreenshot(forScreenshotWithImage("/screenshots/google_page_2.png").and().withSource("screenshot2.html"));

        ScreenshotAndHtmlSource screenshot1 = step.getScreenshots().get(0);
        ScreenshotAndHtmlSource screenshot2 = step.getScreenshots().get(1);

        assertThat(screenshot1.getScreenshot().getName(), is("google_page_1.png"));
        assertThat(screenshot1.getHtmlSource().get().getName(), is("screenshot.html"));

        assertThat(screenshot2.getScreenshot().getName(), is("google_page_2.png"));
        assertThat(screenshot2.getHtmlSource().get().getName(), is("screenshot2.html"));
        
        assertThat(screenshot1.hashCode(), is(not(screenshot2.hashCode())));
    }

    @Test
    public void the_test_step_knows_how_many_illustrations_it_has() throws IOException {
        TestStep step = new TestStep("a narrative description");

        step.addScreenshot(forScreenshotWithImage("/screenshots/google_page_1.png").and().withSource("screenshot.html"));
        step.addScreenshot(forScreenshotWithImage("/screenshots/google_page_2.png").and().withSource("screenshot2.html"));
        step.addScreenshot(forScreenshotWithImage("/screenshots/google_page_3.png").and().withSource("screenshot2.html"));

        assertThat(step.getScreenshotCount(), is(3));
    }              
    
    private ScreenshotAndHtmlSourceBuilder forScreenshotWithImage(String image) {
        return new ScreenshotAndHtmlSourceBuilder().withImage(image);
    }

    private class ScreenshotAndHtmlSourceBuilder {                   
        String image;
        public ScreenshotAndHtmlSourceBuilder withImage(String image) {
            this.image = image;
            return this;
        }
        
        public ScreenshotAndHtmlSourceBuilder and() {
            return this;
        }
        
        public ScreenshotAndHtmlSource withSource(String source) throws IOException {
            File screenshotFile = screenshotFileFrom(image);
            File sourceFile = new File(source);
            return new ScreenshotAndHtmlSource(screenshotFile, sourceFile);
        }
        
        
    }
    
    @Test
    public void the_first_screenshot_can_be_used_to_represent_the_step() throws IOException {
        TestStep step = new TestStep("a narrative description");

        File screenshot = temporaryFolder.newFile("screenshot.png");
        File source = temporaryFolder.newFile("screenshot.html");
        step.addScreenshot(new ScreenshotAndHtmlSource(screenshot, source));

        File screenshot2 = temporaryFolder.newFile("screenshot2.png");
        File source2 = temporaryFolder.newFile("screenshot2.html");
        step.addScreenshot(new ScreenshotAndHtmlSource(screenshot2, source2));

        assertThat(step.getFirstScreenshot().getScreenshot(), is(screenshot));
        assertThat(step.getFirstScreenshot().getHtmlSource().get(), is(source));
    }

    @Test
    public void if_a_screenshot_is_identical_to_the_previous_one_in_the_step_it_wont_be_added() throws IOException {
        TestStep step = new TestStep("a narrative description");

        File screenshot = temporaryFolder.newFile("screenshot.png");
        File source = temporaryFolder.newFile("screenshot.html");
        step.addScreenshot(new ScreenshotAndHtmlSource(screenshot, source));
        step.addScreenshot(new ScreenshotAndHtmlSource(screenshot, source));

        assertThat(step.getScreenshots().size(), is(1));
    }

    @Test
    public void the_first_screenshot_is_null_if_there_are_no_screenshots() throws IOException {
        TestStep step = new TestStep("a narrative description");

        assertThat(step.getFirstScreenshot(), is(nullValue()));
    }
    @Test
    public void when_a_step_fails_the_error_message_can_be_recorded() throws IOException {
        TestStep step = new TestStep("a narrative description");
      
        step.setResult(TestResult.FAILURE);
        step.failedWith(new Exception("Oh nose!"));
        assertThat(step.getErrorMessage(), containsString("Oh nose!"));
    }
    
    @Test
    public void when_a_step_fails_the_stack_trace_is_also_recorded() throws IOException {
        TestStep step = new TestStep("a narrative description");
      
        step.setResult(TestResult.FAILURE);
        Throwable e = new IllegalStateException();
        step.failedWith(new Exception("Oh nose", e));
        assertThat(step.getException().getStackTrace(), is(notNullValue()));
    }

    @Test
    public void when_a_step_fails_with_a_cause_the_original_message_is_used() throws IOException {
        TestStep step = new TestStep("a narrative description");

        step.setResult(TestResult.FAILURE);
        Throwable e = new IllegalStateException("Original error");
        step.failedWith(new Exception("Oh nose", e));
        assertThat(step.getErrorMessage(), containsString("Original error"));
    }

    @Test
    public void the_default_short_error_message_is_the_normal_error_message() throws IOException {
        TestStep step = new TestStep("a narrative description");

        step.setResult(TestResult.FAILURE);
        Throwable e = new IllegalStateException("Original error");
        step.failedWith(new Exception("Oh nose", e));
        assertThat(step.getShortErrorMessage(), is("Original error"));
    }

    @Test
    public void the_short_error_message_should_only_include_the_first_line() throws IOException {
        TestStep step = new TestStep("a narrative description");

        step.setResult(TestResult.FAILURE);
        Throwable e = new IllegalStateException("Original error\nwith lots of messy details");
        step.failedWith(new Exception("Oh nose", e));
        assertThat(step.getShortErrorMessage(), is("Original error"));
    }

    @Test
    public void the_short_error_message_should_replace_double_quotes_with_single_quotes() throws IOException {
        TestStep step = new TestStep("a narrative description");

        step.setResult(TestResult.FAILURE);
        Throwable e = new IllegalStateException("Original \"error\"\nwith lots of messy details");
        step.failedWith(new Exception("Oh nose", e));
        assertThat(step.getShortErrorMessage(), is("Original 'error'"));
    }

    @Test
    public void the_short_error_message_should_remove_double_quotes() throws IOException {
        TestStep step = new TestStep("a narrative description");

        step.setResult(TestResult.FAILURE);
        Throwable e = new IllegalStateException("Original error");
        step.failedWith(new Exception("Oh nose", e));
        assertThat(step.getShortErrorMessage(), is("Original error"));
    }

    @Test
    public void we_can_record_the_lifetime_of_a_test_step() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        Thread.sleep(150);
        step.recordDuration();
        assertThat(step.getDuration(), is(greaterThanOrEqualTo(100L)));
    }

    @Test
    public void we_can_display_the_lifetime_of_a_test_step_in_seconds() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        Thread.sleep(150);
        step.recordDuration();
        double expectedDuration = step.getDuration() / 1000.0;
        assertThat(step.getDurationInSeconds(), closeTo(expectedDuration,0.01));
    }

    @Test
    public void a_test_result_can_be_defined_for_a_step() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        step.setResult(TestResult.SUCCESS);

        assertThat(step.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void a_test_step_with_empty_child_steps_is_successful_unless_marked_as_pending() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        step.addChildStep(new TestStep("child step 1"));
        step.addChildStep(new TestStep("child step 2"));
        step.addChildStep(new TestStep("child step 3"));

        assertThat(step.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void an_empty_step_is_successful_unless_annotated_as_pending() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        assertThat(step.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void a_test_step_with_successful_child_steps_is_successful() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        step.addChildStep(successfulTestStepCalled("child step 1"));
        step.addChildStep(successfulTestStepCalled("child step 2"));
        step.addChildStep(successfulTestStepCalled("child step 3"));

        assertThat(step.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void an_ignored_test_step_with_successful_child_steps_is_still_ignored() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        step.setResult(TestResult.IGNORED);
        step.addChildStep(successfulTestStepCalled("child step 1"));
        step.addChildStep(successfulTestStepCalled("child step 2"));
        step.addChildStep(successfulTestStepCalled("child step 3"));

        assertThat(step.getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void a_skipped_test_step_with_successful_child_steps_is_still_ignored() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        step.setResult(TestResult.SKIPPED);
        step.addChildStep(successfulTestStepCalled("child step 1"));
        step.addChildStep(successfulTestStepCalled("child step 2"));
        step.addChildStep(successfulTestStepCalled("child step 3"));

        assertThat(step.getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void a_pending_test_step_with_successful_child_steps_is_still_pending() throws InterruptedException {
        TestStep step = new TestStep("a narrative description");
        step.setResult(TestResult.PENDING);
        step.addChildStep(successfulTestStepCalled("child step 1"));
        step.addChildStep(successfulTestStepCalled("child step 2"));
        step.addChildStep(successfulTestStepCalled("child step 3"));

        assertThat(step.getResult(), is(TestResult.PENDING));
    }


    private TestStep successfulTestStepCalled(String stepName) {
        TestStep step = new TestStep(stepName);
        step.setResult(TestResult.SUCCESS);
        return step;
    }

    private File screenshotFileFrom(final String screenshot) {
        return FileSystemUtils.getResourceAsFile(screenshot);
//        URL sourcePath = getClass().getResource(screenshot);
//        return new File(sourcePath.getPath());
    }
}

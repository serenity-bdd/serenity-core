package net.serenitybdd.cucumber.util;

import net.thucydides.core.model.TakeScreenshots;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StepDefinitionAnnotationReaderTest {

    @Test
    public void should_read_annotations_from_a_step_definition_method_name() {
        assertThat(
                StepDefinitionAnnotationReader
                        .forStepDefinition("net.serenitybdd.cucumber.util.SampleStepDefinitions.aStepDefinitionWithAScreenshotAnnotation()")
                        .getScreenshotPreferences()
        ).isEqualTo(TakeScreenshots.DISABLED);
    }

    @Test
    public void should_read_annotations_from_a_parameterised_step_definition_method_name() {
        assertThat(
                StepDefinitionAnnotationReader
                        .forStepDefinition("net.serenitybdd.cucumber.util.SampleStepDefinitions.aStepDefinitionWithAParameter(java.lang.String)")
                        .getScreenshotPreferences()
        ).isEqualTo(TakeScreenshots.DISABLED);
    }

    @Test
    public void screenshot_preference_is_undefined_if_no_annotation_is_present() {
        assertThat(
                StepDefinitionAnnotationReader
                        .forStepDefinition("net.serenitybdd.cucumber.util.SampleStepDefinitions.aStepDefinitionWithNoScreenshotAnnotation()")
                        .getScreenshotPreferences()
        ).isEqualTo(TakeScreenshots.UNDEFINED);
    }
    @Test
    public void screenshot_preference_is_before_and_after_each_step_by_default_if_no_annotation_is_present() {
        assertThat(
                StepDefinitionAnnotationReader
                        .forStepDefinition("net.serenitybdd.cucumber.util.SampleStepDefinitions.aStepDefinitionWithAScreenshotAnnotationWithNoAttribute()")
                        .getScreenshotPreferences()
        ).isEqualTo(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP);
    }
}

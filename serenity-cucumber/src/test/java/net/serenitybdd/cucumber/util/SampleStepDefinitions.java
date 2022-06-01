package net.serenitybdd.cucumber.util;

import io.cucumber.java.en.Given;
import net.thucydides.core.annotations.Screenshots;

public class SampleStepDefinitions {

    @Given("some precondition 1")
    public void aStepDefinitionWithNoScreenshotAnnotation() {}

    @Given("some precondition 2")
    @Screenshots(disabled = true)
    public void aStepDefinitionWithAScreenshotAnnotation() {}

    @Given("some precondition 3")
    @Screenshots(disabled = true)
    public void aStepDefinitionWithAParameter(String parameter) {}

    @Given("some precondition 4")
    @Screenshots
    public void aStepDefinitionWithAScreenshotAnnotationWithNoAttribute() {}


}

package net.serenitybdd.cucumber.util;

import io.cucumber.java.en.Given;
import net.serenitybdd.annotations.Screenshots;

public class SampleStepDefinitions {

    @Given("some precondition 1")
    public void aStepDefinitionWithNoScreenshotAnnotation() {}

    @Given("some precondition 2")
    public void aStepDefinitionWithAScreenshotAnnotation() {}

    @Given("some precondition 3")
    public void aStepDefinitionWithAParameter(String parameter) {}

    @Given("some precondition 4")
    @Screenshots
    public void aStepDefinitionWithAScreenshotAnnotationWithNoAttribute() {}


}

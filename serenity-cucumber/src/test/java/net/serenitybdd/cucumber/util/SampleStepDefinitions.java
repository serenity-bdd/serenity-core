package net.serenitybdd.cucumber.util;

import io.cucumber.java.en.Given;
import net.thucydides.core.annotations.Screenshots;

public class SampleStepDefinitions {

    @Given("some precondition")
    public void aStepDefinitionWithNoScreenshotAnnotation() {}

    @Given("some precondition")
    @Screenshots(disabled = true)
    public void aStepDefinitionWithAScreenshotAnnotation() {}

    @Given("some precondition")
    @Screenshots(disabled = true)
    public void aStepDefinitionWithAParameter(String parameter) {}

    @Given("some precondition")
    @Screenshots
    public void aStepDefinitionWithAScreenshotAnnotationWithNoAttribute() {}


}

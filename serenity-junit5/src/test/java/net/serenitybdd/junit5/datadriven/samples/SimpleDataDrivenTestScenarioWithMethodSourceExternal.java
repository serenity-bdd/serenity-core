package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@ExtendWith(SerenityJUnit5Extension.class)
public class SimpleDataDrivenTestScenarioWithMethodSourceExternal {

    @Steps
    public SampleScenarioSteps steps;

    @ParameterizedTest(name = "run {index} with {arguments}")
    @MethodSource("net.serenitybdd.junit5.datadriven.samples.ExternalParametersProvider#stringProvider")
    void withMethodSourceSimpleStatic(String string1,String string2) {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }


}

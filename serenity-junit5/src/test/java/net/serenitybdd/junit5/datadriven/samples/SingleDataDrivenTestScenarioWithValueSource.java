package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(SerenityJUnit5Extension.class)
public class SingleDataDrivenTestScenarioWithValueSource {

    @Steps
    public SampleScenarioSteps steps;

    @ParameterizedTest
    @ValueSource(ints = { 1,2,3 })
    void withValueSourceIntegers(int number) {
        steps.stepWithData(number);
    }
}

package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.junit5.SerenityBDD;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.thucydides.core.annotations.Steps;

import net.thucydides.samples.SampleScenarioSteps;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(SerenityJUnit5Extension.class)
@SerenityBDD
public class SimpleSuccessfulParameterizedTestSample {


    @Steps
    public SampleScenarioSteps steps;

    @ParameterizedTest
    @ValueSource(strings = { "A", "B" ,"C"})
    public void test1() {
    }


    @ParameterizedTest
    @ValueSource(strings = { "A", "B" ,"C"})
    public void test2() {
    }

}

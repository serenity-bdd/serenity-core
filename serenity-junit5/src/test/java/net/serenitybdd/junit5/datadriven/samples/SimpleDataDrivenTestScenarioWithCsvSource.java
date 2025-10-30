package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SerenityJUnit5Extension.class)
public class SimpleDataDrivenTestScenarioWithCsvSource {

    @Steps
    public SampleScenarioSteps steps;

    @ParameterizedTest
    @CsvSource({"2, Unit testing", "3, JUnit in Action",
            "4, Write solid Java code"})
    public void testWordsInSentence(int expected, String sentence) {
        assertEquals(expected, sentence.split(" ").length);
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }

}

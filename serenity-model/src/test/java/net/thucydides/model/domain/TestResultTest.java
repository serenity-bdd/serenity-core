package net.thucydides.model.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestResultTest {

    @ParameterizedTest(name = "Should {0} be more severe than {1} => {2}")
    @CsvSource({
            "COMPROMISED, SUCCESS, true",
            "ERROR, SUCCESS, true",
            "FAILURE, SUCCESS, true",
            "FAILURE, FAILURE, false",
            "SUCCESS, FAILURE, false"
    })
    public void shouldDetectMoreSevereResults(TestResult firstStatus, TestResult secondStatus, boolean expectedResult) {
        Assertions.assertEquals(expectedResult, firstStatus.isMoreSevereThan(secondStatus));

    }

    @ParameterizedTest(name = "Should {0} be less severe than {1} => {2}")
    @CsvSource({
            "COMPROMISED, SUCCESS, false",
            "ERROR, SUCCESS, false",
            "FAILURE, SUCCESS, false",
            "FAILURE, FAILURE, false",
            "SUCCESS, FAILURE, true"
    })
    public void shouldDetectLessSevereResults(TestResult firstStatus, TestResult secondStatus, boolean expectedResult) {
        Assertions.assertEquals(expectedResult, firstStatus.isLessSevereThan(secondStatus));
    }


    @ParameterizedTest(name = "{0} should override {1} if it is in a nested step")
    @CsvSource({
            "COMPROMISED, SUCCESS",
            "ERROR, SUCCESS",
            "FAILURE, SUCCESS",
            "PENDING, SUCCESS",
    })
    public void shouldMarkCertainResultsAsHigherPriority(TestResult nestedResult, TestResult topLevelResult) {
        Assertions.assertTrue(nestedResult.overrides(topLevelResult));
    }
}

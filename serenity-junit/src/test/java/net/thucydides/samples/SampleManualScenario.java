package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Manual;
import net.thucydides.core.model.TestResult;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class SampleManualScenario {

    @Manual @Test
    public void a_manual_test() {}

    @Manual(result = TestResult.SUCCESS)
    @Test
    public void a_successful_manual_test() {}

    @Manual(result = TestResult.FAILURE)
    @Test
    public void a_failing_manual_test() {}

    @Manual(result = TestResult.FAILURE, reason = "Doesn't work")
    @Test
    public void a_failing_manual_test_with_a_message() {}

//    @Manual(result = TestResult.SUCCESS, lastTested = "2000-01-01")
//    @Test
//    public void a_successful_manual_test_with_an_expiry_date() {}

}

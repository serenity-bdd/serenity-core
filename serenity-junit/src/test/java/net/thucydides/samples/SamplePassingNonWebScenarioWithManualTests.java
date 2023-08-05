package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.annotations.Manual;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class SamplePassingNonWebScenarioWithManualTests {
    
    @Test
    @Manual
    public void a_manual_test() throws Throwable {}

}

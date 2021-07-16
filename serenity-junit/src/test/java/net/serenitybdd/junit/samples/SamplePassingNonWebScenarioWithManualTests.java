package net.serenitybdd.junit.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Manual;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class SamplePassingNonWebScenarioWithManualTests {

    @Test
    @Manual
    public void a_manual_test() throws Throwable {}

}

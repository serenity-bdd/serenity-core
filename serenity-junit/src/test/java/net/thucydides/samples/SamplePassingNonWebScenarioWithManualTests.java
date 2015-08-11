package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Manual;
import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(SerenityRunner.class)
public class SamplePassingNonWebScenarioWithManualTests {
    
    @Test
    @Manual
    public void a_manual_test() throws Throwable {}

}

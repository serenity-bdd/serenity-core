package net.thucydides.samples;

import net.serenitybdd.junit5.extensions.Serenity;
import net.thucydides.core.annotations.Manual;
import org.junit.jupiter.api.Test;

@Serenity
public class SamplePassingNonWebScenarioWithManualTests {
    
    @Test
    @Manual
    public void a_manual_test() throws Throwable {}

}
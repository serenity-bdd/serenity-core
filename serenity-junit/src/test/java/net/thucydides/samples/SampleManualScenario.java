package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Manual;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class SampleManualScenario {

    @Manual @Test
    public void a_manual_test() {}

}

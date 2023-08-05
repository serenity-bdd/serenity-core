package net.thucydides.junit.sampletests.thucydidestests

import net.serenitybdd.annotations.Steps
import net.thucydides.junit.annotations.UseTestDataFrom
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import org.junit.Test
import org.junit.runner.RunWith

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.not

@RunWith(SerenityParameterizedRunner.class)
@UseTestDataFrom("data.csv")
class SampleDataDrivenTestCase {
    
    String name
    String age

    @Steps
    StepLibrary steps;

    SampleDataDrivenTestCase() {
    }

    @Test
    public void testSomethingWithData(){
        System.out.println("Processing " + name + "/" + age)
        assertThat(name, not(equalTo("Tim")))
        assertThat(name, not(equalTo("John")))
        steps.step1();
    }

}

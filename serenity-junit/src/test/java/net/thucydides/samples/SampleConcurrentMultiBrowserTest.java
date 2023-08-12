package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.ManagedPages;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.WithDriver;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.Concurrent;
import net.thucydides.junit.annotations.TestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Collection;

@RunWith(SerenityParameterizedRunner.class)
@Concurrent(threads = "4")
public class SampleConcurrentMultiBrowserTest {

    @TestData
    public static Collection<Object[]> todoItems(){
        return Arrays.asList(new Object[][]{
                {"walk the lion"},
                {"wash the dishes"},
                {"feed the ferrets"},
                {"count the rabbits"},
                {"catch the fish"}
        });
    }

    private String todo;

    public SampleConcurrentMultiBrowserTest(String todo) {
        this.todo = todo;
    }


    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;
    
    @Steps
    public SampleScenarioSteps steps;

    @Test
    @WithDriver("chrome")
    public void happy_day_scenario() throws Throwable {
        steps.stepThatUsesABrowser();
        steps.stepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.stepThatIsPending();
        steps.anotherStepThatSucceeds();
    }

    @Test
    @WithDriver("chrome")
    public void edge_case_1() {
        steps.stepThatUsesABrowser();
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
    }

    @Test
    @WithDriver("chrome")
    public void edge_case_2() {
        steps.stepThatUsesABrowser();
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
    }

    @Test
    @WithDriver("chrome")
    public void edge_case_3() {
        steps.stepThatUsesABrowser();
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
    }

    @Test
    @WithDriver("firefox")
    public void edge_case_4() {
        steps.stepThatUsesABrowser();
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
    }

    @Test
    @WithDriver("firefox")
    public void edge_case_5() {
        steps.stepThatUsesABrowser();
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
    }

    @Test
    @WithDriver("chrome")
    public void edge_case_6() {
        steps.stepThatUsesABrowser();
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
    }

    @Test
    @WithDriver("chrome")
    public void edge_case_7() {
        steps.stepThatUsesABrowser();
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
    }

    @Test
    @WithDriver("chrome")
    public void edge_case_8() {
        steps.stepThatUsesABrowser();
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
    }

    @Test
    @WithDriver("chrome")
    public void edge_case_9() {
        steps.stepThatUsesABrowser();
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
    }
//
//    @Test
//    @WithDriver("safari")
//    public void edge_case_10() {
//        steps.stepThatUsesABrowser();
//        steps.stepThatSucceeds();
//        steps.anotherStepThatSucceeds();
//        steps.stepThatIsPending();
//    }
//
//    @Test
//    @WithDriver("safari")
//    public void edge_case_11() {
//        steps.stepThatUsesABrowser();
//        steps.stepThatSucceeds();
//        steps.anotherStepThatSucceeds();
//        steps.stepThatIsPending();
//    }
//
//    @Test
//    @WithDriver("safari")
//    public void edge_case_12() {
//        steps.stepThatUsesABrowser();
//        steps.stepThatSucceeds();
//        steps.anotherStepThatSucceeds();
//        steps.stepThatIsPending();
//    }
}

package net.thucydides.samples;

import net.serenitybdd.annotations.Pending;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.junit.Assume;
import org.junit.Ignore;
import org.openqa.selenium.ElementNotInteractableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("serial")
public class SampleScenarioSteps extends ScenarioSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerenityRunner.class);

    public SampleScenarioSteps(Pages pages) {
        super(pages);
    }

    @Steps
    public SampleScenarioNestedSteps nestedSteps;

    @Step
    public void stepThatSucceeds() {

    }

    @Step
    public void stepWithFailedAssumption() {
        Assume.assumeTrue(false);
    }

    @Step
    public void anotherStepThatSucceeds() {
    }

    IndexPage indexPage;

    @Step
    public void stepThatUsesABrowser() {
        indexPage.open();
        assertThat(indexPage.getTitle(), not(isEmptyString()));
    }

    @Step
    public void stepThatOpensWikipedia() {
        WikipediaPage page = pages().get(WikipediaPage.class);
        page.open();
        page.getTitle();
    }

    @Step
    public void anotherStepThatUsesABrowser() {
        IndexPage page = pages().get(IndexPage.class);
        page.enterValue("some value");
        page.enterValue("some value");
        page.enterValue("some other different value");
    }

    @Step
    public void aStepThatAlsoUsesABrowser() {
        IndexPage page = pages().get(IndexPage.class);
        page.enterValue("some other value");
        page.enterValue("some other value");
        page.enterValue("some other value");
    }

    @Step
    public void stepThree(String option) {
    }

    @Step
    public void stepWithParameters(String option1, Integer option2) {
    }

    @Step
    public void stepWithParameters(String option1, Integer option2, Boolean pause) {
        if (pause) {
            action();
        }
    }

    @Step
    public void moreStepWithParameters(String option1, Integer option2) {
    }

    @Step
    public void stepThatFails() {
        assertThat(1, is(2));
    }

    @Step
    public void stepFour(String option) {
    }

    @Step
    public void stepThatShouldBeSkipped() {
    }

    @Step("Nested group of steps")
    public void stepThatCallsNestedSteps() {
        nestedSteps.stepThatSucceeds();
        nestedSteps.anotherStepThatSucceeds();
    }

    @Step("A pending step")
    @Pending
    public void stepThatIsPending() {
    }

    @Step
    @Ignore
    public void stepThatIsIgnored() {
    }

    public void anUnannotatedMethod() {
    }

    @Step
    public void stepWithAParameter(String value) {
    }

    @Step
    public void stepWithTwoParameters(String value, int number) {
    }

    @Step
    public void groupOfStepsContainingAFailure() {
        stepThatSucceeds();
        stepThatFails();
        stepThatShouldBeSkipped();

    }

    @Step
    public void anotherGroupOfSteps() {
        stepThatSucceeds();
        anotherStepThatSucceeds();
        stepThatIsPending();

    }

    @Step
    public void groupOfStepsContainingAnError() {
        stepThatSucceeds();
        anotherStepThatSucceeds();
        String nullString = null;
        int thisShouldFail = nullString.length();

    }

    IndexPage page;

    @Step
    public void failsToFindElement() {
        throw new RuntimeException("Element not found");
    }

    public String name;
    public String age;
    public String address;

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Step
    public void data_driven_test_step() {
        enter_name_and_age(name, age);
        enter_address(address);
        nestedSteps.stepThatSucceeds();
        nestedSteps.anotherStepThatSucceeds();
        nestedSteps.groupOfSteps();
    }

    @Step
    public void simple_data_driven_test_step() {
        enter_name_and_age(name, age);
        enter_address(address);
    }

    @Step
    public void data_driven_test_step_that_fails() {
        assertThat(Integer.parseInt(age), is(lessThan(35)));
    }

    @Step
    public void data_driven_test_step_that_breaks() {
        if (Integer.parseInt(age) > 35) {
            throw new ElementNotInteractableException("A webdriver issue");
        }
    }

    @Step
    public void enter_address(String address) {
    }

    @Step
    public void enter_name_and_age(String name, String age) {
    }

    @Pending
    @Step
    public void data_driven_test_step_that_is_skipped() {
    }

    @Step
    public void store_name(String value) {
        Serenity.getCurrentSession().put("name", value);
    }

    @Step
    public String get_name() {
        return (String) Serenity.getCurrentSession().get("name");
    }

    public Boolean hasName() {
        return Serenity.getCurrentSession().containsKey("name");
    }

    private void action() {
        try {
            System.out.println("STEP EXECUTED " + ZonedDateTime.now());
//            Thread.sleep(ThreadLocalRandom.current().nextInt(2, 5));
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

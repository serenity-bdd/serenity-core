package net.serenitybdd.cucumber.integration.steps.thucydides;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SomeSeleniumSteps {

    @Managed
    public WebDriver webDriver;

    @ManagedPages
    public Pages pages;

    @Steps
    SomeNestedSeleniumSteps the_user;

    StaticSitePage page;

    @Given("I have an implemented JBehave scenario that uses selenium")
    public void givenIHaveAnImplementedJBehaveScenarioThatUsesSelenium() {
    }

    @Given("the scenario uses selenium")
    public void givenTheScenarioUsesSelenium() {
    }

    @When("I run the web scenario")
    public void whenIRunTheWebScenario() {
        page.open();
    }

    @Then("the webdriver variable should be correctly instantiated")
    public void thenTheWebdriverVariableShouldBeCorrectlyInstantiated() {
        assertThat(webDriver, is(notNullValue()));
    }

    @Then("the pages variable should be correctly instantiated")
    public void thenThePagesVariableShouldBeCorrectlyInstantiated() {
        assertThat(pages, is(notNullValue()));
    }



   /* @When("I type in the first name <firstname>")
    public void whenITypeInTheFirstName(String firstname) {
        the_user.enters_the_first_name(firstname);
    }*/

    @When("I type in the last name <lastname>")
    public void whenITypeInTheLastName(String lastname) {
        the_user.enters_the_last_name(lastname);
    }



    @Then("I should see first name $expectedFirstname on the screen")
    public void thenIShouldSeeFirstNameOnTheScreen(String $expectedFirstname) {
        assertThat(page.firstName().getValue(), is($expectedFirstname));
    }

    @Then("I should see last name $expectedLastname on the screen")
    public void thenIShouldSeeLastNameOnTheScreen(String $expectedLastname) {
        assertThat(page.lastName().getValue(), is($expectedLastname));
    }

    @Then("I should be using {}")
    public void andIShouldBeUsing(String driverName) {
        assertThat(((WebDriverFacade)webDriver).getDriverClass().getName(), containsString(driverName));
    }

    @Given("the scenario throws an exception")
    public void throwAnException() {
        throw new ElementNotVisibleException("Oops");
    }


    //Cucumber

    @Given("^I am on the test page$")
    public void givenIAmOnTheTestPage() {
        page.open();
    }

    @When("^I enter a first name (.*)")
    public void whenIEnterTheFirstName(String firstname) {
        page.setFirstName(firstname);
    }

    @When("^I enter a last name (.*)")
    public void whenIEnterTheLastName(String lastname) {
        page.setLastName(lastname);
    }

    @When("^I type in the first name (.*)$")
    public void whenITypeInTheFirstName(String firstname)
    {
        the_user.enters_the_first_name(firstname);
    }

    @When("^I type in the last name (.*)$")
    public void i_type_in_the_last_name(String lastname) throws Throwable {
        the_user.enters_the_last_name(lastname);
    }


    @Then("^I should see first name (.*) on the screen$")
    public void i_should_see_entered_values_of_firstName(String expectedFirstName) throws Throwable {
        assertThat(page.firstName().getValue(), is(expectedFirstName));
    }

    @Then("^I should see last name (.*) on the screen$")
    public void i_should_see_entered_values_of_lastName(String expectedLastName) throws Throwable {
        assertThat(page.lastName().getValue(), is(expectedLastName));
    }

    @Then("^I should see entered values of (.*) and (.*)$")
    public void i_should_see_entered_values_of_firstName_and_lastName(String expectedFirstName, String expectedLastName) throws Throwable {
        assertThat(page.firstName().getValue(), is(expectedFirstName));
        assertThat(page.lastName().getValue(), is(expectedLastName));
    }

    @Then("^I should see the (.*) and (.*) in the names fields$")
    public void thenIShouldSeeInTheNamesFields(String expectedFirstname,
                                               String expectedLastname) {
        assertThat(page.firstName().getValue(), is(expectedFirstname));
        assertThat(page.lastName().getValue(), is(expectedLastname));
    }
}

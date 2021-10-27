package net.serenitybdd.cucumber.smoketests;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SkippingScenariosStepDefinitions {

    @DefaultUrl("https://duckduckgo.com")
    public static class DuckDuckGoSearchPage extends PageObject {

        @FindBy(id = "search_form_input_homepage")
        WebElementFacade searchField;

        @FindBy(id = "search_button_homepage")
        WebElementFacade searchButton;

        @FindBy(className = "result__title")
        List<WebElementFacade> results;

        public void enterSearchTerm(String searchTerm) {
            searchField.type(searchTerm);
        }

        public void requestSearch() {
            searchButton.click();
        }

        public List<String> getResults() {
            return results.stream().map(element -> element.getText()).collect(Collectors.toList());
        }

    }

    public static class CuriousSurfer {

        DuckDuckGoSearchPage searchPage;

        @Step
        public void opensTheSearchApp() {
            searchPage.open();
        }

        @Step
        public void searchesFor(String searchTerm) {
            searchPage.enterSearchTerm(searchTerm);
            searchPage.requestSearch();
        }

        @Step
        public void shouldSeeTitle(String title) {
            Assertions.assertThat(searchPage.getTitle()).contains(title);
        }

        @Step
        public void shouldSeeAListOfResults() {
        }

    }

    @Steps
    CuriousSurfer connor;

    @Given("I want to search for something")
    public void givenIWantToSearchForFruit() {
        connor.opensTheSearchApp();
    }

    @Given("I want to add two numbers")
    public void givenIWantToAddTwoNumbers() {
    }

    int runningTotal = 0;

    @When("the first number is {int}")
    public void theFirstNumberIs(int n) {
        runningTotal += n;
    }

    @Then("the running total should be {int}")
    public void runningTotalIs(int n) {
        assertThat(runningTotal).isEqualTo(n);
    }

    @When("I lookup {}")
    public void whenILookup(String searchTerm) {
        connor.searchesFor(searchTerm);
    }

    @When("I view the home page details")
    public void viewHomePage() {
    }

    @Then("I should see {} in the page title")
    public void thenIShouldSeeTitle(String title) {
        connor.shouldSeeTitle(title);
    }

    @Before("@start-at-two")
    public void doSomethingBefore() {
        runningTotal = 2;
    }

    @After("@multiply-result-by-two")
    public void doSomethingAfter() {
        runningTotal = runningTotal * 2;
    }
}

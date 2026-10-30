package shop;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;

public class SignInStepDefinitions {

    @Steps
    ReaderSteps reader;

    @Given("Reader is on the sign-in page")
    public void readerIsOnTheSignInPage() {
        reader.opensTheSignInPage();
    }

    @When("Reader signs in as {string} with the password {string}")
    public void readerSignsIn(String email, String password) {
        reader.signsInWith(email, password);
    }

    @Then("the {word} page is shown")
    public void thePageIsShown(String name) {
        reader.shouldSeeThePage(name);
    }

    @Then("Reader is told {string}")
    public void readerIsTold(String message) {
        reader.shouldBeToldThat(message);
    }

    // "book(s)" is optional text in a Cucumber expression: this step matches "5 book" and "5 books",
    // but NOT the literal text "5 book(s)" if a feature file contains it.
    @Then("the catalogue lists {int} book(s)")
    public void theCatalogueLists(int count) {
        reader.shouldBeToldThat(count + " books");
    }
}

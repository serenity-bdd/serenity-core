package integration.cucumer;

/**
 * Created by khanhdo on 8/30/18.
 */

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import integration.serenitySteps.IFixitSteps;
import net.thucydides.core.annotations.Steps;
public class iFixitScenarioSteps {

    @Steps
    IFixitSteps iFixitSteps;

    @Given("^User goes to Home page$")
    public void gotoIFixitPage() {
        iFixitSteps.clickOnStartARepairButton();
        sleep(2);
    }

    @And("^User clicks on Car and Truck category$")
    public void clickOnCarTruckCategory() {
        iFixitSteps.clickOnCarAndTruckText();
    }

    @And("^User clicks on Acura category$")
    public void clickOnAcuraCateogry() {
        iFixitSteps.clickOnAcuraText();
    }

    @When("^User waits for Navigation Bar$")
    public void waitForNavigationBar() {
        iFixitSteps.waitkOnXCUIElementTypeNavigationBar();
    }

    @Then("^Verify five items display: Acura Integra, Acura MDX, Acura RL, Acura TL, Acura TSX$")
    public void verifyItems() {
        iFixitSteps.verifyItems();
    }

    public void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

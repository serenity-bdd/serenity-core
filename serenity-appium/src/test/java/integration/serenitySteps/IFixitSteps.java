package integration.serenitySteps;

import integration.pages.IFixitPage;
import net.thucydides.model.annotations.Step;
import net.thucydides.core.steps.ScenarioSteps;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by khanhdo on 8/30/18.
 */
public class IFixitSteps extends ScenarioSteps {
    IFixitPage ifixitPage;

    @Step
    public void clickOnAcuraText() {
        ifixitPage.clickOnAcuraText();
    }

    @Step
    public void clickOnCarAndTruckText() {
        ifixitPage.clickOnCarAndTruckText();
    }

    @Step
    public void clickOnStartARepairButton() {
        ifixitPage.clickOnStartARepairButton();
    }

    @Step
    public boolean isXCUIElementTypeNavigationBarShown() {
        return ifixitPage.isXCUIElementTypeNavigationBarShown();
    }

    @Step
    public void waitkOnXCUIElementTypeNavigationBar() {
        ifixitPage.waitkOnXCUIElementTypeNavigationBar();
    }

    @Step
    public void verifyItems() {
        ifixitPage.verifyItems();
    }

}

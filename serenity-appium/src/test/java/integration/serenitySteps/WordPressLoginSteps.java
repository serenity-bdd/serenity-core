package integration.serenitySteps;

import integration.pages.WordPressLoginPage;

import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.model.annotations.Step;

import static org.assertj.core.api.Assertions.assertThat;

public class WordPressLoginSteps extends ScenarioSteps{
    WordPressLoginPage loginPage;

    @Step
    public void loginPageInvalidDataInput(){
        loginPage.gotoWPLoginPage();
    }

    @Step
    public void enterLoginData(){
        loginPage.enterInvalidEmailIdWPLoginPage();
    }

    @Step
    public void checkErrorMessage(){
        assertThat(loginPage.isErrorMessageShownWPLoginPage()).isTrue();
    }

}

package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.webtests.model.LoginAccount;
import net.serenitybdd.screenplay.webtests.pages.JLLoginPage;
import net.thucydides.core.annotations.Step;

public class JLLoginWebApp implements Performable{
    private JLLoginPage jlLoginPage;

    LoginAccount loginAccount = new LoginAccount("lienn-uat@joblogic.com", "1");

    @Step("{0} opens the application")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Enter.theValue(loginAccount.getUsername()).into(jlLoginPage.txtEmail),
                Enter.theValue(loginAccount.getPassword()).into(jlLoginPage.txtPassword),
                Click.on(jlLoginPage.btnLogin));
    }
}

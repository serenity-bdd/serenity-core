package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

/**
 * Switch to an alert window
 */
public class AlertAction implements Performable {
    @Override
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWeb.as(actor).getDriver().switchTo().alert();
    }

    /**
     * Dismiss the HTML alert message
     */
    public Performable andDismiss() {
        return Task.where("{0} dismisses the alert dialog",
                actor -> BrowseTheWeb.as(actor).getDriver().switchTo().alert().dismiss()
        );
    }

    /**
     * Accept the HTML alert message
     */
    public Performable andAccept() {
        return Task.where("{0} accepts the alert dialog",
                actor -> BrowseTheWeb.as(actor).getDriver().switchTo().alert().accept()
        );
    }

    /**
     * Retrieve the text of the current alert window
     */
    public Question<String> getText() {
        return Question.about("the alert text")
                .answeredBy(actor -> BrowseTheWeb.as(actor).getDriver().switchTo().alert().getText());
    }
}

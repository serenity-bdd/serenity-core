package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Settable;
import net.serenitybdd.screenplay.webtests.actions.UpdateTextFieldValue;
import net.serenitybdd.screenplay.webtests.pages.JLSitesAddSitePage;
import net.thucydides.core.annotations.Step;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class JLAddNewSite implements Performable {

    private final String customer;
    String site;

    public JLAddNewSite(String customer){
        this.customer = customer;
    }
    
    public static Settable customer() { return instrumented(UpdateTextFieldValue.class, JLSitesAddSitePage.DDCUSTOMER); }
    public static Settable site() { return instrumented(UpdateTextFieldValue.class, JLSitesAddSitePage.TXTSITE);
    }

    public static JLAddNewSite withCustomer(String customer) {
        return instrumented(JLAddNewSite.class, customer);
    }

    public JLAddNewSite withSite(String site) {
        this.site = site;
        return this;
    }

    @Step("{0} Add New Site for a customer")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                JLAddNewSite.customer().to(customer),
                JLAddNewSite.site().to(site),
                Click.on(JLSitesAddSitePage.BTNSAVE)
        );

    }
}

package net.serenitybdd.screenplay.shopping.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.equalTo;

@Subject("the prices should be correctly displayed")
public class DisplayedPrices implements Question<Void> {

    private final int vat;
    private final boolean throwError;

    public DisplayedPrices(int vat, boolean throwError) {
        this.vat = vat;
        this.throwError = throwError;
    }

    @Override
    public Void answeredBy(Actor actor) {
        actor.should(
                seeThat("the total price", ThePrice.total(), equalTo(100)),
                seeThat("the VAT", ThePrice.vat(throwError), equalTo(vat)),
                seeThat("the price with VAT", ThePrice.totalWithVAT(), equalTo(120))
        );
        return null;
    }

    public static DisplayedPrices thePriceIsCorrectlyDisplayed() {
        return new DisplayedPrices(20, false);
    }

    public static DisplayedPrices thePriceIsIncorrectlyDisplayed() {
        return new DisplayedPrices(30, false);
    }

    public static DisplayedPrices thePriceIsIncorrectlyDisplayedWithAnError() {
        return new DisplayedPrices(20, true);
    }

}

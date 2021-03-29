package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.questions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.SelectedVisibleTextValue;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.Value;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.model.Customer;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.pages.ProfilePage;

public class ProfileQuestion implements Question<Customer> {
    @Override
    public Customer answeredBy(Actor actor) {
        String name = Value.of(ProfilePage.NAME)
                           .viewedBy(actor)
                           .value();

        String country = SelectedVisibleTextValue.of(ProfilePage.COUNTRY).viewedBy(actor).value();
        return new Customer(name, country);
    }
}

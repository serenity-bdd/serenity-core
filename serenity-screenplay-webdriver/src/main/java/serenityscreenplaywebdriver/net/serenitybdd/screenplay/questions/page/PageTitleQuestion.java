package serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.page;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import serenityscreenplay.net.serenitybdd.screenplay.annotations.Subject;

@Subject("the title of the page")
public class PageTitleQuestion implements Question<String> {
    @Override
    public String answeredBy(Actor actor) {
        return BrowseTheWeb.as(actor).getTitle();
    }
}

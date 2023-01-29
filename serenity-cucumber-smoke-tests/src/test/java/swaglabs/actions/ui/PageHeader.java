package swaglabs.actions.ui;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.ui.PageElement;

public class PageHeader {
    public static Question<String> title() {
        return Text.of(PageElement.withNameOrId("title"));
    }
}

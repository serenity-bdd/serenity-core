package net.poc.questions;

import net.poc.ui.NoteDetailsPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class DetailNote implements Question<Boolean> {

    @Override
    public Boolean answeredBy(Actor actor) {
        return NoteDetailsPage.PICTURE_FIELD.resolveFor(actor).isCurrentlyVisible();
    }

    public static DetailNote hasPicture() {
        return new DetailNote();
    }
}

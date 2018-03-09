package net.poc.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.poc.model.Note;
import net.poc.ui.AddNotePage;
import net.poc.ui.CameraViewerPage;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class EnterANote implements Interaction {

    private Note note;

    public EnterANote(Note note) {
        this.note = note;
    }

    @Override
    @Step("{0} fills the note")
    public <T extends Actor> void performAs(T actor) {
        if(!note.getTitle().isEmpty()){
            AddNotePage.NOTE_TITLE_FIELD.resolveFor(actor).type(note.getTitle());
        }
        if(!note.getDescription().isEmpty()){
            AddNotePage.NOTE_DESCRIPTION_FIELD.resolveFor(actor).type(note.getDescription());
        }
        if(note.getWithPicture()){
            AddNotePage.MORE_OPTIONS.resolveFor(actor).click();
            AddNotePage.ADD_PICTURE_OPTION.resolveFor(actor).click();
            CameraViewerPage.SHUTTER.resolveFor(actor).click();
            CameraViewerPage.CONFIRM.resolveFor(actor).click();
        }
    }

    public static Interaction with(Note note) {
        return instrumented(EnterANote.class, note);
    }
}



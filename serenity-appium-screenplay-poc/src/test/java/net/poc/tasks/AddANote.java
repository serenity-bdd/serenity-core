package net.poc.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.*;
import net.poc.interactions.EnterANote;
import net.poc.model.Note;
import net.poc.ui.AddNotePage;
import net.poc.ui.NotesWelcomePage;
import net.thucydides.core.annotations.Step;

import java.util.ArrayList;

import static net.serenitybdd.screenplay.Tasks.instrumented;


public class AddANote implements Task {

    private final Note note;

    protected AddANote(Note note) {
        this.note = note;
    }

    @Step("{0} adds a note")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Click.on(NotesWelcomePage.ADD_NOTE_BUTTON),
                EnterANote.with(note),
                Click.on(AddNotePage.CONFIRM_ADD_NOTE_BUTTON)
        );
    }

    public static AddANote with(Note note) {
        return instrumented(AddANote.class, note);
    }
}


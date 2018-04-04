package net.poc.tasks;

import net.poc.model.Note;
import net.poc.ui.finder.NoteFinder;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.thucydides.core.annotations.Step;

import java.util.Optional;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class See  implements Task {
    private final Note note;

    protected See(Note note) {
        this.note = note;
    }

    @Step("{0} sees the new note details")
    @Override
    public <T extends Actor> void performAs(T actor) {
        Optional<WebElementFacade> noteInUI = NoteFinder.getNoteInDashboard(note, actor);
        actor.attemptsTo(
                Click.on(noteInUI.get())
        );

    }

    public static See noteDetails(Note note) {
        return instrumented(See.class, note);
    }
}

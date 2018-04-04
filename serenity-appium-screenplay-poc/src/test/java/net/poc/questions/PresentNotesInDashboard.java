package net.poc.questions;

import net.poc.model.Note;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.poc.ui.builder.NoteBuilder;

import java.util.List;

public class PresentNotesInDashboard implements Question<List<Note>> {
    @Override
    public List<Note> answeredBy(Actor actor) {
        return  NoteBuilder.getNoteListInUI(actor);
    }
}

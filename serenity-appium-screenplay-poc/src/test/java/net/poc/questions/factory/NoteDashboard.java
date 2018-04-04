package net.poc.questions.factory;

import net.serenitybdd.screenplay.Question;
import net.poc.model.Note;
import net.poc.questions.NumberOfNotesInDashboard;
import net.poc.questions.PresentNotesInDashboard;

import java.util.List;

public class NoteDashboard {
    public static Question<Integer> numberOfNotes() {
        return new NumberOfNotesInDashboard();
    }

    public static Question<List<Note>> displayed() {
        return new PresentNotesInDashboard();
    }
}

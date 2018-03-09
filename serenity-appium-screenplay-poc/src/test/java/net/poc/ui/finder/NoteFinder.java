package net.poc.ui.finder;

import io.appium.java_client.MobileBy;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.poc.model.Note;
import net.poc.ui.NotesWelcomePage;

import java.util.List;
import java.util.Optional;

public class NoteFinder {

    public static Optional<WebElementFacade> getNoteInDashboard(Note note, Actor actor){
        List<WebElementFacade> notesInUI = NotesWelcomePage.NOTES.resolveAllFor(actor);
        for (WebElementFacade noteInUI : notesInUI){
            String titleNote = noteInUI.containsElements(MobileBy.AndroidUIAutomator(NotesWelcomePage.TITLE_NOTE_SELECTOR)) ?
                    noteInUI.findElement(MobileBy.AndroidUIAutomator(NotesWelcomePage.TITLE_NOTE_SELECTOR)).getText() : "";
            String descriptionNote = noteInUI.containsElements(MobileBy.AndroidUIAutomator((NotesWelcomePage.DESCRIPTION_NOTE_SELECTOR))) ?
                    noteInUI.findElement(MobileBy.AndroidUIAutomator(NotesWelcomePage.DESCRIPTION_NOTE_SELECTOR)).getText() : "";
            if (titleNote.equals(note.getTitle()) && descriptionNote.equals(note.getDescription())){
                return Optional.of(noteInUI);
            }
        }
        return Optional.empty();
    }

}

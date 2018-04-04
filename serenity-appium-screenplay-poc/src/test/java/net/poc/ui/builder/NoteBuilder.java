package net.poc.ui.builder;

import io.appium.java_client.MobileBy;
import net.poc.model.Note;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.poc.ui.NotesWelcomePage;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class NoteBuilder {
    public static List<Note> getNoteListInUI(Actor actor) {
        List<Note> notes = new ArrayList<>();
        List<WebElementFacade> notesInUI = NotesWelcomePage.NOTES.resolveAllFor(actor);
        for (WebElementFacade noteInUI : notesInUI){
            String titleNote = noteInUI.containsElements(MobileBy.AndroidUIAutomator(NotesWelcomePage.TITLE_NOTE_SELECTOR)) ?
                    noteInUI.findElement(MobileBy.AndroidUIAutomator(NotesWelcomePage.TITLE_NOTE_SELECTOR)).getText() : "";
            String descriptionNote = noteInUI.containsElements(MobileBy.AndroidUIAutomator((NotesWelcomePage.DESCRIPTION_NOTE_SELECTOR))) ?
                    noteInUI.findElement(MobileBy.AndroidUIAutomator(NotesWelcomePage.DESCRIPTION_NOTE_SELECTOR)).getText() : "";
            notes.add(new Note(titleNote, descriptionNote));
        }
        return notes;
    }


}

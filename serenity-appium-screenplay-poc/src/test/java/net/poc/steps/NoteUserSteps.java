package net.poc.steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.poc.model.Note;
import net.poc.questions.Application;
import net.poc.questions.factory.NoteDashboard;
import net.poc.tasks.AddANote;
import net.poc.tasks.AddNotes;
import net.poc.tasks.See;
import net.poc.tasks.TryAdd;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.poc.questions.DetailNote;
import net.thucydides.core.annotations.Managed;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.*;

public class NoteUserSteps {

    @Managed(driver = "Appium")
    public WebDriver herMobileDevice;
    private Note note = new Note();
    List<Note> notes = new ArrayList<>();

    @Before
    public void set_the_stage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @Given("that (.*) has a note list containing only the default notes$")
    public void that_Andrei_has_a_note_list_containing_only_the_default_notes(String actorName) {
        theActorCalled(actorName).can(BrowseTheWeb.with(herMobileDevice));
    }

    @When("^s?he creates a note with valid and complete information$")
    public void createsANoteWithValidAndCompleteInformation() {
        this.note = new Note.NoteBuilder().called("Test Note").
                withDescription("Description Test").build();
        theActorInTheSpotlight().attemptsTo(AddANote.with(note));
    }

    @Then("^(?:his|her|the) note list should contains the new note$")
    public void noteListShouldContainsTheNewNote() {
        theActorInTheSpotlight().should(
            seeThat(NoteDashboard.numberOfNotes(),is(3)),
            seeThat(NoteDashboard.displayed(), hasItem(note))
        );
    }

    @When("^s?he creates a note without a title$")
    public void heCreatesANoteWithoutATitle() {
        Note note = new Note.NoteBuilder().build();
        this.note = note;
        theActorInTheSpotlight().attemptsTo(AddANote.with(note));
    }

    @Then("^s?he should see this warning message: \"([^\"]*)\"$")
    public void heShouldSeeThisWarningMessage(String expectedMessage) {
        theActorInTheSpotlight().should(
                seeThat(Application.notification(), is(expectedMessage))
        );
    }

    @When("^s?he creates a note only with blanks as title$")
    public void heCreatesANoteOnlyWithBlanksAsTitle() {
        this.note = new Note.NoteBuilder().called("          ").build();
        theActorInTheSpotlight().attemptsTo(AddANote.with(note));
    }

    @When("^s?he creates a note with a picture$")
    public void heCreatesANoteWithAPicture() {
        this.note = new Note.NoteBuilder().called("Test Note with picture").
                withDescription("Description").withPicture().build();
        theActorInTheSpotlight().attemptsTo(AddANote.with(note));
    }

    @And("^the note detail should contains a picture$")
    public void theNoteDetailShouldContainsAPicture() {
        theActorInTheSpotlight().attemptsTo(See.noteDetails(note));
        theActorInTheSpotlight().should(
                seeThat(DetailNote.hasPicture())
        );
    }

    @When("^s?he tries to create a note and cancels$")
    public void heTriesToCreateANoteAndCancels() {
        theActorInTheSpotlight().attemptsTo(TryAdd.andCancel());
    }

    @Then("^(?:his|her|the) note list should contains only the default notes$")
    public void hisNoteListShouldContainsOnlyTheDefaultNotes() {
        theActorInTheSpotlight().should(
                seeThat(NoteDashboard.numberOfNotes(),is(2))
        );
    }

    @When("^s?he creates (\\d+) notes with valid and complete information$")
    public void heCreatesMoreThanNotesWithValidAndCompleteInformation(int numberOfNewNotes) {
        for(int i = 0; i<numberOfNewNotes; i++)
            this.notes.add(new Note.NoteBuilder().called("Note #"+(i+1)).withDescription("Description #"+(i+1)).build());
        theActorInTheSpotlight().attemptsTo(
                AddNotes.with(notes)
        );
    }

    @Then("^(?:his|her|the) note list should contains all the new notes$")
    public void hisNoteListShouldContainsAllTheNewNotes() {
        Note[] notesInArray = notes.toArray(new Note[0]);
        theActorInTheSpotlight().should(
                seeThat(NoteDashboard.displayed(), hasItems(notesInArray))
        );
    }
}

/*package net.serenitybdd.tutorials.features.search;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import Note;
import NoteDashboard;
import Add;
import net.thucydides.core.annotations.Managed;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.net.MalformedURLException;

import static net.serenitybdd.screenplay.GivenWhenThen.*;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@RunWith(SerenityRunner.class)
public class SearchByKeywordStory {

    Actor anna = Actor.named("Anna");
    static AppiumDriverLocalService appiumService = null;

    @Managed(driver = "Appium")
    public WebDriver herMobileDevice;

    @BeforeClass
    public static void startAppiumServer() throws IOException {
        appiumService = AppiumDriverLocalService.buildDefaultService();
        appiumService.start();
    }

    @Before
    public void annaCanBrowseTheMobileApp() throws MalformedURLException {
        givenThat(anna.can(BrowseTheWeb.with(herMobileDevice)));
    }

    @AfterClass
    public static void stopAppiumServer() {
        appiumService.stop();
    }

    @Test
    public void add_a_note_with_title_and_description() {
        Note note = new Note.NoteBuilder().called("Test Note").withDescription("Description Test").build();
        anna.attemptsTo(Add.a(note));
        then(anna).should(
            seeThat(NoteDashboard.numberOfNotes(),is(3)),
            seeThat(NoteDashboard.displayed(), hasItem(note))
        );
    }

    @Test
    public void add_a_note_with_title() {
        Note note = new Note.NoteBuilder().called("Title Test").build();
        anna.attemptsTo(Add.a(note));
        then(anna).should(
                seeThat(NoteDashboard.numberOfNotes(),is(3)),
                seeThat(NoteDashboard.displayed(), hasItem(note))
        );
    }

    @Test
    public void add_a_note_with_description() {
        Note note = new Note.NoteBuilder().withDescription("Description Test").build();
        anna.attemptsTo(Add.a(note));
        then(anna).should(
                seeThat(NoteDashboard.numberOfNotes(),is(3)),
                seeThat(NoteDashboard.hasANote(note))
        );
    }


}*/
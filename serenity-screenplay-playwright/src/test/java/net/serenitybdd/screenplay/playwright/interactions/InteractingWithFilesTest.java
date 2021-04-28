package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class InteractingWithFilesTest {
    Actor danny = Actor.named("Danny").whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

    @Test
    public void pressKeyboardButton() {
        String uploadResultSelector = ".template-upload";
        danny.attemptsTo(
                Open.url("https://blueimp.github.io/jQuery-File-Upload/"),
                Upload.file("src/test/resources/files/cucumber.jpg").to("[type=file]"),
                WaitFor.selector(uploadResultSelector),
                Ensure.that(uploadResultSelector).isVisible()
        );
    }
}

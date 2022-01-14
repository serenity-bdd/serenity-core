package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.thucydides.core.annotations.Step;

import java.nio.file.Paths;

/**
 * Upload file using file chooser.
 * More info at https://playwright.dev/java/docs/api/class-page/#pageonfilechooserhandler
 */
public class Upload implements Performable {

    private String path;
    private Target target;

    public Upload(String path) {
        this.path = path;
    }

    public static Upload file(String path) {
        return new Upload(path);
    }

    public Performable to(String selector) {
        this.target = Target.the(selector).locatedBy(selector);
        return this;
    }

    public Performable to(Target target) {
        this.target = target;
        return this;
    }

    @Override
    @Step("{0} uploads file from #path to #target")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright.as(actor).getCurrentPage().onFileChooser(fileChooser ->
                fileChooser.setFiles(Paths.get(path))
        );
        actor.attemptsTo(Click.on(target));
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}

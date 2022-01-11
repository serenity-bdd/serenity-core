package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Keyboard;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.StringUtils;

/**
 * Press keyboard key(s).
 * More info: https://playwright.dev/java/docs/api/class-keyboard#keyboardpresskey-options
 */
public class Press implements Performable {

    private final String keysSequence;
    private Keyboard.PressOptions options;

    public Press(String keysSequence) {
        this.keysSequence = keysSequence;
    }

    public static Press keys(String... keys) {
        return new Press(StringUtils.joinWith("+", keys));
    }

    public Performable withOptions(Keyboard.PressOptions options) {
        this.options = options;
        return this;
    }

    @Override
    @Step("{0} presses '#keysSequence' keys combination")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright.as(actor).getCurrentPage().keyboard().press(keysSequence, options);
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}

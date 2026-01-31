package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.function.Consumer;

/**
 * Handle JavaScript dialogs (alert, confirm, prompt).
 *
 * <p>This interaction sets up a dialog handler that will be triggered
 * when the next dialog appears. Must be called BEFORE the action that
 * triggers the dialog.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     actor.attemptsTo(
 *         HandleDialog.byAccepting(),
 *         Click.on("#trigger-alert")
 *     );
 *
 *     actor.attemptsTo(
 *         HandleDialog.byDismissing(),
 *         Click.on("#trigger-confirm")
 *     );
 *
 *     actor.attemptsTo(
 *         HandleDialog.byEntering("some text"),
 *         Click.on("#trigger-prompt")
 *     );
 * </pre>
 */
public class HandleDialog implements Performable {

    private final Consumer<Dialog> dialogHandler;
    private final String description;

    private HandleDialog(Consumer<Dialog> dialogHandler, String description) {
        this.dialogHandler = dialogHandler;
        this.description = description;
    }

    /**
     * Accept the next dialog (clicks OK/Accept).
     */
    public static HandleDialog byAccepting() {
        return new HandleDialog(Dialog::accept, "accepting");
    }

    /**
     * Dismiss the next dialog (clicks Cancel/Dismiss).
     */
    public static HandleDialog byDismissing() {
        return new HandleDialog(Dialog::dismiss, "dismissing");
    }

    /**
     * Enter text into the next prompt dialog and accept it.
     */
    public static HandleDialog byEntering(String text) {
        return new HandleDialog(dialog -> dialog.accept(text), "entering '" + text + "' into");
    }

    @Override
    @Step("{0} handles dialog by " + "#description")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.onceDialog(dialogHandler);
    }

    @Override
    public String toString() {
        return "handle dialog by " + description;
    }
}

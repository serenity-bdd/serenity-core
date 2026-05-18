package net.serenitybdd.screenplay.ensure.visual;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * A Question that takes a screenshot using WebDriver and returns it as a byte array.
 */
public class ScreenshotQuestion implements Question<byte[]> {

    private final Target target;
    private final boolean fullPage;

    public ScreenshotQuestion(Target target, boolean fullPage) {
        this.target = target;
        this.fullPage = fullPage;
    }

    public static ScreenshotQuestion ofPage() {
        return new ScreenshotQuestion(null, true);
    }

    public static ScreenshotQuestion of(Target target) {
        return new ScreenshotQuestion(target, false);
    }

    @Override
    public byte[] answeredBy(Actor actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        if (fullPage) {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } else {
            return target.resolveFor(actor).getScreenshotAs(OutputType.BYTES);
        }
    }
}

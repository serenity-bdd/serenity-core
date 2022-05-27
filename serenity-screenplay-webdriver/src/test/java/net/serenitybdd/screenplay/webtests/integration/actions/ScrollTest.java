package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class ScrollTest extends ScreenplayInteractionTestBase {

    private final static Target TITLE_UNDER_THE_FOLD = PageElement.withNameOrId("under-the-fold");

    @Test
    public void scrollToUsingATarget() {
        dina.attemptsTo(Scroll.to(TITLE_UNDER_THE_FOLD));
    }

    @Test
    public void scrollToUsingALocator() {
        dina.attemptsTo(Scroll.to(By.id("under-the-fold")));
    }

    @Test
    public void scrollToUsingCss() {
        dina.attemptsTo(Scroll.to("#under-the-fold"));
    }


    @Test
    public void scrollToUsingATargetAndAlignToTheBottom() {
        dina.attemptsTo(Scroll.to(TITLE_UNDER_THE_FOLD).andAlignToBottom());
    }

    @Test
    public void scrollToUsingATargetAndAlignToTheTop() {
        dina.attemptsTo(Scroll.to(TITLE_UNDER_THE_FOLD).andAlignToTop());
    }

}

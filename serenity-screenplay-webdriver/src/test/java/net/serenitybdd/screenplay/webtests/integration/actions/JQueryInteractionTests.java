package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.AlertText;
import net.serenitybdd.screenplay.actions.ContextClick;
import net.serenitybdd.screenplay.actions.Drag;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class JQueryInteractionTests extends ScreenplayInteractionTestBase {

    private final static Target FROM = PageElement.withNameOrId("draggable").inIFrame(By.cssSelector(".demo-frame"));
    private final static Target TO = PageElement.withNameOrId("droppable").inIFrame(By.cssSelector(".demo-frame"));

    @Test
    public void draggingAnElementToATarget() {

        dina.attemptsTo(
                Open.url("https://jqueryui.com/droppable/"),
                Drag.from(FROM).to(TO)
        );

        String droppedText = dina.asksFor(Text.of(TO));

        assertThat(droppedText).contains("Dropped!");
    }

    @Test
    public void contextClick() {

    }
    private final static Target ITEM_1 = PageElement.withCSSClass("ui-selectee").containingText("Item 1").inIFrame(By.cssSelector(".demo-frame"));
    private final static Target ITEM_2 = PageElement.withCSSClass("ui-selectee").containingText("Item 2").inIFrame(By.cssSelector(".demo-frame"));
    private final static Target ITEM_3 = PageElement.withCSSClass("ui-selectee").containingText("Item 3").inIFrame(By.cssSelector(".demo-frame"));

    @Test
    public void mouseInteractions() {
        dina.attemptsTo(
                Open.url("https://the-internet.herokuapp.com/context_menu"),
                ContextClick.on("#hot-spot")
        );

        String alertText = dina.asksFor(AlertText.currentlyVisible().thenDismiss());

        assertThat(alertText).contains("You selected a context menu");
    }
}

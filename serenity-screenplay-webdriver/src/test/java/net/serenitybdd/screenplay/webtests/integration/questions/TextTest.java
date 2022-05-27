package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.Attribute;
import net.serenitybdd.screenplay.questions.CSSValue;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class TextTest extends ScreenplayInteractionTestBase {

    private final static Target FIRST_NAME_FIELD = PageElement.withNameOrId("first");
    private final static Target FULL_NAME = PageElement.withNameOrId("first-and-last-name");
    private final static Target NAME_ELEMENTS = PageElement.withCSSClass("name");

    @Test
    public void checkForTextOfAFieldUsingATarget() {
        assertThat(dina.asksFor(Text.of(FIRST_NAME_FIELD))).isEqualTo("Sarah-Jane");
    }

    @Test
    public void checkForTextOfAFieldUsingALocator() {
        assertThat(dina.asksFor(Text.of("#first"))).isEqualTo("Sarah-Jane");
    }

    @Test
    public void checkForTextOfAFieldUsingAByLocator() {
        assertThat(dina.asksFor(Text.of(By.id("first")))).isEqualTo("Sarah-Jane");
    }

    @Test
    public void checkForTextOfANestedElement() {
        assertThat(dina.asksFor(Text.of(FULL_NAME))).isEqualTo("Name: Sarah-Jane Smith");
    }


    @Test
    public void checkForTextOfSeveralFieldsUsingATarget() {
        assertThat(dina.asksFor(Text.ofEach(NAME_ELEMENTS))).containsExactly("Sarah-Jane","Smith");
    }

    @Test
    public void checkForTextOfSeveralFieldsUsingALocator() {
        assertThat(dina.asksFor(Text.ofEach(".name"))).containsExactly("Sarah-Jane","Smith");
    }

    @Test
    public void checkForTextOfSeveralFieldsUsingAByLocator() {
        assertThat(dina.asksFor(Text.ofEach(By.cssSelector(".name")))).containsExactly("Sarah-Jane","Smith");
    }


}

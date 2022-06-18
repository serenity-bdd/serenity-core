package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.questions.TextValue;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class TextValueTest extends ScreenplayInteractionTestBase {

    private final static Target FIRST_NAME_FIELD = PageElement.withNameOrId("first");
    private final static Target FULL_NAME = PageElement.withNameOrId("first-and-last-name");
    private final static Target NAME_ELEMENTS = PageElement.withCSSClass("name");

    @Test
    public void checkForTextValueOfAFieldUsingATarget() {
        assertThat(dina.asksFor(TextValue.of(FIRST_NAME_FIELD))).isEqualTo("Sarah-Jane");
    }

    @Test
    public void checkForTextValueOfAFieldUsingALocator() {
        assertThat(dina.asksFor(TextValue.of("#first"))).isEqualTo("Sarah-Jane");
    }

    @Test
    public void checkForTextValueOfAFieldUsingAByLocator() {
        assertThat(dina.asksFor(TextValue.of(By.id("first")))).isEqualTo("Sarah-Jane");
    }

    @Test
    public void checkForTextValueOfANestedElement() {
        assertThat(dina.asksFor(TextValue.of(FULL_NAME))).isEqualTo("Name: Sarah-Jane Smith");
    }


    @Test
    public void checkForTextValueOfSeveralFieldsUsingATarget() {
        assertThat(dina.asksFor(TextValue.ofEach(NAME_ELEMENTS))).containsExactly("Sarah-Jane","Smith");
    }

    @Test
    public void checkForTextValueOfSeveralFieldsUsingALocator() {
        assertThat(dina.asksFor(TextValue.ofEach(".name"))).containsExactly("Sarah-Jane","Smith");
    }

    @Test
    public void checkForTextValueOfSeveralFieldsUsingAByLocator() {
        assertThat(dina.asksFor(TextValue.ofEach(By.cssSelector(".name")))).containsExactly("Sarah-Jane","Smith");
    }

    @Test
    public void checkForValueOfAFieldUsingALocator() {
        assertThat(dina.asksFor(TextValue.of("#firstName"))).isEqualTo("Jo Grant");
    }

    @Test
    public void checkForValueOfAFieldUsingAByLocator() {
        assertThat(dina.asksFor(TextValue.of(By.id("firstName")))).isEqualTo("Jo Grant");
    }

    @Test
    public void checkForValueOfAFieldWithNoValueAttribute() {
        assertThat(dina.asksFor(TextValue.of(By.id("nickname")))).isEqualTo("");
    }

    @Test
    public void checkForValueOfSeveralFieldsUsingATarget() {
        assertThat(dina.asksFor(TextValue.ofEach("#firstName"))).containsExactly("Jo Grant");
    }

}

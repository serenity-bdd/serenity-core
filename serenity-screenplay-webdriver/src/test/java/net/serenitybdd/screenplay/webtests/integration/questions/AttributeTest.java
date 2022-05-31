package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.Absence;
import net.serenitybdd.screenplay.questions.Attribute;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class AttributeTest extends ScreenplayInteractionTestBase {

    private final static Target FIRST_NAME_FIELD = PageElement.withNameOrId("firstName");
    private final static Target COLOR_OPTIONS = PageElement.withCSSClass("color-option");

    @Test
    public void checkForAnAttributeOfAFieldUsingATarget() {
        assertThat(dina.asksFor(Attribute.of(FIRST_NAME_FIELD).named("placeholder"))).isEqualTo("First Name");
    }

    @Test
    public void checkForAnAttributeOfAFieldUsingALocator() {
        assertThat(dina.asksFor(Attribute.of("#firstName").named("placeholder"))).isEqualTo("First Name");
    }

    @Test
    public void checkForAnAttributeOfAFieldUsingAByLocator() {
        assertThat(dina.asksFor(Attribute.of(By.id("firstName")).named("placeholder"))).isEqualTo("First Name");
    }


    @Test
    public void checkForAnAttributeOfSeveralFieldsUsingATarget() {
        assertThat(dina.asksFor(Attribute.ofEach(COLOR_OPTIONS).named("value"))).containsExactly("red","green","blue");
    }

    @Test
    public void checkForAnAttributeOfSeveralFieldsUsingALocator() {
        assertThat(dina.asksFor(Attribute.ofEach(".color-option").named("value"))).containsExactly("red","green","blue");
    }

    @Test
    public void checkForAnAttributeOfSeveralFieldsUsingAByLocator() {
        assertThat(dina.asksFor(Attribute.ofEach(By.cssSelector(".color-option")).named("value"))).containsExactly("red","green","blue");
    }


}

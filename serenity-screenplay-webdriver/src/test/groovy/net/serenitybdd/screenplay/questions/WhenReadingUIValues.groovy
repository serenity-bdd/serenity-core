package net.serenitybdd.screenplay.questions

import net.serenitybdd.core.pages.WebElementFacade
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.exceptions.UnexpectedEnumValueException
import net.serenitybdd.screenplay.targets.Target
import org.joda.time.DateTime
import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
import spock.lang.Specification

class WhenReadingUIValues extends Specification {

    def target = Mock(Target)
    def element = Mock(WebElementFacade)
    def element2 = Mock(WebElementFacade)
    def actor = Actor.named("James")

    def setup() {
        target.resolveFor(actor) >> element
        target.resolveAllFor(actor) >> [element, element2]
    }

    def "should read string values"() {
        when:
            element.getValue() >> "some value"
        then:
            Value.of(target).viewedBy(actor).as(String) == "some value"
    }

    def "questions about string values"() {
        when:
        element.getValue() >> "some value"
        def theValue = Value.of(target).asAString();
        then:
        theValue.answeredBy(actor) == "some value"
    }

    def "should read string values when specifying the type"() {
        when:
            element.getValue() >> "some value"
        then:
            Value.of(target).viewedBy(actor).as(String) == "some value"
    }

    def "should read boolean values"() {
        when:
            element.getValue() >> "true"
        then:
            Value.of(target).viewedBy(actor).asBoolean() == true
        and:
            Value.of(target).viewedBy(actor).as(Boolean) == true
    }

    def "should convert string values to numerical types"() {
        when:
            element.getValue() >> "1"
        then:
            Value.of(target).viewedBy(actor).as(Integer) == 1
            Value.of(target).viewedBy(actor).as(Long) == 1L
            Value.of(target).viewedBy(actor).as(Double) == 1.0D
            Value.of(target).viewedBy(actor).as(Float) == 1.0F
            Value.of(target).viewedBy(actor).as(BigDecimal) == new BigDecimal("1.0")
    }

    def "should convert string values to numerical types using shortcut methods"() {
        when:
        element.getValue() >> "1"
        then:
        Value.of(target).viewedBy(actor).asInteger() == 1
        Value.of(target).viewedBy(actor).asDouble() == 1.0D
    }

    def "should convert string values to dates"() {
        when:
            element.getValue() >> "2015-10-15"
        then:
        def question = Value.of(target).asADate();
            Value.of(target).viewedBy(actor).asDate() == new DateTime(2015,10,15,0,0)
    }

    def "should convert string values to formatted dates"() {
        when:
            element.getValue() >> "15/10/2015"
        then:
           Value.of(target).viewedBy(actor).asDate("dd/MM/yyyy") == new DateTime(2015,10,15,0,0)
    }

    static enum Color { red, blue }

    def "should convert string values to other enums"() {
        when:
            element.getValue() >> "red"
        then:
            Value.of(target).viewedBy(actor).asEnum(Color.class) == Color.red
    }

    def "should throw assertion error if the enum is not an expected value"() {
        given:
            element.getValue() >> "spicy"
        when:
            Value.of(target).viewedBy(actor).asEnum(Color.class)
        then:
            thrown(UnexpectedEnumValueException)
    }

    def "should convert lists of string values"() {
        when:
            element.getValue() >> "A"
            element2.getValue() >> "B"
        then:
            Value.of(target).viewedBy(actor).asList() == ["A", "B"]
    }

    def "should be possible to provide a question about lists of values"() {
        when:
            element.getValue() >> "A"
            element2.getValue() >> "B"
        then:
            def theValues = Value.of(target).asAList();
            theValues.answeredBy(actor) == ["A", "B"]
    }


    def "should convert lists of string enums"() {
        when:
            element.getValue() >> "red"
            element2.getValue() >> "blue"
        then:
            Value.of(target).viewedBy(actor).asListOf(Color) == [Color.red, Color.blue]
    }

    def "questions about lists of string enums"() {
        when:
        element.getValue() >> "red"
        element2.getValue() >> "blue"
        then:
        def theValues = Value.of(target).asAListOf(Color)
        theValues.answeredBy(actor) == [Color.red, Color.blue]
    }

    def "should throw exception if an invalid enum is found"() {
        given:
            element.getValue() >> "red"
            element2.getValue() >> "spicy"
        when:
            Value.of(target).viewedBy(actor).asListOf(Color) == [Color.red, Color.blue]
        then:
            thrown(UnexpectedEnumValueException)
    }

    def "should read text values"() {
        when:
            element.getText() >> "some value"
        then:
            Text.of(target).viewedBy(actor).as(String) == "some value"
    }

    def "should read multiple text values"() {
        when:
            element.getText() >> "some value"
            element2.getText() >> "some other value"
        then:
            Text.of(target).viewedBy(actor).asList() == ["some value", "some other value"]
    }

    def "should read native text values"() {
        when:
            element.getText() >> "some value"
        then:
            Text.of(target).viewedBy(actor).value() == "some value"
    }

    def "should read selected values"() {
        when:
            element.getSelectedValue() >> "some value"
        then:
            SelectedValue.of(target).viewedBy(actor).as(String) == "some value"
    }

    def "should read multiple selected values"() {
        when:
            element.getSelectedValue() >> "some value"
            element2.getSelectedValue() >> "some other value"
        then:
            SelectedValue.of(target).viewedBy(actor).asList() == ["some value", "some other value"]
    }

    def "should read visible selected values"() {
        when:
        element.getSelectedVisibleTextValue() >> "some value"
        then:
        SelectedVisibleTextValue.of(target).viewedBy(actor).as(String) == "some value"
    }

    def "should read multiple svisible elected values"() {
        when:
        element.getSelectedVisibleTextValue() >> "some value"
        element2.getSelectedVisibleTextValue() >> "some other value"
        then:
        SelectedVisibleTextValue.of(target).viewedBy(actor).asList() == ["some value", "some other value"]
    }

    def "should read selected options as string"() {
        when:
            element.getSelectOptions() >> ["value1", "value2"]
        then:
            SelectOptions.of(target).viewedBy(actor).as(String) == "[value1, value2]"
    }

    def "should read selected options"() {
        when:
            element.getSelectOptions() >> ["value1", "value2"]
        then:
            SelectOptions.of(target).viewedBy(actor).value() == ["value1", "value2"]
    }

    def "should read multiple selected options"() {
        when:
            element.getSelectOptions() >> ["value1", "value2"]
            element2.getSelectOptions() >> ["value3", "value4"]
        then:
            SelectOptions.of(target).viewedBy(actor).asList() == [["value1", "value2"], ["value3", "value4"]]
    }

    def "should be able to read values without calling value() directly"() {
        when:
            element.getCssValue("font") >> "Italics"
        then:

            ValueOf.the(CSSValue.of(target).named("font").viewedBy(actor)) == "Italics"
    }

    def "should read css value"() {
        when:
        element.getCssValue("font") >> "Italics"
        then:
        CSSValue.of(target).named("font").viewedBy(actor).value() == "Italics"
    }

    def "should read multiple css values"() {
        when:
            element.getCssValue("font") >> "Italics"
            element2.getCssValue("font") >> "Bold"
        then:
            CSSValue.of(target).named("font").viewedBy(actor).asList() == ["Italics", "Bold"]
    }

    def "should read attribute value"() {
        when:
            element.getAttribute("checked") >> "true"
        then:
            Attribute.of(target).named("checked").viewedBy(actor).as(Boolean) == true
    }

    def "should read multiple attribute values"() {
        when:
            element.getAttribute("checked") >> "true"
            element2.getAttribute("checked") >> "false"
        then:
            Attribute.of(target).named("checked").viewedBy(actor).asListOf(Boolean.class) == [Boolean.TRUE, Boolean.FALSE]
    }

    def "should read size value"() {
        when:
            element.getSize() >> new Dimension(10,10)
        then:
            TheSize.of(target).viewedBy(actor).value() == new Dimension(10,10)
    }

    def "should read multiple size values"() {
        when:
            element.getSize() >> new Dimension(10,10)
            element2.getSize() >> new Dimension(20,20)
        then:
            TheSize.of(target).viewedBy(actor).asList() == [new Dimension(10,10), new Dimension(20,20)]
    }

    def "should read location value"() {
        when:
            element.getLocation() >> new Point(10,10)
        then:
            TheLocation.of(target).viewedBy(actor).value() == new Point(10,10)
    }

    def "should read multiple location values"() {
        when:
            element.getLocation() >> new Point(10,10)
            element2.getLocation() >> new Point(20,20)
        then:
            TheLocation.of(target).viewedBy(actor).asList() == [new Point(10,10), new Point(20,20)]
    }

    def coordinates1 = Mock(org.openqa.selenium.interactions.internal.Coordinates);
    def coordinates2 = Mock(org.openqa.selenium.interactions.internal.Coordinates);

    def "should read coordinated value"() {
        when:
            element.getCoordinates() >> coordinates1
        then:
            TheCoordinates.of(target).viewedBy(actor).value() == coordinates1
    }

    def "should read multiple coordinated values"() {
        when:
            element.getCoordinates() >> coordinates1
            element2.getCoordinates() >> coordinates2
        then:
            TheCoordinates.of(target).viewedBy(actor).asList() == [coordinates1, coordinates2]
    }

    def "should see if enabled"() {
        when:
            element.isEnabled() >> true
        then:
            Enabled.of(target).viewedBy(actor).value()== true
    }

    def "should see if selected"() {
        when:
            element.isSelected() >> true
        then:
            SelectedStatus.of(target).viewedBy(actor).value()== true
    }

    def "should see if multiple fields are selected"() {
        when:
            element.isSelected() >> true
            element2.isSelected() >> false
        then:
            SelectedStatus.of(target).viewedBy(actor).asList()== [true, false]
    }

    def "should see if multiple elements enabled"() {
        when:
            element.isEnabled() >> true
            element2.isEnabled() >> false
        then:
            Enabled.of(target).viewedBy(actor).asList() == [true, false]
    }


    def "should see if currently enabled"() {
        when:
            element.isCurrentlyEnabled() >> true
        then:
            CurrentlyEnabled.of(target).viewedBy(actor).value()== true
    }

    def "should see if multiple elements currently enabled"() {
        when:
            element.isCurrentlyEnabled() >> true
            element2.isCurrentlyEnabled() >> false
        then:
        CurrentlyEnabled.of(target).viewedBy(actor).asList() == [true, false]
    }

    def "should see if currently visible"() {
        when:
        element.isCurrentlyVisible() >> true
        then:
        CurrentVisibility.of(target).viewedBy(actor).value()== true
    }

    def "should see if multiple elements currently visible"() {
        when:
        element.isCurrentlyVisible() >> true
        element2.isCurrentlyVisible() >> false
        then:
        CurrentVisibility.of(target).viewedBy(actor).asList() == [true, false]
    }


    def "should see if visible"() {
        when:
            element.isVisible() >> true
        then:
            Visibility.of(target).viewedBy(actor).value()== true
    }

    def "should see if multiple elements visible"() {
        when:
            element.isVisible() >> true
            element2.isVisible() >> false
        then:
            Visibility.of(target).viewedBy(actor).asList() == [true, false]
    }


    def "should see if present"() {
        when:
        element.isPresent() >> true
        then:
        Presence.of(target).viewedBy(actor).value()== true
    }

    def "should see if multiple elements present"() {
        when:
        element.isPresent() >> true
        element2.isPresent() >> false
        then:
        Presence.of(target).viewedBy(actor).asList() == [true, false]
    }


}

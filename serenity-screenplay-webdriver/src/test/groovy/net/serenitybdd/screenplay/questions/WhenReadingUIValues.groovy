package net.serenitybdd.screenplay.questions

import net.serenitybdd.core.pages.WebElementFacade
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.targets.Target

import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
import org.openqa.selenium.interactions.Coordinates
import spock.lang.Specification

import java.time.LocalDate

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
        Value.of(target).answeredBy(actor) == "some value"
    }

    def "questions about string values"() {
        when:
        element.getValue() >> "some value"
        def theValue = Value.of(target).asString();
        then:
        theValue.answeredBy(actor) == "some value"
    }

    def "should read string values when specifying the type"() {
        when:
        element.getValue() >> "some value"
        then:
        Value.of(target).answeredBy(actor) == "some value"
    }

    def "should read boolean values"() {
        when:
        element.getValue() >> "true"
        then:
        Value.of(target).asBoolean().answeredBy(actor) == true
        and:
        Value.of(target).as(Boolean).answeredBy(actor) == true
    }

    def "should convert string values to numerical types"() {
        when:
        element.getValue() >> "1"
        then:
        Value.of(target).as(Integer).answeredBy(actor) == 1
        Value.of(target).as(Long).answeredBy(actor) == 1L
        Value.of(target).as(Double).answeredBy(actor) == 1.0D
        Value.of(target).as(Float).answeredBy(actor) == 1.0F
        Value.of(target).as(BigDecimal).answeredBy(actor) == new BigDecimal("1.0")
    }

    def "should convert string values to numerical types using shortcut methods"() {
        when:
        element.getValue() >> "1"
        then:
        Value.of(target).asInteger().answeredBy(actor) == 1
        Value.of(target).asDouble().answeredBy(actor) == 1.0D
    }

    def "should convert string values to dates"() {
        when:
        element.getValue() >> "2015-10-15"
        then:
        Value.of(target).asADate().answeredBy(actor) == LocalDate.parse("2015-10-15")
    }

    def "should convert string values to formatted dates"() {
        when:
        element.getValue() >> "15/10/2015"
        then:
        Value.of(target).asADate("dd/MM/yyyy").answeredBy(actor) == LocalDate.parse("2015-10-15")
    }

    static enum Color {
        red, blue
    }

    def "should convert string values to other enums"() {
        when:
        element.getValue() >> "red"
        then:
        Value.of(target).asEnum(Color.class).answeredBy(actor) == Color.red
    }

    def "should convert lists of string values"() {
        when:
        element.getValue() >> "A"
        element2.getValue() >> "B"
        then:
        Value.ofEach(target).answeredBy(actor) == ["A", "B"]
    }

    def "should convert lists to other types"() {
        when:
        element.getValue() >> "1"
        element2.getValue() >> "2"
        then:
        Value.ofEach(target).asListOf(Integer).answeredBy(actor) == [1, 2]
    }

    def "should convert collections to other types"() {
        when:
        element.getValue() >> "1"
        element2.getValue() >> "2"
        then:
        Value.ofEach(target).asCollectionOf(Integer).answeredBy(actor) == [1, 2]
    }

    def "should convert lists to other types using a map"() {
        when:
        element.getValue() >> "1"
        element2.getValue() >> "2"
        then:
        Value.ofEach(target).mapEach(value -> Integer.parseInt(value)).answeredBy(actor) == [1, 2]
    }

    def "should be possible to provide a question about lists of values"() {
        when:
        element.getValue() >> "A"
        element2.getValue() >> "B"
        then:
        def theValues = Value.ofEach(target)
        theValues.answeredBy(actor) == ["A", "B"]
    }

    def "should read text values"() {
        when:
        element.getText() >> "some value"
        then:
        Text.of(target).answeredBy(actor) == "some value"
    }

    def "should read multiple text values"() {
        when:
        element.getText() >> "some value"
        element2.getText() >> "some other value"
        then:
        Text.ofEach(target).answeredBy(actor) == ["some value", "some other value"]
    }

    def "should read native text values"() {
        when:
        element.getText() >> "some value"
        then:
        Text.of(target).answeredBy(actor) == "some value"
    }

    def "should read selected values"() {
        when:
        element.getSelectedValue() >> "some value"
        then:
        SelectedValue.of(target).answeredBy(actor) == "some value"
    }

    def "should read multiple selected values"() {
        when:
        element.getSelectedValue() >> "some value"
        element2.getSelectedValue() >> "some other value"
        then:
        SelectedValue.ofEach(target).answeredBy(actor) == ["some value", "some other value"]
    }

    def "should read visible selected values"() {
        when:
        element.getSelectedVisibleTextValue() >> "some value"
        then:
        SelectedVisibleTextValue.of(target).answeredBy(actor) == "some value"
    }

    def "should read multiple svisible elected values"() {
        when:
        element.getSelectedVisibleTextValue() >> "some value"
        element2.getSelectedVisibleTextValue() >> "some other value"
        then:
        SelectedVisibleTextValue.ofEach(target).answeredBy(actor) == ["some value", "some other value"]
    }

    def "should read selected options as string"() {
        when:
        element.getSelectOptions() >> ["value1", "value2"]
        then:
        SelectOptions.of(target).asString().answeredBy(actor) == "[value1, value2]"
    }

    def "should read selected options"() {
        when:
        element.getSelectOptions() >> ["value1", "value2"]
        then:
        SelectOptions.of(target).answeredBy(actor) == ["value1", "value2"]
    }

    def "should read multiple selected options"() {
        when:
        element.getSelectOptions() >> ["value1", "value2"]
        element2.getSelectOptions() >> ["value3", "value4"]
        then:
        SelectOptions.ofEach(target).answeredBy(actor) == [["value1", "value2"], ["value3", "value4"]]
    }

    def "should be able to read values without calling value() directly"() {
        when:
        element.getCssValue("font") >> "Italics"
        then:
        CSSValue.of(target).named("font").answeredBy(actor) == "Italics"
    }

    def "should read css value"() {
        when:
        element.getCssValue("font") >> "Italics"
        then:
        CSSValue.of(target).named("font").answeredBy(actor) == "Italics"
    }

    def "should read multiple css values"() {
        when:
        element.getCssValue("font") >> "Italics"
        element2.getCssValue("font") >> "Bold"
        then:
        CSSValue.ofEach(target).named("font").answeredBy(actor) == ["Italics", "Bold"]
    }

    def "should read attribute value"() {
        when:
        element.getAttribute("checked") >> "true"
        then:
        Attribute.of(target).named("checked").as(Boolean).answeredBy(actor) == true
    }

    def "should read multiple attribute values"() {
        when:
        element.getAttribute("checked") >> "true"
        element2.getAttribute("checked") >> "false"
        then:
        Attribute.ofEach(target).named("checked").answeredBy(actor) == ["true", "false"]
    }

    def "should read size value"() {
        when:
        element.getSize() >> new Dimension(10, 10)
        then:
        TheSize.of(target).answeredBy(actor) == new Dimension(10, 10)
    }

    def "should read multiple size values"() {
        when:
        element.getSize() >> new Dimension(10, 10)
        element2.getSize() >> new Dimension(20, 20)
        then:
        TheSize.ofEach(target).answeredBy(actor) == [new Dimension(10, 10), new Dimension(20, 20)]
    }

    def "should read location value"() {
        when:
        element.getLocation() >> new Point(10, 10)
        then:
        TheLocation.of(target).answeredBy(actor) == new Point(10, 10)
    }

    def "should read multiple location values"() {
        when:
        element.getLocation() >> new Point(10, 10)
        element2.getLocation() >> new Point(20, 20)
        then:
        TheLocation.ofEach(target).answeredBy(actor) == [new Point(10, 10), new Point(20, 20)]
    }

    def coordinates1 = Mock(Coordinates)
    def coordinates2 = Mock(Coordinates)

    def "should read coordinated value"() {
        when:
        element.getCoordinates() >> coordinates1
        then:
        TheCoordinates.of(target).answeredBy(actor) == coordinates1
    }

    def "should read multiple coordinated values"() {
        when:
        element.getCoordinates() >> coordinates1
        element2.getCoordinates() >> coordinates2
        then:
        TheCoordinates.ofEach(target).answeredBy(actor) == [coordinates1, coordinates2]
    }

    def "should see if enabled"() {
        when:
        element.isEnabled() >> true
        then:
        Enabled.of(target).answeredBy(actor)
    }

    def "should see if selected"() {
        when:
        element.isSelected() >> true
        then:
        SelectedStatus.of(target).answeredBy(actor)
    }

    def "should see if unselected"() {
        when:
        element.isSelected() >> false
        then:
        UnselectedStatus.of(target).answeredBy(actor)
    }

    def "should see if multiple fields are selected"() {
        when:
        element.isSelected() >> true
        element2.isSelected() >> false
        then:
        SelectedStatus.ofEach(target).answeredBy(actor) == [true, false]
    }

    def "should see if multiple fields are not selected"() {
        when:
        element.isSelected() >> true
        element2.isSelected() >> false
        then:
        UnselectedStatus.ofEach(target).answeredBy(actor) == [false, true]
    }

    def "should see if multiple elements enabled"() {
        when:
        element.isEnabled() >> true
        element2.isEnabled() >> false
        then:
        Enabled.ofEach(target).answeredBy(actor) == [true, false]
    }


    def "should see if currently enabled"() {
        when:
        element.isCurrentlyEnabled() >> true
        then:
        CurrentlyEnabled.of(target).answeredBy(actor)
    }

    def "should see if multiple elements currently enabled"() {
        when:
        element.isCurrentlyEnabled() >> true
        element2.isCurrentlyEnabled() >> false
        then:
        CurrentlyEnabled.ofEach(target).answeredBy(actor) == [true, false]
    }

    def "should see if currently visible"() {
        when:
        element.isCurrentlyVisible() >> true
        then:
        CurrentVisibility.of(target).answeredBy(actor)
    }

    def "should see if multiple elements currently visible"() {
        when:
        element.isCurrentlyVisible() >> true
        element2.isCurrentlyVisible() >> false
        then:
        CurrentVisibility.ofEach(target).answeredBy(actor) == [true, false]
    }


    def "should see if visible"() {
        when:
        element.isVisible() >> true
        then:
        Visibility.of(target).answeredBy(actor)
    }

    def "should see if multiple elements visible"() {
        when:
        element.isVisible() >> true
        element2.isVisible() >> false
        then:
        Visibility.ofEach(target).answeredBy(actor) == [true, false]
    }


    def "should see if present"() {
        when:
        element.isPresent() >> true
        then:
        Presence.of(target).answeredBy(actor)
    }

    def "should see if multiple elements present"() {
        when:
        element.isPresent() >> true
        element2.isPresent() >> false
        then:
        Presence.ofEach(target).answeredBy(actor) == [true, false]
    }


}

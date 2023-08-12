package net.thucydides.model.domain

import net.thucydides.model.requirements.model.CustomFieldValue
import net.thucydides.model.requirements.model.Requirement
import spock.lang.Specification

class WhenModellingRequirements extends Specification {

    def "should be able to store a custom requirement field"() {
        given: "a requirement"
            def requirement = Requirement.named("some requirement").withType("story").withNarrative("narrative")
        when: "I store a custom field value"
            requirement = requirement.withCustomField("Acceptance Criteria").setTo("It should work")
        then: "I should be able to retrieve this value"
            requirement.getCustomField("Acceptance Criteria").isPresent() &&
            requirement.getCustomField("Acceptance Criteria").get().text == "It should work"
    }

    def "should be able to store the rendered form of a custom requirement field"() {
        given: "a requirement"
            def requirement = Requirement.named("some requirement").withType("story").withNarrative("narrative")
        when: "I store a custom field value"
            requirement = requirement.withCustomField("Acceptance Criteria").setTo("It should work","<p>It should work</p>")
        then: "I should be able to retrieve this value"
            requirement.getCustomField("Acceptance Criteria").isPresent() &&
            requirement.getCustomField("Acceptance Criteria").get().renderedText == "<p>It should work</p>  "
    }

    def "should be able to list the custom requirement fields"() {
        given: "a requirement"
            def requirement = Requirement.named("some requirement").withType("story").withNarrative("narrative")
        when: "I store some custom field values"
            requirement = requirement.withCustomField("Acceptance Criteria").setTo("It should work")
            requirement = requirement.withCustomField("Background").setTo("A long time ago...")
        then: "I should be able to retrieve the custom field names"
            requirement.customFields == ["Acceptance Criteria", "Background"]
    }

    def "custom fields have an optional rendered value"() {
        when: "A custom field with a rendered value"
            def customField = new CustomFieldValue("Color","*red*","<b>red</b>")
        then:
            customField.text == "*red*"
        and:
            customField.renderedText == "<b>red</b>  "
    }

    def "custom fields without a rendered value should default to the normal value"() {
        when: "A custom field with a rendered value"
            def customField = new CustomFieldValue("Color", "*red*")
        then:
            customField.renderedText == "*red*  "
    }
}

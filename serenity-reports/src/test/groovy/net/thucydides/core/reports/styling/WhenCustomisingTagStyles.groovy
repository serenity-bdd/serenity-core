package net.thucydides.core.reports.styling

import net.serenitybdd.core.reports.styling.TagStylist
import net.thucydides.model.domain.TestTag
import net.thucydides.model.util.EnvironmentVariables
import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * We can style the appearance of tags in the Serenity reports using `tag.color.for` properties.
 */
class WhenCustomisingTagStyles extends Specification {

    @Shared
    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    def "We use the 'tag.color.for' properties to configure the colors of different types of tags"() {

        given:
        environmentVariables.setProperty("tag.color.for.error", "red")
        when:
        def tag = TestTag.withValue("error:crap!")
        then:
        TagStylist.from(environmentVariables).tagStyleFor(tag) == 'background:red;'
    }

    @Unroll
    def "Different colors and formats can be defined for different tags"() {

        given:
        environmentVariables.setProperty("tag.color.for.error", "red")
        environmentVariables.setProperty("tag.color.for.warning", "#FFBF00")
        when:
        def tag = TestTag.withName("my tag").andType(tagType)
        then:
        TagStylist.from(environmentVariables).tagStyleFor(tag) == expectedTagStyle
        where:
        tagType   | expectedTagStyle
        "error"   | 'background:red;'
        "warning" | 'background:#FFBF00;'
    }

    def "Unconfigured tags are set to 'default', which will retain the normal color"() {

        given:
        environmentVariables.setProperty("tag.color.for.error", "red")
        when:
        def tag = TestTag.withValue("feature:do stuff")
        then:
        TagStylist.from(environmentVariables).tagStyleFor(tag) == 'background:default;'
    }

    @Unroll
    def "Both background and foreground colors can be defined"() {

        given:
        environmentVariables.setProperty(style, styleValue)
        when:
        def tag = TestTag.withName("my tag").andType(tagType)
        then:
        TagStylist.from(environmentVariables).tagStyleFor(tag) == expectedTagStyle
        where:
        style                   | styleValue       | tagType   | expectedTagStyle
        "tag.color.for.error"   | "red, black"     | "error"   | 'background:red; color:black;'
        "tag.color.for.error"   | "default, black" | "error"   | 'background:default; color:black;'
        "tag.color.for.warning" | "#FFBF00, white" | "warning" | 'background:#FFBF00; color:white;'
        "tag.color.for.warning" | "#FFBF00, "      | "warning" | 'background:#FFBF00;'
        "tag.color.for.warning" | ", "             | "warning" | 'background:default;'

    }
}

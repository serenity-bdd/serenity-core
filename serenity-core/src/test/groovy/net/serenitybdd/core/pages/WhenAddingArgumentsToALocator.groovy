package net.serenitybdd.core.pages

import spock.lang.Specification

class WhenAddingArgumentsToALocator extends Specification {

    def "locators with no arguments should not be changed"() {
        expect:
            ParameterisedLocator.withArguments(".name") == ".name"
    }

    def "locators with a single parameter"() {
        expect:
            ParameterisedLocator.withArguments(".name[{0}]",1) == ".name[1]"
    }

    def "locators with multiple parameters"() {
        expect:
        ParameterisedLocator.withArguments(".name[{0},{1}]",1,3) == ".name[1,3]"
    }

    def "locators with parameters in a different order"() {
        expect:
        ParameterisedLocator.withArguments(".name-{1}[{0}]",1,"color") == ".name-color[1]"
    }

    def "locators the wrong number of parameters"() {
        expect:
        ParameterisedLocator.withArguments(".name-{1}",1,"color") == ".name-color"
    }

    def "locators with invalid parameter placeholders"() {
        expect:
        ParameterisedLocator.withArguments(".name-{3}",1,"color") == ".name-{3}"
    }

}

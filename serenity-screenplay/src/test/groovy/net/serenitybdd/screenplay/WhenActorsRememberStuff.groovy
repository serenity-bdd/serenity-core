package net.serenitybdd.screenplay

import spock.lang.Specification

class WhenActorsRememberStuff extends Specification {

    def "actor should remember stuff"() {
        given:
            def actor = new Actor("Jill")
        when:
            actor.remember("favorite color","blue")
        then:
            actor.recall("favorite color") == "blue"
    }

    def "actor should remember objects"() {
        given:
            def actor = new Actor("Jill")
        when:
            actor.remember("salary",new BigDecimal("100000"))
        then:
            actor.recall("salary") == new BigDecimal("100000")
    }

}

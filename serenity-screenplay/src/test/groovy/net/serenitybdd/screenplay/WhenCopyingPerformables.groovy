package net.serenitybdd.screenplay

import spock.lang.Specification

class WhenCopyingPerformables extends Specification {

    def "all the public and private fields should be duplicated"() {
        given:
        def person1 = new Person("Jill", 40, yes);
        def person2 = new Person();
        when:
        CopyNonNullProperties.from(person1).to(person2)
        then:
        person1 == person2
    }
}

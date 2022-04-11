package net.serenitybdd.screenplay

import spock.lang.Specification

class WhenCopyingPerformables extends Specification {

    def "all the public and private fields should be duplicated"() {
        given:
        def person1 = new Person("Jill", 40, true);
        def person2 = new Person();
        when:
        CopyNonNullProperties.from(person1).to(person2)
        then:
        person1 == person2
    }

    def "static methods should be ignored"() {
        given:
        def pet1 = new Pet("spot", 4, "dog");
        def pet2 = new Pet();
        when:
        CopyNonNullProperties.from(pet1).to(pet2)
        then:
        pet1 == pet2
    }
}

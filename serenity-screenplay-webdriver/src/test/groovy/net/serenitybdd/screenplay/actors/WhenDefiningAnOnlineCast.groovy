package net.serenitybdd.screenplay.actors

import net.serenitybdd.screenplay.Ability
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.abilities.BrowseTheWeb
import spock.lang.Specification

class WhenDefiningAnOnlineCast extends Specification {

    def "An online cast should have the ability to browse the web"() {
        given:
        Cast cast = new OnlineCast()
        when:
            Actor joe = cast.actorNamed("Joe")
        then:
            BrowseTheWeb.as(joe)
    }

    static class MyAbility implements Ability {}

    def "An online cast can have other abilities too"() {
        given:
            Cast cast = OnlineCast.whereEveryoneCan( new MyAbility() )
        when:
            Actor joe = cast.actorNamed("Joe")
        then:
            joe.abilityTo(MyAbility.class) &&  BrowseTheWeb.as(joe)
    }

    def "we can also define a standard cast"() {
        given:
            Cast cast = Cast.whereEveryoneCan( new MyAbility() )
        when:
            Actor joe = cast.actorNamed("Joe")
        then:
            joe.abilityTo(MyAbility.class) && !joe.abilityTo(BrowseTheWeb.class)
    }

}

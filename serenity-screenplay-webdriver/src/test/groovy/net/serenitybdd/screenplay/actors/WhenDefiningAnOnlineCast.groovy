package net.serenitybdd.screenplay.actors

import net.serenitybdd.screenplay.Ability
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.abilities.BrowseTheWeb
import spock.lang.Specification

import java.util.function.Consumer

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
    static class MyOtherAbility implements Ability {}

    def "An online cast can have other abilities too"() {
        given:
            Cast cast = OnlineCast.whereEveryoneCan( new MyAbility(), new MyOtherAbility() )
        when:
            Actor joe = cast.actorNamed("Joe")
        then:
            joe.abilityTo(MyAbility.class) && joe.abilityTo(MyOtherAbility.class) &&  BrowseTheWeb.as(joe)
    }

    def "online cast members can be trained to do arbitrary things"() {
        given:
        Consumer<Actor> fetchTheCoffee = { actor -> actor.whoCan(Fetch.some("Coffee")) }
        Cast globeTheatreCast = OnlineCast.whereEveryoneCan(fetchTheCoffee)
        when:
        Actor kenneth = globeTheatreCast.actorNamed("Kenneth")
        then:
        kenneth.abilityTo(Fetch.class).item == "Coffee" && BrowseTheWeb.as(kenneth)
    }

    def "we can also define a cast where every cast member can perform a given ability"() {
        given:
            Cast cast = Cast.whereEveryoneCan( new MyAbility() )
        when:
            Actor joe = cast.actorNamed("Joe")
        then:
            joe.abilityTo(MyAbility.class) && !joe.abilityTo(BrowseTheWeb.class)
    }

    def "we can also define a standard cast of actors with no abilities (not very useful though)"() {
        given:
        Cast cast = Cast.ofStandardActors();
        when:
        Actor joe = cast.actorNamed("Joe")
        then:
        !joe.abilityTo(BrowseTheWeb.class)
    }

}

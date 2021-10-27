package net.serenitybdd.cucumber.screenplay

import net.serenitybdd.screenplay.actors.Cast
import net.serenitybdd.screenplay.actors.OnStage
import net.serenitybdd.screenplay.actors.OnlineCast
import spock.lang.Specification

public class WhenManagingScreenplayActorsInCucumber extends Specification {

    def "Actors are identified by their names"() {
        given:
            Cast cast = new Cast()
            OnStage.setTheStage(cast)
        when:
            def jamesDean = cast.actorNamed("James Dean")
        then:
            cast.actorNamed("James Dean") == jamesDean
    }

    def "Actors can be assigned a webdriver type"() {
        given:
            OnlineCast cast = new OnlineCast();
            OnStage.setTheStage(cast)
        when:
            def jamesDean = cast.actorUsingBrowser("chrome").named("James Dean")
        then:
            cast.actorNamed("James Dean") == jamesDean
    }


    def "An actor is only cast once"() {
        given:
            Cast cast = new Cast();
            OnStage.setTheStage(cast)
        when:
            def jamesDean = cast.actorNamed("James Dean")
        and:
            cast.actorNamed("James Dean")
        then:
            cast.actorNamed("James Dean") == jamesDean
    }

    def "Cast can be dismissed"() {
        given:
            Cast cast = new Cast();
            OnStage.setTheStage(cast)
            cast.actorNamed("James Dean")
        when:
            cast.dismissAll()
        then:
            cast.actors.isEmpty()
    }



}

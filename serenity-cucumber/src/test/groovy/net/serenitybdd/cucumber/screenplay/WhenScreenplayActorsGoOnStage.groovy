package net.serenitybdd.cucumber.screenplay

import net.serenitybdd.cucumber.actors.StageDirector
import net.serenitybdd.screenplay.actors.Cast
import net.serenitybdd.screenplay.actors.OnStage
import spock.lang.Specification

public class WhenScreenplayActorsGoOnStage extends Specification {

    def "When an actor is called he is placed in the spotlight"() {
        given:
            Cast cast = new Cast()
            OnStage.setTheStage(cast);
        when:
            def jamesDean = OnStage.theActorCalled("James Dean")
        then:
            OnStage.theActorInTheSpotlight() == jamesDean
    }

    def "When a new actor is placed in the spotlight the previous actor is no longer in the spotlight"() {
        given:
            Cast cast = new Cast()
            OnStage.setTheStage(cast);
        and:
            def jamesDean = OnStage.theActorCalled("James Dean")
        when:
            def bradPitt = OnStage.theActorCalled("Brad Pitt")
        then:
            OnStage.theActorInTheSpotlight() == bradPitt
    }

    def "At the end of the act we remove all of the actors"() {
        given:
            Cast cast = new Cast()
            OnStage.setTheStage(cast);
        and:
            OnStage.theActorCalled("James Dean")
            OnStage.theActorCalled("Brad Pitt")
        and:
            StageDirector stageDirector = new StageDirector();
        when:
            stageDirector.endTheAct()
        then:
            cast.actors.isEmpty()
    }

}

package net.serenitybdd.screenplay.actors

import net.serenitybdd.screenplay.Ability
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.HasTeardown
import net.serenitybdd.screenplay.Task
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepEventBus
import net.thucydides.model.steps.ExecutedStepDescription
import spock.lang.Specification

import java.nio.file.Files
import java.util.function.Consumer

class WhenRecruitingACast extends Specification {

    def "a cast can provide actors by name"() {
        given:
        Cast cast = new Cast()
        when:
        Actor actor = cast.actorNamed("Joe")
        then:
        actor.name == "Joe"
    }

    static class PerformShakespeare implements Ability {}

    def "cast members can be trained"() {
        given:
        Ability performShakespeare = new PerformShakespeare();
        and:
        Cast globeTheatreCast = Cast.whereEveryoneCan(performShakespeare)
        when:
        Actor laurance = globeTheatreCast.actorNamed("Laurence")
        then:
        laurance.abilityTo(PerformShakespeare.class) == performShakespeare
    }

    static class Fetch implements Ability {
        final String item
        private int counter = 1;

        Fetch(String item) {
            this.item = item
        }

        static some(String item) {
            return new Fetch(item)
        }

        String deliverItem() {
            return item + " #" + counter++;
        }
    }

    def "cast members can be trained to do arbitrary things"() {
        given:
        Consumer<Actor> fetchTheCoffee = { actor -> actor.whoCan(Fetch.some("Coffee")) }
        Cast globeTheatreCast = Cast.whereEveryoneCan(fetchTheCoffee)
        when:
        Actor kenneth = globeTheatreCast.actorNamed("Kenneth")
        then:
        kenneth.abilityTo(Fetch.class).deliverItem() == "Coffee #1"
        kenneth.abilityTo(Fetch.class).deliverItem() == "Coffee #2"
    }

    def "cast members can be trained to deliver coffee"() {
        given:
        Cast globeTheatreCast = Cast.whereEveryoneCan(Fetch.some("Coffee"))
        when:
        Actor kenneth = globeTheatreCast.actorNamed("Kenneth")
        then:
        kenneth.abilityTo(Fetch.class).deliverItem() == "Coffee #1"
        kenneth.abilityTo(Fetch.class).deliverItem() == "Coffee #2"
    }
    static class PerformHamlet implements Ability, HasTeardown {

        @Override
        void tearDown() {}
    }

    def "cast members can tidy up after themselves"() {
        given:
        PerformHamlet performHamlet = Mock(PerformHamlet.class)
        OnStage.setTheStage(Cast.whereEveryoneCan(performHamlet))
        OnStage.theActorCalled("Laurence")
        when:
        OnStage.drawTheCurtain()
        then:
        1 * performHamlet.tearDown()
    }

    def "theActor is a shorter form of 'theActorCalled'"() {
        given:
        PerformHamlet performHamlet = Mock(PerformHamlet.class)
        OnStage.setTheStage(Cast.whereEveryoneCan(performHamlet))
        OnStage.theActor("Laurence")
        when:
        OnStage.drawTheCurtain()
        then:
        1 * performHamlet.tearDown()
    }

    def "cast members can be used before knowing their names"() {
        given:
        OnStage.setTheStage(Cast.ofStandardActors())
        Actor theNextActor = OnStage.aNewActor()
        when:
        Actor kenneth = OnStage.theActorCalled("Kenneth");
        then:
        theNextActor == kenneth;
    }

    def "When a cast member is called they move into the spotlight"() {
        given:
        OnStage.setTheStage(Cast.ofStandardActors())
        Actor kenneth = OnStage.theActorCalled("Kenneth");
        when:
        Actor inTheSpotlight = OnStage.theActorInTheSpotlight()
        then:
        kenneth == inTheSpotlight;
    }


    def "We can perform actions with the current actor in the spotlight"() {
        def partPlayedBy = ""
        def playThePart = Task.where("the actor plays his part", actor -> partPlayedBy = actor.getName())

        given:
            File outputDir = Files.createTempDirectory("out").toFile()

            StepEventBus.getEventBus().registerListener(new BaseStepListener(outputDir));
            StepEventBus.getEventBus().testStarted("some test")
            StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("some test"))

            OnStage.setTheStage(Cast.ofStandardActors())
            OnStage.theActorCalled("Kenneth");
        when:
            OnStage.withCurrentActor(playThePart)
        then:
            partPlayedBy == "Kenneth"
    }

    def "We can set a new stage"() {
        given:
            Stage oldStage = OnStage.setTheStage(Cast.ofStandardActors())
            Stage newStage = new Stage(Cast.ofStandardActors())
        when:
            OnStage.setTheStage(newStage)
        then:
            OnStage.stage() == newStage
    }

}

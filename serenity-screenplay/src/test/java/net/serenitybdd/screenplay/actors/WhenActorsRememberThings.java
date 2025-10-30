package net.serenitybdd.screenplay.actors;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SerenityJUnit5Extension.class)
public class WhenActorsRememberThings {

    @Test
    public void anActorCanRememberInformation() {
        Actor actor = Actor.named("Archie");

        actor.remember("TOTAL_COST", 100);

        int totalCost = actor.recall("TOTAL_COST");

        assertThat(totalCost).isEqualTo(100);
    }

    Question<Integer> totalCost() {
        return actor -> 100;
    }

    @Test
    public void anActorCanRememberTheAnswerToAQuestion() {
        Actor actor = Actor.named("Archie");

        actor.remember("TOTAL_COST", totalCost());

        int totalCost = actor.recall("TOTAL_COST");

        assertThat(totalCost).isEqualTo(100);
    }

    @Test
    public void anActorCanForgetWhatTheyKnow() {
        Actor actor = Actor.named("Archie");

        actor.remember("TOTAL_COST", totalCost());

        actor.forget("TOTAL_COST");

        Integer totalCost = actor.recall("TOTAL_COST");

        assertThat(totalCost).isNull();
    }


    @Test
    public void anActorCanRecallEverythingTheyKnow() {
        Actor actor = Actor.named("Archie");

        actor.remember("COLOR", "Red");
        actor.remember("FLAVOUR", "Vanilla");

        Map<String, Object> memory = actor.recallAll();

        assertThat(memory.get("COLOR")).isEqualTo("Red");
        assertThat(memory.get("FLAVOUR")).isEqualTo("Vanilla");
    }
}

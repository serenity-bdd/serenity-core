package net.serenitybdd.screenplay.webtests.actors;

import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actors.Cast;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.exceptions.ActorCannotBrowseTheWebException;
import org.junit.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenDefiningAnOnlineCast {

    @Test
    public void shouldHaveTheAbilityToBrowseTheWeb() {
        Cast cast = new OnlineCast();
        Actor joe = cast.actorNamed("Joe");
        assertThat(BrowseTheWeb.as(joe)).isNotNull();
    }

    static class MyAbility implements Ability {}
    static class MyOtherAbility implements Ability {}


    @Test
    public void onlineCastMembersCanHaveOtherAbilities() {

        Cast cast = OnlineCast.whereEveryoneCan( new MyAbility(), new MyOtherAbility() );
        Actor joe = cast.actorNamed("Joe");

        assertThat(BrowseTheWeb.as(joe)).isNotNull();
        assertThat(joe.abilityTo(MyAbility.class)).isNotNull();
        assertThat(joe.abilityTo(MyOtherAbility.class)).isNotNull();
    }

    static class Fetch implements Ability {
        final String item;
        private int counter = 1;

        Fetch(String item) {
            this.item = item;
        }

        public static Fetch some(String item) {
            return new Fetch(item);
        }

        public String deliverItem() {
            return item + " #" + counter++;
        }
    }

    @Test
    public void canDoArbitraryThings() {
        Consumer<Actor> fetchTheCoffee = actor -> actor.whoCan(Fetch.some("Coffee"));
        Cast globeTheatreCast = OnlineCast.whereEveryoneCan(fetchTheCoffee);
        Actor kenneth = globeTheatreCast.actorNamed("Kenneth");
        assertThat(BrowseTheWeb.as(kenneth)).isNotNull();
        assertThat(kenneth.abilityTo(Fetch.class).item).isEqualTo("Coffee");

    }

    @Test(expected = ActorCannotBrowseTheWebException.class)
    public void weCanDefineANonWebCast() {
        Cast cast = Cast.whereEveryoneCan( new MyAbility() );
        Actor joe = cast.actorNamed("Joe");
        assertThat(joe.abilityTo(MyAbility.class)).isNotNull();
        assertThat(BrowseTheWeb.as(joe)).isNull();
    }

    @Test(expected = ActorCannotBrowseTheWebException.class)
    public void weCanDefineACastOfStandardActors() {
        Cast cast = Cast.ofStandardActors();
        Actor joe = cast.actorNamed("Joe");
        assertThat(BrowseTheWeb.as(joe)).isNull();
    }
}

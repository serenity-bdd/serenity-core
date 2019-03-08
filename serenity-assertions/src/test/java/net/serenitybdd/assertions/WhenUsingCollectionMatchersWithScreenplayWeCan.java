package net.serenitybdd.assertions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.questions.TheValue;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static net.serenitybdd.assertions.CollectionMatchers.*;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

@RunWith(SerenityRunner.class)
public class WhenUsingCollectionMatchersWithScreenplayWeCan {

    Actor collette = Actor.named("Collette");

    @Test
    public void checkForSubsets() {

        List<String> allColors = Arrays.asList("red", "blue", "green", "yellow");
        List<String> favoriteColors = Arrays.asList("red", "blue");

        collette.should(
                seeThat(TheValue.of(allColors), containsAll(favoriteColors))
        );
    }

    @Test
    public void checkThatAllElementsMatchAPattern() {

        List<String> allColors = Arrays.asList("color:red", "color:blue", "color:green", "color:yellow");

        collette.should(
                seeThat(TheValue.of(allColors), allMatch(color -> color.startsWith("color:")))
        );
    }

    @Test
    public void checkThatAtLeastOneElementMatchesAPattern() {

        List<String> allColors = Arrays.asList("color:red", "color:blue", "color:green", "color:yellow");

        collette.should(
                seeThat(TheValue.of(allColors), anyMatch(color -> color.endsWith(":red")))
        );
    }

    @Test
    public void checkThatAllElementsSatisfySomeCondition() {

        List<String> allColors = Arrays.asList("color:red", "color:blue", "color:green", "color:yellow");

        collette.should(
                seeThat(TheValue.of(allColors), allSatisfy(color -> Assertions.assertThat(color).startsWith("color:")))
        );
    }


}

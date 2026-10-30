package shop.ordering;

import net.serenitybdd.annotations.Narrative;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("Building a basket")
@Narrative(text = {"As a reader", "I want to collect books in a basket", "So that I can buy them together"})
class WhenAReaderBuildsABasketTest {

    @BeforeEach
    void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @Test
    @DisplayName("A reader can add a book to an empty basket")
    void aReaderCanAddABookToAnEmptyBasket() {
        Actor ada = OnStage.theActorCalled("Ada");
        ada.remember("basket", new Basket());

        ada.attemptsTo(AddToBasket.theBook("The Pragmatic Programmer"));

        assertThat(ada.<Basket>recall("basket").size()).isEqualTo(1);
    }

    @Test
    void shouldKeepEveryBookAddedToTheBasket() {
        Actor ada = OnStage.theActorCalled("Ada");
        ada.remember("basket", new Basket());

        ada.attemptsTo(AddToBasket.theBook("Refactoring"), AddToBasket.theBook("Fluent Python"));

        assertThat(ada.<Basket>recall("basket").size()).isEqualTo(2);
    }
}

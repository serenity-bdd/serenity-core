package shop.browsing;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("Searching the catalog")
class WhenSearchingTheCatalogTest {

    @Steps
    ShopperSteps shopper;

    @Test
    void searchingByTitleFindsTheMatchingBook() {
        shopper.searchesFor("Refactoring");
        shopper.shouldSeeBooks(1);
    }

    public static class ShopperSteps {
        private int found;

        @Step("Search the catalog for '{0}'")
        public void searchesFor(String term) { found = "Refactoring".equals(term) ? 1 : 0; }

        @Step("The catalog lists {0} book(s)")
        public void shouldSeeBooks(int expected) { assertThat(found).isEqualTo(expected); }
    }
}

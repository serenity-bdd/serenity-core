package demo;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.core.Serenity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class BasketSteps {

    // A custom parameter type: "3.70 dollars" -> BigDecimal
    @ParameterType("\\d+\\.\\d+")
    public BigDecimal money(String amount) {
        return new BigDecimal(amount);
    }

    @Given("an empty basket")
    public void anEmptyBasket() {
        // Serenity session variables live for one scenario and can be read from any step class
        Serenity.setSessionVariable("basketTotal").to(BigDecimal.ZERO);
    }

    @When("I add these items:")
    public void iAddTheseItems(DataTable items) {
        List<Map<String, String>> rows = items.asMaps();
        for (Map<String, String> row : rows) {
            addToTotal(new BigDecimal(row.get("price")));
        }
    }

    @When("I add a {string} costing {money} dollars")
    public void iAddACosting(String name, BigDecimal price) {
        addToTotal(price);
    }

    @Then("the basket total should be {money} dollars")
    public void theTotalShouldBe(BigDecimal expected) {
        BigDecimal total = Serenity.sessionVariableCalled("basketTotal");
        assertThat(total).isEqualByComparingTo(expected);
    }

    private void addToTotal(BigDecimal price) {
        BigDecimal total = Serenity.sessionVariableCalled("basketTotal");
        Serenity.setSessionVariable("basketTotal").to(total.add(price));
    }
}

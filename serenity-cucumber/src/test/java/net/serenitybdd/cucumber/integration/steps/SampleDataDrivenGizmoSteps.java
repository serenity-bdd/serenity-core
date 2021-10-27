package net.serenitybdd.cucumber.integration.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.cucumber.datatable.matchers.DataTableHasTheSameRowsAs.hasTheSameRowsAs;
import static org.hamcrest.MatcherAssert.assertThat;

;

public class SampleDataDrivenGizmoSteps {

/*
    Given I want to purchase the following gizmos:
    | item | quantity | price |
    | A1   | 10       | 10    |
    | B2   | 5        | 40    |
    | C3   | 60       | 5     |
    When I buy the gizmos
    Then I should be billed the following for each item:
    | item | total |
    | A1   | 100   |
    | B2   | 200   |
    | C3   | 300   |
 */

    @Given("I want to purchase the following gizmos:")
    public void iWantSomeGizmos(DataTable gizmos) {
        given(gizmos);
    }

    @When("I buy the gizmos")
    public void buyTheGizmos() {
        costs = forEachExampleIn(examples).perform(buyAGizmo);
    }

    @Then("I should be billed the following for each item:")
    public void shouldBeBilled(DataTable expectedCost) {
        forEachExampleIn(examples).verifyThat(costs).match(expectedCost);
    }

    private List<Map<String, String>> examples;
    private List<List<String>> costs;
    private void given(DataTable examples) {
        this.examples = mapped(examples);
        throw new AssertionError("crap");
    }

    BuyAGizmo buyAGizmo = new BuyAGizmo();

    class BuyAGizmo implements ExampleTask {

        @Override
        public List<String> performWithValuesFrom(Map<String, String> exampleData) {
            List<String> list = new ArrayList<>();
            list.add(exampleData.get("item"));
            list.add(exampleData.get("total"));
            //list.add(exampleData.get("100"));
            return list;
        }
    }

    private ExampleProcessor forEachExampleIn(List<Map<String, String>> examples) {
        return new ExampleProcessor(examples);
    }

    class ExampleProcessor {
        private final List<Map<String, String>> examples;

        ExampleProcessor(List<Map<String, String>> examples) {
            this.examples = examples;
        }

        public List<List<String>> perform(ExampleTask example) {
            List<List<String>> outcomes = new ArrayList<>();
            for(Map<String, String> exampleData : examples) {
                outcomes.add(example.performWithValuesFrom(exampleData));
            }
            return outcomes;
        }


        public ExampleVerifier verifyThat(List<List<String>> outcomes) {
            return new ExampleVerifier(outcomes);
        }
    }

    class ExampleVerifier {
        private final List<List<String>> actualOutcomes;

        ExampleVerifier(List<List<String>> actualOutcomes) {
            this.actualOutcomes = actualOutcomes;
        }

        public void match(List<List<String>> expectedOutcomes) {
            DataTable actualOutcomesTable = DataTable.create(actualOutcomes);
            DataTable expectedOutcomesTable = DataTable.create(expectedOutcomes);
            //actualOutcomesTable.diff(expectedOutcomesTable);
            assertThat(actualOutcomesTable, hasTheSameRowsAs(expectedOutcomesTable));
        }

        public void match(DataTable expectedOutcomes) {
            //DataTable.create(actualOutcomes).diff(expectedOutcomes);
            assertThat(DataTable.create(actualOutcomes), hasTheSameRowsAs(expectedOutcomes));
        }
    }

    interface ExampleTask {
        List<String> performWithValuesFrom(Map<String, String> exampleData);
    }

    private List<Map<String, String>> mapped(DataTable expectedCost) {
        return expectedCost.asMaps(String.class, String.class);
    }
}
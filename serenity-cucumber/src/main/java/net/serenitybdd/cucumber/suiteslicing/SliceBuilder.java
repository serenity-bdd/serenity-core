package net.serenitybdd.cucumber.suiteslicing;

public class SliceBuilder {

    private int sliceNumber;
    private WeightedCucumberScenarios weightedCucumberScenarios;

    public SliceBuilder(int sliceNumber, WeightedCucumberScenarios weightedCucumberScenarios) {
        this.sliceNumber = sliceNumber;
        this.weightedCucumberScenarios = weightedCucumberScenarios;
    }

    public WeightedCucumberScenarios of(int sliceCount) {
        return weightedCucumberScenarios.sliceInto(sliceCount).get(sliceNumber - 1);
    }
}

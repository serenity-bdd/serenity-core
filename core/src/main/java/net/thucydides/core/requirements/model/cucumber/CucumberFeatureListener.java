package net.thucydides.core.requirements.model.cucumber;

import gherkin.formatter.Formatter;
import gherkin.formatter.model.*;

import java.util.List;

class CucumberFeatureListener implements Formatter {

        Feature feature;
        Background background;

        @Override
        public void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) {

        }

        @Override
        public void uri(String uri) {

        }

        @Override
        public void feature(Feature feature) {
            this.feature = feature;
        }

        @Override
        public void scenarioOutline(ScenarioOutline scenarioOutline) {

        }

        @Override
        public void examples(Examples examples) {

        }

        @Override
        public void startOfScenarioLifeCycle(Scenario scenario) {

        }

        @Override
        public void background(Background background) {
            this.background = background;
        }

        @Override
        public void scenario(Scenario scenario) {

        }

        @Override
        public void step(Step step) {

        }

        @Override
        public void endOfScenarioLifeCycle(Scenario scenario) {

        }

        @Override
        public void done() {

        }

        @Override
        public void close() {

        }

        @Override
        public void eof() {

        }

        public Feature getFeature() {
            return feature;
        }

        public Background getBackground() {
            return background;
        }
    }
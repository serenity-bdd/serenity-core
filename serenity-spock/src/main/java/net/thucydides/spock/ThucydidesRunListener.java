package net.thucydides.spock;

import net.thucydides.core.bootstrap.ThucydidesAgent;
import org.spockframework.runtime.IRunListener;
import org.spockframework.runtime.model.ErrorInfo;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.IterationInfo;
import org.spockframework.runtime.model.SpecInfo;

public class ThucydidesRunListener implements IRunListener {

    private final ThucydidesAgent agent;

    public ThucydidesRunListener(ThucydidesAgent agent) {
        this.agent = agent;
    }

    public void beforeSpec(SpecInfo spec) {
        agent.testSuiteStarted(spec.getName());
    }

    public void beforeFeature(FeatureInfo feature) {
        agent.testStarted(feature.getFeatureMethod().getName());

    }

    public void beforeIteration(IterationInfo iteration) {
    }

    public void afterIteration(IterationInfo iteration) {
    }

    public void afterFeature(FeatureInfo feature) {
        agent.testFinished();
    }

    public void afterSpec(SpecInfo spec) {
        agent.testSuiteFinished();
    }

    public void error(ErrorInfo error) {
    }

    public void specSkipped(SpecInfo spec) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void featureSkipped(FeatureInfo feature) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

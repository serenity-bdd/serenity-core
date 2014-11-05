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
        System.out.println("listener: beforeSpec" + spec.getName());
    }

    public void beforeFeature(FeatureInfo feature) {
        System.out.println("listener: beforeFeature " + feature.getName());
        agent.testStarted(feature.getFeatureMethod().getName());

    }

    public void beforeIteration(IterationInfo iteration) {
        System.out.println("listener: beforeIteration");
    }

    public void afterIteration(IterationInfo iteration) {
        System.out.println("listener: afterIteration");
    }

    public void afterFeature(FeatureInfo feature) {
        System.out.println("listener: afterFeature");
        agent.testFinished();
    }

    public void afterSpec(SpecInfo spec) {
        System.out.println("listener: afterSpec");
        agent.testSuiteFinished();
    }

    public void error(ErrorInfo error) {
        System.out.println("listener: error");
    }

    public void specSkipped(SpecInfo spec) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void featureSkipped(FeatureInfo feature) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

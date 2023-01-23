package net.thucydides.spock;

import net.thucydides.core.bootstrap.ThucydidesAgent;
import net.thucydides.core.steps.Listeners;
import org.apache.commons.lang3.StringUtils;
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.FieldInfo;
import org.spockframework.runtime.model.MethodInfo;
import org.spockframework.runtime.model.SpecInfo;

public class ThucydidesEnabledExtension extends AbstractAnnotationDrivenExtension<ThucydidesEnabled> {

    private ThucydidesAgent agent;

    public ThucydidesEnabledExtension() {
    }

    public void visitSpecAnnotation(ThucydidesEnabled annotation, SpecInfo spec) {
        agent = new ThucydidesAgent(optionalDriverFrom(annotation),
                                    Listeners.getLoggingListener(),
                                    Listeners.getStatisticsListener());
        spec.addListener(new ThucydidesRunListener(agent));
        spec.getInitializerMethod().addInterceptor(new ThucydidesInterceptor(agent));
    }

    private Optional<String> optionalDriverFrom(ThucydidesEnabled annotation) {
        if (StringUtils.isEmpty(annotation.driver())) {
            return Optional.absent();
        } else {
            return Optional.of(annotation.driver());
        }
    }

    @Override
    public void visitFeatureAnnotation(ThucydidesEnabled annotation, FeatureInfo feature) {
    }

    @Override
    public void visitFixtureAnnotation(ThucydidesEnabled annotation, MethodInfo fixtureMethod) {
    }

    @Override
    public void visitFieldAnnotation(ThucydidesEnabled annotation, FieldInfo field) {
    }

    @Override
    public void visitSpec(SpecInfo spec) {
    }
}

package net.thucydides.spock;

import com.google.common.base.Optional;
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
        System.out.println("visitFeatureAnnotation");
    }

    @Override
    public void visitFixtureAnnotation(ThucydidesEnabled annotation, MethodInfo fixtureMethod) {
        System.out.println("visitFixtureAnnotation");
    }

    @Override
    public void visitFieldAnnotation(ThucydidesEnabled annotation, FieldInfo field) {
        System.out.println("visitFieldAnnotation");
    }

    @Override
    public void visitSpec(SpecInfo spec) {
        System.out.println("visitSpec");
    }
}

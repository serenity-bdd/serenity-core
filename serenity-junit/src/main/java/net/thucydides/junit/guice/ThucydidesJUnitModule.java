package net.thucydides.junit.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import serenitymodel.net.thucydides.core.guice.Injectors;
import serenitymodel.net.thucydides.core.statistics.TestCount;
import serenitymodel.net.thucydides.core.steps.StepListener;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.junit.listeners.TestCountListener;
import net.thucydides.junit.listeners.TestCounter;

public class ThucydidesJUnitModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(StepListener.class).annotatedWith(TestCounter.class)
                                .toProvider(TestCountListenerProvider.class).in(Singleton.class);
    }

    public static class TestCountListenerProvider implements Provider<StepListener> {

        public StepListener get() {
            EnvironmentVariables environmentVariables = Injectors.getInjector().getProvider(EnvironmentVariables.class).get() ;
            TestCount testCount = Injectors.getInjector().getInstance(TestCount.class);
            return new TestCountListener(environmentVariables, testCount);
        }
    }
}

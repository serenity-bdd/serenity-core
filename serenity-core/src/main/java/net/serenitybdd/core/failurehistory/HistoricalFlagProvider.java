package net.serenitybdd.core.failurehistory;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.flags.Flag;
import net.thucydides.core.model.flags.FlagProvider;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.Set;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_HISTORY_DIRECTORY;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_HISTORY_FLAGS;

public class HistoricalFlagProvider implements FlagProvider {

    private final EnvironmentVariables environmentVariables;

    @Inject
    public HistoricalFlagProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public Set<? extends Flag> getFlagsFor(TestOutcome testOutcome) {
        if (!historicalFlagsAreActivated()) { return ImmutableSet.of(); }

        return ImmutableSet.of(new NewFailureFlag());
    }

    private boolean historicalFlagsAreActivated() {
        return SHOW_HISTORY_FLAGS.booleanFrom(environmentVariables, false)
                && SERENITY_HISTORY_DIRECTORY.isDefinedIn(environmentVariables);
    }
}

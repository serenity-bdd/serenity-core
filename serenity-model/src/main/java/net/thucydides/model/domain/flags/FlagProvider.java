package net.thucydides.model.domain.flags;

import net.thucydides.model.domain.TestOutcome;

import java.util.Set;

public interface FlagProvider {
    Set<? extends Flag> getFlagsFor(TestOutcome testOutcome);
}

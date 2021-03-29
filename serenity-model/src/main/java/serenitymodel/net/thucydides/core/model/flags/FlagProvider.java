package serenitymodel.net.thucydides.core.model.flags;

import serenitymodel.net.thucydides.core.model.TestOutcome;

import java.util.Set;

public interface FlagProvider {
    Set<? extends Flag> getFlagsFor(TestOutcome testOutcome);
}

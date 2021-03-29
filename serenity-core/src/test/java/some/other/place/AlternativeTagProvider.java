package some.other.place;

import serenitymodel.net.thucydides.core.model.TestOutcome;
import serenitymodel.net.thucydides.core.model.TestTag;
import serenitymodel.net.thucydides.core.statistics.service.TagProvider;

import java.util.Set;

public class AlternativeTagProvider implements TagProvider {
    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        return null;
    }
}

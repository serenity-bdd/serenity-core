package some.other.place;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.statistics.service.TagProvider;

import java.util.Set;

public class AlternativeTagProvider implements TagProvider {
    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        return null;
    }
}

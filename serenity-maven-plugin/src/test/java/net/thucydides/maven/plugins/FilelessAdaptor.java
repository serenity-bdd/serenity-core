package net.thucydides.maven.plugins;

import com.google.common.collect.Lists;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.adaptors.TestOutcomeAdaptor;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FilelessAdaptor implements TestOutcomeAdaptor {

    TestOutcome outcome1 = TestOutcome.forTestInStory("some test", Story.called("some story"));
    TestOutcome outcome2 = TestOutcome.forTestInStory("some other test", Story.called("some story"));

    @Override
    public List<TestOutcome> loadOutcomes() throws IOException {
        return Lists.newArrayList(outcome1, outcome2);
    }

    @Override
    public List<TestOutcome> loadOutcomesFrom(File source) throws IOException {
        return Lists.newArrayList(outcome1, outcome2);
    }
}

package net.serenitybdd.cucumber.suiteslicing;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.empty;
import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_BATCH_NUMBER;
import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_FORK_NUMBER;

public class SerenityTags {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerenityTags.class);

    private final String TEST_BATCH = "test batch";
    private final String TEST_FORK = "test fork";

    private final EnvironmentVariables environmentVariables;

    private SerenityTags() {
        environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    public static SerenityTags create() {
        return new SerenityTags();
    }

    public Optional<TestTag> batches() {
        Integer batchNumber = environmentVariables.getPropertyAsInteger(SERENITY_BATCH_NUMBER, 0);
        return numberedTagFor(batchNumber, "batch", TEST_BATCH);
    }

    public Optional<TestTag> forks() {
        Integer forkNumber = environmentVariables.getPropertyAsInteger(SERENITY_FORK_NUMBER, 0);
        return numberedTagFor(forkNumber, "fork", TEST_FORK);
    }

    private static Optional<TestTag> numberedTagFor(Integer value, String shardType, String tagType) {
        return (shardType.isEmpty() || (value == 0)) ? empty() : Optional.of(TestTag.withName(String.format("%s %s", shardType, value)).andType(tagType));
    }

    public void tagScenarioWithBatchingInfo() {
        batches().ifPresent(addTag());
        forks().ifPresent(addTag());
    }

    private Consumer<TestTag> addTag() {
        return this::addTagWith;
    }

    public void addTagWith(TestTag tag) {
        LOGGER.debug("adding tag to {} scenario", tag);
        StepEventBus.getEventBus().addTagsToCurrentStory(newArrayList(tag));
    }

    public void addTagWith(String tagName, String tagType) {
        addTagWith(TestTag.withName(tagName).andType(tagType));
    }

}
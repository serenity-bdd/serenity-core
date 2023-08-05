package net.thucydides.model.requirements;

import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WhenFindingARelativePathForAFeatureFile {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @Test
    void shouldLookForTheLastFeatureFolder() {
        String path = "/Users/john/Projects/Serenity/serenity-core/serenity-model/target/test-classes/serenity-cucumber/features/rules/hints.feature";
        FeatureFilePath featureFilePath = new FeatureFilePath(environmentVariables);
        String relativePath = featureFilePath.relativePathFor(path);

        assertThat(relativePath).isEqualTo("rules/hints.feature");
    }

    @Test
    void shouldLetYouRedefineTheFeatureFileName() {
        environmentVariables.setProperty("serenity.features.directory", "my-features");
        String path = "/Users/john/Projects/my-project/my-features/rules/hints.feature";
        FeatureFilePath featureFilePath = new FeatureFilePath(environmentVariables);
        String relativePath = featureFilePath.relativePathFor(path);

        assertThat(relativePath).isEqualTo("rules/hints.feature");
    }

    @Test
    void shouldLookForASubdirectoryOfSrcTestResourcesIfPresent() {
        String path = "/a/b/c/src/test/resources/samples/rules/hints.feature";
        FeatureFilePath featureFilePath = new FeatureFilePath(environmentVariables);
        String relativePath = featureFilePath.relativePathFor(path);

        assertThat(relativePath).isEqualTo("rules/hints.feature");
    }

    @Test
    void shouldReturnTheFullPathIfNoOtherOptionAvailable() {
        String path = "/a/b/c/rules/hints.feature";
        FeatureFilePath featureFilePath = new FeatureFilePath(environmentVariables);
        String relativePath = featureFilePath.relativePathFor(path);

        assertThat(relativePath).isEqualTo("/a/b/c/rules/hints.feature");
    }

}

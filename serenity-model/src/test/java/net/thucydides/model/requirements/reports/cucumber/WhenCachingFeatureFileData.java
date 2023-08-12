package net.thucydides.model.requirements.reports.cucumber;

import net.thucydides.model.requirements.model.cucumber.AnnotatedFeature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class WhenCachingFeatureFileData {

    private File featureFile;
    private FeatureCache featureCache;

    @BeforeEach
    void setup() {
        featureFile = Paths.get("src/test/resources/features/record_todos/add_new_items_to_the_todo_list.feature").toFile();
        featureCache = new FeatureCache();
    }

    @AfterEach
    void tearDown() {
        featureCache.close();
    }

    @Test
    void shouldLoadNewFeatureFiles() {
        Optional<AnnotatedFeature> result = featureCache.loadFeature(featureFile);

        assertThat(result.isPresent()).isTrue();
    }

    @Nested
    class WhenAFileHasAlreadyBeenLoaded {
        AnnotatedFeature loadedFeature;

        @BeforeEach
        void preloadFeatureFile() {
            loadedFeature = featureCache.loadFeature(featureFile).get();
        }

        @Test
        void shouldReturnKnownFeatureFiles() {
            Optional<AnnotatedFeature> reloadedFeature = featureCache.loadFeature(featureFile);
            assertThat(reloadedFeature).isPresent();
            assertThat(reloadedFeature.get()).isEqualTo(loadedFeature);
        }
    }

}

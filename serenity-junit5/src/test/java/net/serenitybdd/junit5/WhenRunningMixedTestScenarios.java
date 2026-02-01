package net.serenitybdd.junit5;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import io.cucumber.java.en.Given;
import net.serenitybdd.core.steps.UIInteractions;
import net.thucydides.model.ThucydidesSystemProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

class WhenRunningMixedTestScenarios extends AbstractTestStepRunnerTest {

    private static final String UNIQUE_TEST_IDENTIFIER = UUID.randomUUID().toString();

    @ExtendWith(SerenityJUnit5Extension.class)
    static class SerenityTest {

        MySteps mySteps;

        @Test
        void serenityTest() {
            mySteps.myGiven(UNIQUE_TEST_IDENTIFIER);
        }
    }

    static class MySteps extends UIInteractions {

        @Given("mygiven {0}")
        void myGiven(String arg) {
        }
    }

    static class NonSerenityTest {
        @Test
        void nonSerenityTest() {
        }
    }

    @TempDir
    File tempDir;

    String outputInReport = "mygiven %s".formatted(UNIQUE_TEST_IDENTIFIER);

    @BeforeEach
    void setUp() {
        System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(), tempDir.getAbsolutePath());
    }

    @Test
    void shouldGenerateReportForSingleSerenityTest() {
        runTestForClass(SerenityTest.class);

        assertThat(findReportsForTest(WhenRunningMixedTestScenarios.class))
                .isNotEmpty()
                .anySatisfy(report -> assertThat(report).contains(outputInReport));
    }

    @Test
    void shouldGenerateReportForMixedSerenityLast() {
        runTestForClass(NonSerenityTest.class, SerenityTest.class);

        assertThat(findReportsForTest(WhenRunningMixedTestScenarios.class))
                .isNotEmpty()
                .anySatisfy(report -> assertThat(report).contains(outputInReport));
    }

    @Test
    void shouldGenerateReportForMixedNonSerenityLast() {
        runTestForClass(SerenityTest.class, NonSerenityTest.class);

        assertThat(findReportsForTest(WhenRunningMixedTestScenarios.class))
                .isNotEmpty()
                .anySatisfy(report -> assertThat(report).contains(outputInReport));
    }

    private List<String> findReportsForTest(Class<?> clazz) {
        try (Stream<Path> stream = Files.list(tempDir.toPath())) {
            List<Path> files = stream
                    .filter(p -> p.toString().endsWith(".json"))
                    .filter(p -> fileContains(p, clazz.getName()))
                    .toList();

            assertThat(files)
                    .describedAs("No json reports found for test %s", clazz.getName())
                    .isNotEmpty();

            return files.stream()
                    .map(this::readFile)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean fileContains(Path path, String text) {
        return readFile(path).contains(text);
    }

    private String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

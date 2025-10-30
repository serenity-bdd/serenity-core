package net.serenitybdd.ant;

import org.apache.tools.ant.BuildFileTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Thread.currentThread;
import static org.assertj.core.api.Assertions.assertThat;

;

public abstract class SerenityAntTaskTestBase extends BuildFileTest {
    protected void serenityReportsShouldAppearIn(String reportDirectory) throws URISyntaxException {
        assertThat(getLog()).contains("Generating Serenity reports");
        String indexFilePath = reportDirectory + "/" + "index.html";
        String screenshotFilePath = reportDirectory + "/" + "amazon.png";
        assertThat(currentThread().getContextClassLoader().getResource(indexFilePath)).isNotNull();
        assertThat(Paths.get("target/test-classes/" + indexFilePath)).exists();
        assertThat(currentThread().getContextClassLoader().getResource(screenshotFilePath)).isNotNull();
    }

    protected void thucydidesResourcesShouldAppearIn(String reportDirectory) throws URISyntaxException {
        assertThat(getLog()).contains("Generating Serenity reports");
        String cssFilePath = reportDirectory + "/css/core.css";
        assertThat(currentThread().getContextClassLoader().getResource(cssFilePath)).isNotNull();
    }


    protected void cleanReportsIn(String reportDirectory) throws URISyntaxException, IOException {
        String indexFilePath = reportDirectory + "/" + "index.html";
        URL indexUrl = currentThread().getContextClassLoader().getResource(indexFilePath);
        if (indexUrl != null) {
            Path indexFile = Paths.get(indexUrl.toURI());
            Files.delete(indexFile);
        }
    }

}

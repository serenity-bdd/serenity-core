package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.Upload;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class UploadTest extends ScreenplayInteractionTestBase {

    private final static Target INPUT_FIELD = Target.the("File upload field").locatedBy("#upload-file");

    Path fileToUpload;

    @Before
    public void createTempFile() throws IOException {
        fileToUpload = Files.createTempFile("sample", "upload");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToUpload.toFile()))) {
            writer.write("Sample data");
        }
    }

    @Test
    public void uploadAFileFromAPath() {
        dina.attemptsTo(Upload.theFile(fileToUpload).to(INPUT_FIELD));
        assertThat(dina.asksFor(Value.of(INPUT_FIELD))).endsWith(fileToUpload.getFileName().toString());
    }

    @Test
    public void uploadAFileFromAPathUsingTheLocalFileDetector() {
        dina.attemptsTo(Upload.theFile(fileToUpload).to(INPUT_FIELD).usingLocalFileDetector());
        assertThat(dina.asksFor(Value.of(INPUT_FIELD))).endsWith(fileToUpload.getFileName().toString());
    }

    @Test
    public void uploadAFileFromTheClasspath() throws URISyntaxException {
        dina.attemptsTo(Upload.theClasspathResource("sample-data/upload.txt").to(INPUT_FIELD));
        assertThat(dina.asksFor(Value.of(INPUT_FIELD))).endsWith("upload.txt");
    }


}

package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebElement;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Upload a file from the local filesystem to a webpage using the standard HTML upload form.
 */
public class Upload {

    public static UploadBuilder theFile(Path fileToUpload) { return new UploadBuilder(fileToUpload); }

    public static UploadBuilder theClasspathResource(String resourcePath) throws URISyntaxException {
        URL systemResource =  Optional.ofNullable(ClassLoader.getSystemResource(resourcePath))
                                      .orElseThrow(() -> new InvalidArgumentException("File not found on classpath: " + resourcePath));
        Path fileToUpload = Paths.get(systemResource.toURI());
        return new UploadBuilder(fileToUpload);
    }

    public static class UploadBuilder {
        private final Path fileToUpload;

        public UploadBuilder(Path fileToUpload) {
            this.fileToUpload = fileToUpload;
        }

        public UploadToField to(Target uploadField) {
            return instrumented(UploadToTarget.class, fileToUpload, uploadField);
        }

        public UploadToField to(WebElement uploadField) {
            return instrumented(UploadToWebElement.class, fileToUpload, uploadField);
        }

    }
}

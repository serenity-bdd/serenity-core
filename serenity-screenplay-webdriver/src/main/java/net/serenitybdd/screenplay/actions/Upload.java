package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.WebElement;

import java.nio.file.Path;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Upload a file from the local filesystem to a webpage using the standard HTML upload form.
 */
public class Upload {

    public static UploadBuilder theFile(Path fileToUpload) { return new UploadBuilder(fileToUpload); }

    public static class UploadBuilder {
        private final Path fileToUpload;

        public UploadBuilder(Path fileToUpload) {
            this.fileToUpload = fileToUpload;
        }

        public Performable to(Target uploadField) {
            return instrumented(UploadToTarget.class, fileToUpload, uploadField);
        }

        public Performable to(WebElement uploadField) {
            return instrumented(UploadToWebElement.class, fileToUpload, uploadField);
        }

    }
}

package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.WebElement;

import java.nio.file.Path;

public class UploadToWebElement extends UploadToField {

    private final WebElement uploadField;

    public UploadToWebElement(Path inputFile, WebElement uploadField) {
        super(inputFile);
        this.uploadField = uploadField;
    }

    @Override
    @Step("Upload file at #inputFile to #uploadField")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWeb.as(actor).upload(inputFile.toFile().getPath())
                              .fromLocalMachine()
                              .to(uploadField);
    }
}

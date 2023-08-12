package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.annotations.Step;

import java.nio.file.Path;

public class UploadToTarget extends UploadToField {

    private final Target uploadField;

    public UploadToTarget(Path inputFile, Target uploadField) {
        super(inputFile);
        this.uploadField = uploadField;
    }

    @Override
    @Step("Upload file at #inputFile to #uploadField")
    public <T extends Actor> void performAs(T actor) {
        uploadFile(actor).to(uploadField.resolveFor(actor));
    }

}

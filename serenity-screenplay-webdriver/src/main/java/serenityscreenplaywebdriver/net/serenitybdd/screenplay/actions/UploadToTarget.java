package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenitymodel.net.thucydides.core.annotations.Step;

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
        BrowseTheWeb.as(actor).upload(inputFile.toFile().getPath())
                              .fromLocalMachine()
                              .to(uploadField.resolveFor(actor));
    }
}

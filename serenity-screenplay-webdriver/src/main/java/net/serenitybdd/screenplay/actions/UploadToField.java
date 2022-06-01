package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.pages.components.FileToUpload;

import java.nio.file.Path;

public abstract class UploadToField implements Interaction {

    protected final Path inputFile;
    protected boolean useLocalFileDetector = false;

    public UploadToField usingLocalFileDetector() {
        this.useLocalFileDetector = true;
        return this;
    }

    public UploadToField(Path inputFile) {
        this.inputFile = inputFile;
    }


    protected  <T extends Actor> FileToUpload uploadFile(T actor) {
        FileToUpload uploadFile = BrowseTheWeb.as(actor).upload(inputFile.toFile().getPath());
        if (useLocalFileDetector) {
            uploadFile.fromLocalMachine();
        }
        return uploadFile;
    }
}

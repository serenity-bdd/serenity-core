package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Interaction;

import java.nio.file.Path;

public abstract class UploadToField implements Interaction {

    protected final Path inputFile;

    public UploadToField(Path inputFile) {
        this.inputFile = inputFile;
    }

}

package net.serenitybdd.core.photography;

import com.google.common.base.Preconditions;
import net.serenitybdd.annotations.BlurLevel;
import java.nio.file.Path;

public class PhotoSessionBooking {

    private final Darkroom darkroom;
    private PhotoLens lens;
    private Path outputDirectory;
    private BlurLevel blurLevel;

    public PhotoSessionBooking(Darkroom darkroom) {
        this.darkroom = darkroom;
    }

    public PhotoSessionBooking with(PhotoLens lens) {
        this.lens = lens;
        return this;
    }

    public PhotoSessionBooking andWithBlurring(BlurLevel blurLevel) {
        this.blurLevel = blurLevel;
        return this;
    }

    public PhotoSessionBooking toDirectory(Path outputDirectory) {
        this.outputDirectory = outputDirectory;
        return this;
    }

    public ScreenshotPhoto takeScreenshot() {
        Preconditions.checkNotNull(lens);
        Preconditions.checkNotNull(outputDirectory);
        return inPhotoSession().takeScreenshot();
    }

    private PhotoSession inPhotoSession() {
        return new PhotoSession(lens, darkroom, outputDirectory, blurLevel);
    }

}

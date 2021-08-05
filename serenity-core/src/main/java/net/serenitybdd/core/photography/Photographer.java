package net.serenitybdd.core.photography;

public class Photographer {
    private final Darkroom darkroom;

    public Photographer(Darkroom darkroom) {
        this.darkroom = darkroom;
    }

    public PhotoSessionBooking takesAScreenshot() {
        return new PhotoSessionBooking(darkroom);
    }
}

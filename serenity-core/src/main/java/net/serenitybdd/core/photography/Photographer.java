package net.serenitybdd.core.photography;

import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;

public class Photographer {
    private final Darkroom darkroom;
    private ScrollStrategy scrollStrategy;

    public Photographer(Darkroom darkroom, ScrollStrategy scrollStrategy) {
        this.darkroom = darkroom;
        this.scrollStrategy = scrollStrategy;
    }

    public PhotoSessionBooking takesAScreenshot() {
        return new PhotoSessionBooking(darkroom, scrollStrategy);
    }
}

package net.serenitybdd.screenplay;

import net.serenitybdd.markers.CanBeSilent;
import net.serenitybdd.markers.DisableScreenshots;

import java.util.List;

public class AnonymousBackendTask extends AnonymousTask implements DisableScreenshots {

    public AnonymousBackendTask(String title, List<Performable> steps) {
        super(title, steps);
    }

}

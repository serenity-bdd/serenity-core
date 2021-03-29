package serenityscreenplay.net.serenitybdd.screenplay;

import serenitycore.net.serenitybdd.markers.DisableScreenshots;

import java.util.List;

public class AnonymousBackendTask extends AnonymousTask implements DisableScreenshots {

    public AnonymousBackendTask(String title, List<Performable> steps) {
        super(title, steps);
    }

}

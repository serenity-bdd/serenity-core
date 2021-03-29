package serenityscreenplay.net.serenitybdd.screenplay;

import com.google.common.eventbus.Subscribe;

public class ConsequenceListener {
    private final EventBusInterface eventBusInterface;

    public ConsequenceListener(EventBusInterface eventBusInterface) {
        this.eventBusInterface = eventBusInterface;
    }

    @Subscribe
    public void beginConsequenceCheck() {
        if (!eventBusInterface.aStepHasFailed()) {
            eventBusInterface.enableSoftAsserts();
        }
    }

    @Subscribe
    public void endConsequenceCheck() {
        eventBusInterface.disableSoftAsserts();
    }

}

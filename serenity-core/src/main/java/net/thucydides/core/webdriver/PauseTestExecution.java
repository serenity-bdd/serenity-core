package net.thucydides.core.webdriver;

import net.serenitybdd.core.exceptions.SerenityManagedException;

class PauseTestExecution {
    private final int delay;

    PauseTestExecution(int delay) {
        this.delay = delay;
    }

    static PauseTestExecution forADelayOf(int delay) {
        return new PauseTestExecution(delay);
    }

    void seconds() {
        delayFor(delay * 1000);
    }

    private void delayFor(int delayInMilliseconds) {
        try {
            Thread.sleep(delayInMilliseconds);
        } catch (InterruptedException e) {
            throw new SerenityManagedException(e);
        }
    }
}

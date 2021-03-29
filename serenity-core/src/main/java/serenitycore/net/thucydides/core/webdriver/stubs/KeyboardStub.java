package serenitycore.net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.interactions.Keyboard;

/**
 * Created by john on 14/06/2016.
 */
public class KeyboardStub implements Keyboard {
    @Override
    public void sendKeys(CharSequence... keysToSend) {
    }

    @Override
    public void pressKey(CharSequence keyToPress) {
    }

    @Override
    public void releaseKey(CharSequence keyToRelease) {
    }
}

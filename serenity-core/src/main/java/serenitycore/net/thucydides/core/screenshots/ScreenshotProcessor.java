package serenitycore.net.thucydides.core.screenshots;

@Deprecated
public interface ScreenshotProcessor {
    void waitUntilDone();

    void terminate();

    void queueScreenshot(QueuedScreenshot queuedScreenshot);

    boolean isEmpty();
}

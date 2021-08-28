package net.serenitybdd.core.photography;

import java.io.IOException;

/**
 * Implement this interface if you want to implement your own WebDriver screenshot logic.
 * Any class that implements this method must have a constructor with a PhotoLens parameter, e.g.
 * <code>
 *     <pre>
 *         class WebDriverScreenShooter {
 *             WebDriver driver;
 *             public WebDriverScreenShooter(PhotoLens lens) {
 *                 this.driver = ((WebDriverPhotoLens) lens).getDriver();
 *             }
 *         }
 *     </pre>
 * </code>
 *
 * You can use your custom ScreenShooter class by setting the `serenity.screenshooter` property to the fully qualified
 * classname of your class.
 */
public interface ScreenShooter {
    byte[] takeScreenshot() throws IOException;
}

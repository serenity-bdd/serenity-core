package net.serenitybdd.core.photography

import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy
import net.thucydides.core.screenshots.BlurLevel
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.openqa.selenium.WebDriver
import spock.lang.Specification

class WhenTakingScreenshotsFromBrowserWithoutTakingScreenshotAbility extends Specification {

    @Rule
    TemporaryFolder folder = new TemporaryFolder();

    Darkroom darkroom

    def setup() {
        darkroom = new Darkroom()
    }

    def cleanup() {
        darkroom.terminate()
    }

    def "when a photo session with browser without TakesScreenshot ability is used it should not take a photo"() {
        given:
            def driver = Mock(WebDriver)
            driver.getTitle() >> "value";
            def session = new PhotoSession(driver, darkroom, folder.newFolder().toPath(), BlurLevel.NONE, ScrollStrategy.VIEWPORT_ONLY)
        when:
            def photo = session.takeScreenshot()
        then:
            photo == ScreenshotPhoto.None
    }
}

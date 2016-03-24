package net.serenitybdd.core.photography

import net.thucydides.core.screenshots.BlurLevel
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.WebDriver
import spock.lang.Specification

class WhenTakingScreenshotsFromBrowserWithoutTakingScreenshotAbility extends Specification {

    @Rule
    TemporaryFolder folder = new TemporaryFolder();

    def "when a photo session with browser without TakesScreenshot ability is used it should not take a photo"() {
        given:
            def driver = Mock(WebDriver)
            driver.getTitle() >> "value";
            def session = new PhotoSession(driver, folder.newFolder().toPath(), BlurLevel.NONE)
        when:
            def photo = session.takeScreenshot()
        then:
            photo == ScreenshotPhoto.None
    }
}

package net.serenitybdd.core.photography

import net.thucydides.core.screenshots.BlurLevel
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.WebDriver
import spock.lang.Specification

class WhenTakingScreenshotsFromDiedBrowser extends Specification {

    @Rule
    TemporaryFolder folder = new TemporaryFolder();

    def "when a photo session died browser is used it should not take a photo"() {
        given:
            def driver = Mock(WebDriver)
            driver.getTitle() >> { throw new NoSuchWindowException("Some exception ") };
            def session = new PhotoSession(driver, folder.newFolder().toPath(), BlurLevel.NONE)
        when:
            def photo = session.takeScreenshot()
        then:
            photo == ScreenshotPhoto.None
    }

    def "when a for saving page source died  is used browser it should not try to save page source"() {
        given:
            def driver = Mock(WebDriver)
            driver.getTitle() >> { throw new NoSuchWindowException("Some exception ") };
            def recorder = new PageSourceRecorder(driver)
        when:
            def photo = recorder.intoDirectory(folder.newFolder().toPath())
        then:
            !photo.isPresent()
    }

}

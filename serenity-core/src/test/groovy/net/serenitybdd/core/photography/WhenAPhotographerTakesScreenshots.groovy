package net.serenitybdd.core.photography

import net.serenitybdd.core.photography.bluring.AnnotatedBluring
import net.thucydides.core.annotations.BlurScreenshots
import net.thucydides.core.screenshots.BlurLevel
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static net.thucydides.core.ThucydidesSystemProperty.DEFAULT_HEIGHT
import static net.thucydides.core.ThucydidesSystemProperty.DEFAULT_WIDTH

class WhenAPhotographerTakesScreenshots extends Specification {

    def "when a photographer takes a screenshot the photographer returns the future path of the screenshot"() {
        given:
            def photographer = new Photographer();
        when:
            ScreenshotPhoto photo = photographer.takesAScreenshot()
                                                .withDriver(driver)
                                                .andSaveToDirectory(screenshotDirectory);
        then:
            Darkroom.waitUntilClose();
            photo.getPathToScreenshot().startsWith(screenshotDirectory)
    }

    def "when a photographer takes a screenshot the screenshot should be stored after processing"() {
        given:
            def photographer = new Photographer();
        when:
            ScreenshotPhoto photo = photographer.takesAScreenshot()
                    .withDriver(driver)
                    .andSaveToDirectory(screenshotDirectory);
        then:
            Darkroom.waitUntilClose();
            Files.exists(photo.getPathToScreenshot())
    }

    def "a screenshot that has already been stored should not be stored again"() {
        given:
            def photographer = new Photographer();
            ScreenshotPhoto previousPhoto = photographer.takesAScreenshot()
                                                         .withDriver(driver)
                                                         .andSaveToDirectory(screenshotDirectory);
        when:
            ScreenshotPhoto newPhoto = photographer.takesAScreenshot()
                    .withDriver(driver)
                    .andSaveToDirectory(screenshotDirectory);
        then:
            Darkroom.waitUntilClose();
            previousPhoto == newPhoto
    }


    def "a screenshot that is already the correct dimensions should not be resized"() {
        given:
            def photographer = new Photographer();
            driver.manage().window().setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT))

        when:
            ScreenshotPhoto newPhoto = photographer.takesAScreenshot()
                    .withDriver(driver)
                    .andSaveToDirectory(screenshotDirectory);
        then:
            Darkroom.waitUntilClose();
            newPhoto
    }

    @BlurScreenshots(BlurLevel.HEAVY)
    def "blurred screenshots should be blurred"() {
        given:
            def photographer = new Photographer();
            driver.manage().window().setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT))

        when:
            ScreenshotPhoto newPhoto = photographer.takesAScreenshot()
                    .withDriver(driver)
                    .andWithBlurring(AnnotatedBluring.blurLevel())
                    .andSaveToDirectory(screenshotDirectory);
            Darkroom.waitUntilClose();
        then:
            newPhoto.pathToScreenshot.toString().contains("BLURRED_HEAVY")
    }

    def "unblurred screenshots should not be blurred"() {
        given:
            def photographer = new Photographer();
            driver.manage().window().setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT))

        when:
            ScreenshotPhoto newPhoto = photographer.takesAScreenshot()
                    .withDriver(driver)
                    .andWithBlurring(AnnotatedBluring.blurLevel())
                    .andSaveToDirectory(screenshotDirectory);
            Darkroom.waitUntilClose();
        then:
            !newPhoto.pathToScreenshot.toString().contains("BLURRED")
    }

    WebDriver driver;
    Path screenshotDirectory;

    long startTime;

    def setup() {
        screenshotDirectory = Files.createDirectories(Paths.get("./build/screenshots"));// Files.createTempDirectory("screenshots")
        driver = new PhantomJSDriver()
        driver.get(siteFromUrlAt("/static-site/unchanging-page.html"))
        startTime = System.currentTimeMillis()

        Darkroom.isOpenForBusiness();
    }

    String siteFromUrlAt(String path) {
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir, "src/test/resources"  + path);
        return "file://" + testSite.getAbsolutePath();
    }

    def cleanup() {
        println "Test duration: " + (System.currentTimeMillis() - startTime) + " ms"
        driver.quit()
    }

}

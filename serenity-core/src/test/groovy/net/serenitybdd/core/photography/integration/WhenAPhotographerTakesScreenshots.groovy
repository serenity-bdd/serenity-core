package net.serenitybdd.core.photography.integration

import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy
import net.serenitybdd.core.photography.Darkroom
import net.serenitybdd.core.photography.Photographer
import net.serenitybdd.core.photography.ScreenshotPhoto
import net.serenitybdd.core.photography.bluring.AnnotatedBluring
import net.thucydides.core.annotations.BlurScreenshots
import net.thucydides.core.screenshots.BlurLevel
import org.apache.commons.io.FileUtils
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

import static net.thucydides.core.ThucydidesSystemProperty.DEFAULT_HEIGHT
import static net.thucydides.core.ThucydidesSystemProperty.DEFAULT_WIDTH

class WhenAPhotographerTakesScreenshots extends Specification {

    Darkroom darkroom

    def "when a photographer takes a screenshot the photographer returns the future path of the screenshot"() {
        given:
            def photographer = new Photographer(darkroom,ScrollStrategy.VIEWPORT_ONLY)
        when:
            ScreenshotPhoto photo = photographer.takesAScreenshot()
                                                .with(driver)
                                                .andSaveToDirectory(screenshotDirectory);
        then:
            darkroom.waitUntilClose()
            photo.getPathToScreenshot().startsWith(screenshotDirectory)
    }

    def "a screenshot of the whole page can also be taken."() {
        given:
        def photographer = new Photographer(darkroom,ScrollStrategy.WHOLE_PAGE)
        when:
        ScreenshotPhoto photo = photographer.takesAScreenshot()
                .with(driver)
                .andSaveToDirectory(screenshotDirectory);
        then:
        darkroom.waitUntilClose()
        photo.getPathToScreenshot().startsWith(screenshotDirectory)
    }

    @Ignore("Unstable on SnapCI")
    def "when a photographer takes a screenshot the screenshot should be stored after processing"() {
        given:
        def photographer = new Photographer(darkroom,ScrollStrategy.VIEWPORT_ONLY);
        when:
            ScreenshotPhoto photo = photographer.takesAScreenshot()
                    .with(driver)
                    .andSaveToDirectory(screenshotDirectory);
        then:
            darkroom.waitUntilClose();
            Files.exists(photo.getPathToScreenshot())
    }

    def "a screenshot that has already been stored should not be stored again"() {
        given:
        def photographer = new Photographer(darkroom,ScrollStrategy.VIEWPORT_ONLY);
            ScreenshotPhoto previousPhoto = photographer.takesAScreenshot()
                                                         .with(driver)
                                                         .andSaveToDirectory(screenshotDirectory);
        when:
            ScreenshotPhoto newPhoto = photographer.takesAScreenshot()
                    .with(driver)
                    .andSaveToDirectory(screenshotDirectory);
        then:
            darkroom.waitUntilClose();
            previousPhoto == newPhoto
    }


    def "a screenshot that is already the correct dimensions should not be resized"() {
        given:
        def photographer = new Photographer(darkroom,ScrollStrategy.VIEWPORT_ONLY);
            driver.manage().window().setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT))

        when:
            ScreenshotPhoto newPhoto = photographer.takesAScreenshot()
                    .with(driver)
                    .andSaveToDirectory(screenshotDirectory);
        then:
            darkroom.waitUntilClose();
            newPhoto
    }

    @BlurScreenshots(BlurLevel.HEAVY)
    def "blurred screenshots should be blurred"() {
        given:
            def photographer = new Photographer(darkroom,ScrollStrategy.VIEWPORT_ONLY);
            driver.manage().window().setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT))

        when:
            ScreenshotPhoto newPhoto = photographer.takesAScreenshot()
                    .with(driver)
                    .andWithBlurring(AnnotatedBluring.blurLevel())
                    .andSaveToDirectory(screenshotDirectory);
            darkroom.waitUntilClose();
        then:
            newPhoto.pathToScreenshot.toString().contains("BLURRED_HEAVY")
    }

    def "unblurred screenshots should not be blurred"() {
        given:
        def photographer = new Photographer(darkroom, ScrollStrategy.VIEWPORT_ONLY);
            driver.manage().window().setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT))

        when:
            ScreenshotPhoto newPhoto = photographer.takesAScreenshot()
                    .with(driver)
                    .andWithBlurring(AnnotatedBluring.blurLevel())
                    .andSaveToDirectory(screenshotDirectory);
            darkroom.waitUntilClose();
        then:
            !newPhoto.pathToScreenshot.toString().contains("BLURRED")
    }

    WebDriver driver;
    Path screenshotDirectory;

    long startTime;

    def setup() {
        screenshotDirectory =  Files.createTempDirectory("screenshots");//Files.createDirectories(Paths.get("./build/screenshots"));// Files.createTempDirectory("screenshots")
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        driver = new ChromeDriver(chromeOptions)
        driver.get(siteFromUrlAt("/static-site/unchanging-page.html"))
        startTime = System.currentTimeMillis()

        darkroom = new Darkroom()
        darkroom.isOpenForBusiness();

    }

    String siteFromUrlAt(String path) {
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir, "src/test/resources"  + path);
        return "file://" + testSite.getAbsolutePath();
    }

    def cleanup() {
        println "Test duration: " + (System.currentTimeMillis() - startTime) + " ms"
        darkroom.terminate()
        if (driver){
            driver.quit()
        }
        FileUtils.deleteDirectory(screenshotDirectory.toFile());
    }
}
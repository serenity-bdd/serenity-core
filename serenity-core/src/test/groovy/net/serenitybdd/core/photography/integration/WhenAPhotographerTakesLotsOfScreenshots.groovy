package net.serenitybdd.core.photography.integration
import com.google.common.collect.Lists
import net.serenitybdd.core.photography.Darkroom
import net.serenitybdd.core.photography.Photographer
import net.serenitybdd.core.photography.ScreenshotPhoto
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class WhenAPhotographerTakesLotsOfScreenshots extends Specification {

    def "when a photographer takes a series of screenshots they all should be stored"() {
        given:
            def photographer = new Photographer();
        when:
            def driver = new FirefoxDriver()
            Darkroom.isOpenForBusiness();
            List<ScreenshotPhoto> photos = Lists.newArrayList();
            for (photoCount in 0..10){
                photos.add(photographer.takesAScreenshot()
                        .with(driver)
                        .andSaveToDirectory(screenshotDirectory));
            }
            Darkroom.waitUntilClose();
            driver.quit()
        then:
            photos.each { photo -> Files.exists(photo.pathToScreenshot) }
    }

    def "should handle multiple screenshots in parallel"() {
        given:
            def photographer = new Photographer();
            List<ScreenshotPhoto> photos = Lists.newArrayList();
        and:
            def threads = []
            for (i in 1..10) {
                threads << Thread.start {
                    Darkroom.isOpenForBusiness();
                    def driver = new FirefoxDriver()
                    driver.get(siteFromUrlAt("/static-site/index.html"))

                    for (j in 1..10) {
                        photos.add(photographer.takesAScreenshot()
                                .with(driver)
                                .andSaveToDirectory(screenshotDirectory));
                    }
                    driver.quit()
                    Darkroom.waitUntilClose();
                }
            }
            println threads
        when:
            threads.forEach { it.join() }
        then:
            photos.size() == 100
            photos.each { photo -> Files.exists(photo.pathToScreenshot) }

    }
    WebDriver driver;
    Path screenshotDirectory;

    long startTime;

    def setup() {
        screenshotDirectory = Files.createTempDirectory("screenshots")
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
    }

}

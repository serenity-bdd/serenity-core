package net.serenitybdd.core.photography

import com.google.common.collect.Lists
import org.openqa.selenium.WebDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path


class WhenAPhotographerTakesLotsOfScreenshots extends Specification {

    def "when a photographer takes a series of screenshots they all should be stored"() {
        given:
            def photographer = new Photographer();
        when:
            Darkroom.isOpenForBusiness();
            List<ScreenshotPhoto> photos = Lists.newArrayList();
            for (photoCount in 0..10){
                photos.add(photographer.takesAScreenshot()
                        .withDriver(driver)
                        .andSaveToDirectory(screenshotDirectory));
            }
            Darkroom.waitUntilClose();
        then:
            println "Photos: $photos"
            photos.forEach { photo -> Files.exists(photo.pathToScreenshot) }
    }

    WebDriver driver;
    Path screenshotDirectory;

    long startTime;

    def setup() {
        screenshotDirectory = Files.createTempDirectory("screenshots")
        driver = new PhantomJSDriver()
        driver.get(siteFromUrlAt("/static-site/index.html"))
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

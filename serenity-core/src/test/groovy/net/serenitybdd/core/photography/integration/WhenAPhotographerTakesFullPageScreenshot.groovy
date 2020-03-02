package net.serenitybdd.core.photography.integration

import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy
import com.google.common.collect.Lists
import net.serenitybdd.core.photography.Darkroom
import net.serenitybdd.core.photography.Photographer
import net.serenitybdd.core.photography.ScreenshotPhoto
import org.openqa.selenium.phantomjs.PhantomJSDriver
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class WhenAPhotographerTakesFullPageScreenshot extends Specification {

    Darkroom darkroom

    def "when a photographer takes full page screenshot"() {
        given:
            def photographer = new Photographer(darkroom, ScrollStrategy.WHOLE_PAGE);
        when:
            List<ScreenshotPhoto> photos = Lists.newArrayList();
            def driver
            try {
                driver = new PhantomJSDriver()
                darkroom = new Darkroom()
                darkroom.isOpenForBusiness();
                driver.manage().window().maximize();
                driver.get("http://google.ru");
                photos.add(photographer.takesAScreenshot()
                        .with(driver)
                        .andSaveToDirectory(screenshotDirectory));
                darkroom.waitUntilClose();
            }
            catch (Exception e){
                e.getMessage()
            }
            finally {
                try {
                    if (driver) {
                        driver.close()
                        driver.quit()
                    }
                } catch (some) {
                }
            }
        then:
            !photos.empty

    }

    Path screenshotDirectory;

    long startTime;

    def setup() {
        screenshotDirectory = Files.createTempDirectory("screenshots")
        startTime = System.currentTimeMillis()
        darkroom = new Darkroom()
        darkroom.isOpenForBusiness()
    }

    def cleanup() {
        darkroom.terminate()
        println "Test duration: " + (System.currentTimeMillis() - startTime) + " ms"
    }

    String siteFromUrlAt(String path) {
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir, "src/test/resources" + path);
        return "file://" + testSite.getAbsolutePath();
    }

}

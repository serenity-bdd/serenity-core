package net.serenitybdd.core.photography.integration
import com.google.common.collect.Lists
import net.serenitybdd.core.photography.Darkroom
import net.serenitybdd.core.photography.Photographer
import net.serenitybdd.core.photography.ScreenshotPhoto
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class WhenAPhotographerTakesLotsOfScreenshots extends Specification {

    def "when a photographer takes a series of screenshots they all should be stored"(def Class browser) {
        given:
            def photographer = new Photographer();
        when:
            List<ScreenshotPhoto> photos = Lists.newArrayList();
            def driver
            try {
                driver = new FirefoxDriver()
                Darkroom.isOpenForBusiness();
                for (photoCount in 0..10) {
                    photos.add(photographer.takesAScreenshot()
                        .with(driver)
                        .andSaveToDirectory(screenshotDirectory));
                }
                Darkroom.waitUntilClose();
            } finally {
                try {
                    if (driver) {
                        driver.close()
                        driver.quit()
                    }
                } catch (some) {
                }
            }
        then:
            photos.findAll {
                photo -> Files.exists(photo.pathToScreenshot)
            }.size() == 11
        where:
            browser << [ChromeDriver.class, FirefoxDriver.class]
    }

    def "should handle multiple screenshots in parallel"(def Class browser){
        given:
            def photographer = new Photographer();
            List<ScreenshotPhoto> files = Lists.newArrayList();
            def Integer threads = 10
            def Integer photos = 10
        and:
            def runners = []
            for (i in 1..threads) {
                runners << new Thread(new Runnable() {
                    @Override
                    void run() {
                        Darkroom.isOpenForBusiness();
                        def driver = null
                        try {
                            driver = (WebDriver)browser.newInstance()
                            driver.get(siteFromUrlAt("/static-site/index.html"))
                            for (j in 1..files) {
                                files.add(photographer.takesAScreenshot()
                                    .with(driver)
                                    .andSaveToDirectory(screenshotDirectory));
                            }
                        } finally {
                            try {
                                if (driver) {
                                    driver.close()
                                    driver.quit()
                                }
                            } catch (some) {
                            }
                        }
                        Darkroom.waitUntilClose();
                    }
                })
            }
            println runners
        when:
            runners.each {
                it.start()
                it.join()
            }
            runners.each { it.join() }
        then:
            files.size() == threads * photos
            files.findAll {
                photo -> Files.exists(photo.pathToScreenshot)
            }.size() == threads * photos
        where:
            browser << [ChromeDriver.class, FirefoxDriver.class]
    }

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

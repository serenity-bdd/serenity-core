package net.serenitybdd.core.photography.integration

import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy
import com.google.common.collect.Lists
import net.serenitybdd.core.photography.Darkroom
import net.serenitybdd.core.photography.Photographer
import net.serenitybdd.core.photography.ScreenshotPhoto
import org.openqa.selenium.phantomjs.PhantomJSDriver
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class WhenAPhotographerTakesLotsOfScreenshots extends Specification {

    Darkroom darkroom

    def "when a photographer takes a series of screenshots they all should be stored"() {
        given:
            def photographer = new Photographer(darkroom, ScrollStrategy.VIEWPORT_ONLY);
        when:
            List<ScreenshotPhoto> photos = Lists.newArrayList();
            def driver
            try {
                driver = new PhantomJSDriver()
                darkroom = new Darkroom()
                darkroom.isOpenForBusiness();
                for (photoCount in 0..10) {
                    photos.add(photographer.takesAScreenshot()
                        .with(driver)
                        .andSaveToDirectory(screenshotDirectory));
                }
                darkroom.waitUntilClose();
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

    }

    @Ignore("For exploratory and performance testing")
    def "should handle multiple screenshots in parallel"() {
        given:
            def photographer = new Photographer(darkroom, ScrollStrategy.VIEWPORT_ONLY);
            def List<Future<List<ScreenshotPhoto>>> processing = new ArrayList<>();
            def List<ScreenshotPhoto> screenshots = new ArrayList<>();
            def Integer threads = 10
            def Integer photos = 5

            def ExecutorService service = Executors.newFixedThreadPool(10);
        when:
            threads.times {
                processing << service.submit(new Callable<List<ScreenshotPhoto>>() {
                    @Override
                    List<ScreenshotPhoto> call() throws Exception {
                        def List<ScreenshotPhoto> photo = new ArrayList<>()
                        darkroom = new Darkroom()
                        darkroom.isOpenForBusiness();
                        def driver = null
                        try {
                            driver = new PhantomJSDriver()
                            driver.get(siteFromUrlAt("/static-site/index.html"))
                            photos.times {
                                photo.add(photographer.takesAScreenshot()
                                    .with(driver)
                                    .andSaveToDirectory(screenshotDirectory));
                            }
                        } catch (some) {
                            println some

                        } finally {
                            try {
                                if (driver) {
                                    driver.close()
                                    driver.quit()
                                }
                            } catch (some) {
                            }
                        }
                        darkroom.waitUntilClose();
                        return photo
                    }
                })
            }
            processing.each { future ->
                screenshots.addAll(future.get())
            }
        then:
            processing.size() == threads
            screenshots.size() == threads * photos
            screenshots.findAll {
                photo -> Files.exists(photo.pathToScreenshot)
            }.size() == threads * photos
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

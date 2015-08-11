package net.thucydides.core.screenshots.integration

import com.google.common.io.Files
import net.thucydides.core.screenshots.QueuedScreenshot
import net.thucydides.core.screenshots.SingleThreadScreenshotProcessor
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.FileSystemUtils
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenProcessingScreenshots extends Specification {

    File targetDirectory
    File sourceDirectory
    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    def setup() {
        targetDirectory = File.createTempFile("tmp","screenshots")
        targetDirectory.delete()
        targetDirectory.mkdir()
        targetDirectory.deleteOnExit()

        sourceDirectory = File.createTempFile("tmp","screenshot-source")
        sourceDirectory.delete()
        sourceDirectory.mkdir()
        sourceDirectory.deleteOnExit()

    }

    private File copySourceScreenshot(File sourceDirectory) {
        def screenshotsSourceDirectory = FileSystemUtils.getResourceAsFile("screenshots");// new File(Thread.currentThread().getContextClassLoader().getResource("screenshots").getPath());
        def sampleScreenshot = new File(screenshotsSourceDirectory, "amazon.png")
        def timestamp = System.currentTimeMillis()
        def screenshot = new File(sourceDirectory, "amazon-${timestamp}.png")
        Files.copy(sampleScreenshot, screenshot)
        Thread.sleep(100)
        return screenshot;
    }

    def "should process queued screenshots"() {
        given:
            def screenshotProcessor = new SingleThreadScreenshotProcessor(environmentVariables)
        when:
            (1..10).each {
                def screenshotFile = copySourceScreenshot(sourceDirectory)
                def targetFile = new File(targetDirectory,"screenshot-${it}.png")
                screenshotProcessor.queueScreenshot(new QueuedScreenshot(screenshotFile,targetFile))
            }
            screenshotProcessor.waitUntilDone()
            screenshotProcessor.terminate();
        then:
            assert (screenshotProcessor.isEmpty())
            assert targetDirectory.list().size() == 10
    }
}

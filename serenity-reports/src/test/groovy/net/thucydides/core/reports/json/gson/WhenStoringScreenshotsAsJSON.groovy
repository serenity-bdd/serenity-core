package net.thucydides.core.reports.json.gson

import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.reports.json.gson.GsonJSONConverter
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import spock.lang.Specification

class WhenStoringScreenshotsAsJSON extends Specification {

    def environmentVars = new MockEnvironmentVariables()

    def "should generate JSON for a screenshot"() {
        given:
        def screenshotFile = new File("screenshot.png")
        def htmlSource = new File("screenshot.html")
        def screenshot = new ScreenshotAndHtmlSource(screenshotFile, htmlSource)

        when:
        def converter = new GsonJSONConverter(environmentVars)
        def serializedJson = converter.gson.toJson(screenshot)

        then:
        def expectedJson = """
{
  "screenshot" : "screenshot.png",
  "htmlSource" : "screenshot.html"
}
"""
        JSONCompare.compareJSON(expectedJson, serializedJson, JSONCompareMode.LENIENT).passed()
    }


    def "should generate JSON for a screenshot with no source code"() {
        given:
        def screenshotFile = new File("screenshot.png")
        def screenshot = new ScreenshotAndHtmlSource(screenshotFile)

        when:
        def converter = new GsonJSONConverter(environmentVars)
        def serializedJson = converter.gson.toJson(screenshot)

        then:
        def expectedJson = """
{
  "screenshot" : "screenshot.png"
}
"""
        JSONCompare.compareJSON(expectedJson, serializedJson, JSONCompareMode.LENIENT).passed()
    }

    def "should read a screenshot from JSON"() {
        given:
        def serializedScreenshot = """
{
  "screenshot" : "screenshot.png",
  "htmlSource" : "screenshot.html"
}
"""
        def reader = new StringReader(serializedScreenshot)

        when:
        def converter = new GsonJSONConverter(environmentVars)
        def screenshot = converter.gson.fromJson(serializedScreenshot, ScreenshotAndHtmlSource)

        then:
        screenshot.screenshot.name == "screenshot.png"
        screenshot.htmlSource.isPresent()
        screenshot.htmlSource.get().name == "screenshot.html"
    }


    def "should read a screenshot with no source code from JSON"() {
        given:
        def screenshotFile = new File("screenshot.png")

        def serializedScreenshot = """
{
  "screenshot" : "screenshot.png"
}
"""
        when:
        def converter = new GsonJSONConverter(environmentVars)
        def screenshot = converter.gson.fromJson(serializedScreenshot, ScreenshotAndHtmlSource)

        then:
        screenshot.screenshot.absolutePath == screenshotFile.absolutePath
        !screenshot.htmlSource.isPresent()
    }
}

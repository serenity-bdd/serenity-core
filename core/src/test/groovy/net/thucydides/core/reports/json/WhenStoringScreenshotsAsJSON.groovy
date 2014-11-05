package net.thucydides.core.reports.json

import net.thucydides.core.reports.json.jackson.JacksonJSONConverter
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource
import net.thucydides.core.util.MockEnvironmentVariables
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import spock.lang.Specification

class WhenStoringScreenshotsAsJSON extends Specification {

    def environmentVars = new MockEnvironmentVariables();

    def "should generate JSON for a screenshot"() {
        given:
        def screenshotFile = new File("screenshot.png")
        def htmlSource = new File("screenshot.html")
        def screenshot = new ScreenshotAndHtmlSource(screenshotFile, htmlSource)

        when:
        StringWriter writer = new StringWriter();
        def converter = new JacksonJSONConverter(environmentVars)
        converter.mapper.writerWithDefaultPrettyPrinter().writeValue(writer, screenshot);

        then:
        def expectedJson = """
{
  "screenshot" : "screenshot.png",
  "htmlSource" : "screenshot.html"
}
"""
        def serializedStory = writer.toString()
        println serializedStory
        JSONCompare.compareJSON(expectedJson, serializedStory, JSONCompareMode.LENIENT).passed();
    }


    def "should generate JSON for a screenshot with no source code"() {
        given:
        def screenshotFile = new File("screenshot.png")
        def screenshot = new ScreenshotAndHtmlSource(screenshotFile)

        when:
        StringWriter writer = new StringWriter();
        def converter = new JacksonJSONConverter(environmentVars)
        converter.mapper.writerWithDefaultPrettyPrinter().writeValue(writer, screenshot);

        then:
        def expectedJson = """
{
  "screenshot" : "screenshot.png"
}
"""
        def serializedStory = writer.toString()
        JSONCompare.compareJSON(expectedJson, serializedStory, JSONCompareMode.LENIENT).passed();
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
        def converter = new JacksonJSONConverter(environmentVars)
        def screenshot = converter.mapper.readValue(reader,ScreenshotAndHtmlSource)

        then:
        screenshot.screenshotFile.name == "screenshot.png"
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
        def reader = new StringReader(serializedScreenshot)

        when:
        def converter = new JacksonJSONConverter(environmentVars)
        def screenshot = converter.mapper.readValue(reader,ScreenshotAndHtmlSource)

        then:
        screenshot.screenshotFile.absolutePath == screenshotFile.absolutePath
        !screenshot.htmlSource.isPresent()
    }
}
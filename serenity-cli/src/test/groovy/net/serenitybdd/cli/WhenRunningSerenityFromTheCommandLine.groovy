package net.serenitybdd.cli

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * Created by john on 22/06/2016.
 */
class WhenRunningSerenityFromTheCommandLine extends Specification {

    Path outputDirectory = Files.createTempDirectory("reports")

    def "should display help"() {
        given:
        StringWriter capturedOutput = new StringWriter();
        PrintWriter targetOutput = new PrintWriter(capturedOutput);

        when:
        new Serenity(targetOutput).executeWith("--help")
        def helpMessage = capturedOutput.toString()
        then:
        helpMessage.contains("Serenity BDD Command Line Tool")
    }

    def "should generate aggregate reports in an output directory"() {
        given:
        Path sourceDirectory = Paths.get("src/test/resources/test-outcomes")
        assert outputDirectory.toFile().list().length == 0

        when:
        new Serenity().executeWith("-source", sourceDirectory.toAbsolutePath().toString(),
                "-destination", outputDirectory.toAbsolutePath().toString())
        then:
        outputDirectory.toFile().list()
    }

    def "should be able to provide a project name"() {
        given:
        Path sourceDirectory = Paths.get("src/test/resources/test-outcomes")
        assert outputDirectory.toFile().list().length == 0

        when:
        new Serenity().executeWith("-source", sourceDirectory.toAbsolutePath().toString(),
                "-destination", outputDirectory.toAbsolutePath().toString(),
                "-project foo")
        then:
        outputDirectory.resolve("index.html").text.contains("foo")
    }

}
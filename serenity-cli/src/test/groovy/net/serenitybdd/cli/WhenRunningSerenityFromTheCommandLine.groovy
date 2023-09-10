package net.serenitybdd.cli

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

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
        new Serenity().executeWith("--source", sourceDirectory.toAbsolutePath().toString(),
                "--destination", outputDirectory.toAbsolutePath().toString())
        then:
        outputDirectory.toFile().list()
    }

    def EXPECTED_HTML_OUTCOME = ".html"

    def "should generate html test reports in the output directory"() {
        given:
        Path sourceDirectory = Paths.get("src/test/resources/test-outcomes")
        assert outputDirectory.toFile().list().length == 0

        when:
        new Serenity().executeWith("--source", sourceDirectory.toAbsolutePath().toString(),
                "--destination", outputDirectory.toAbsolutePath().toString())
        then:

        assert outputDirectory.toFile().list().any {
            it -> it.endsWith(EXPECTED_HTML_OUTCOME)
        }
    }

    def "should be able to provide a project name"() {
        given:
        Path sourceDirectory = Paths.get("src/test/resources/test-outcomes")
        assert outputDirectory.toFile().list().length == 0

        when:
        new Serenity().executeWith("--source", sourceDirectory.toAbsolutePath().toString(),
                "--destination", outputDirectory.toAbsolutePath().toString(),
                "--project foo")
        then:
        outputDirectory.resolve("index.html").toFile().text.contains("foo")
    }

    def "should generate requirements test reports in the output directory"() {
        given:
        Path sourceDirectory = Paths.get("src/test/resources/test-outcomes")
        Path featureDirectory = Paths.get("src/test/resources/featuresdir")
        assert outputDirectory.toFile().list().length == 0

        when:
        new Serenity().executeWith("--source", sourceDirectory.toAbsolutePath().toString(),
                "--destination", outputDirectory.toAbsolutePath().toString(),
                "--features", featureDirectory.toAbsolutePath().toString()
        )
        then:
        outputDirectory.resolve("capabilities.html").toFile().exists()
        outputDirectory.resolve("capabilities.html").toFile().text.contains("Remember things to do")
"" +
        "git push"    }

    def "should generate requirements test reports from JS test outcomes"() {
        given:
        Path sourceDirectory = Paths.get("src/test/resources/js-outputs")
        assert outputDirectory.toFile().list().length == 0

        when:
        new Serenity().executeWith("--source", sourceDirectory.toAbsolutePath().toString(),
                "--destination", outputDirectory.toAbsolutePath().toString(),
                "--features","no/such/directory")
        then:
        outputDirectory.resolve("capabilities.html").toFile().exists()
    }

    def "should copy report resources to the output directory"() {
        given:
        Path sourceDirectory = Paths.get("src/test/resources/test-outcomes")
        assert outputDirectory.toFile().list().length == 0

        when:
        new Serenity().executeWith("--source", sourceDirectory.toAbsolutePath().toString(),
                "--destination", outputDirectory.toAbsolutePath().toString())
        then:
        Path jqplotCss = Paths.get(outputDirectory.toFile().absolutePath, "bootstrap/css/bootstrap.min.css")
        Path jqplotJS = Paths.get(outputDirectory.toFile().absolutePath, "bootstrap/css/bootstrap.min.css")

        jqplotCss.toFile().exists() && jqplotJS.toFile().exists()
    }

}

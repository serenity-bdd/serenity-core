package net.serenitybdd.core.webdriver.servicepools

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

class WhenFindingDriverServiceExecutables extends Specification {

    @Rule
    TemporaryFolder temporaryFolder

    def "should find a binary on the system path if no other paths are specified"() {
        when:
        File gitExe = DriverServiceExecutable.called("git")
                .withSystemProperty("undefined.property")
                .downloadableFrom("the internet")
                .asAFile()
        then:
        gitExe.exists()
    }

    def "should look for a file using the configured environment property if defined"() {
        given:
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
        String configuredPath = Paths.get(ClassLoader.getResource("/binaries/phantomjs").toURI())
        environmentVariables.setProperty("my.exe.path", configuredPath)
        when:
        File gitExe = DriverServiceExecutable.called("git")
                .withSystemProperty("my.exe.path")
                .usingEnvironmentVariables(environmentVariables)
                .downloadableFrom("the internet")
                .asAFile()
        then:
        gitExe.getAbsolutePath() == configuredPath
    }

    def "should report a sensible error if no binary is found"() {
        when:
        DriverServiceExecutable.called("myexe")
                .withSystemProperty("my.exe.path")
                .reportMissingBinary()
                .downloadableFrom("the internet")
                .asAFile()

        then:
        IllegalStateException e = thrown();
        e.message == "The path to the myexe driver executable must be set by the my.exe.path system property; for more information, see the internet. The latest version can be downloaded from the internet"
    }

    def "should be able to document the driver web site"() {
        when:
        DriverServiceExecutable.called("myexe")
                .withSystemProperty("my.exe.path")
                .documentedAt("the website")
                .reportMissingBinary()
                .downloadableFrom("the internet")
                .asAFile()

        then:
        IllegalStateException e = thrown();
        e.message == "The path to the myexe driver executable must be set by the my.exe.path system property; for more information, see the website. The latest version can be downloaded from the internet"
    }

    def "should be able to configure OS-specific drivers"() {
        given:
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
        String os = CurrentOS.type
        environmentVariables.setProperty("drivers.${os}.my.exe.path","/my/path")

        when:
            Path driverPath = DriverServiceExecutable.called("myexe")
                    .usingEnvironmentVariables(environmentVariables)
                    .withSystemProperty("my.exe.path")
                    .documentedAt("the website")
                    .downloadableFrom("the internet")
                    .asAPath()
        then:
            driverPath.toString() == "/my/path"

    }
}
package net.serenitybdd.core.webdriver.servicepools

import net.serenitybdd.model.CurrentOS
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.util.EnvironmentVariables
import spock.lang.Specification

import java.nio.file.Path

class WhenFindingDriverServiceExecutables extends Specification {

    def "should find a binary on the system path if no other paths are specified"() {
        when:
        File gitExe = DriverServiceExecutable.called("git")
                .withSystemProperty("undefined.property")
                .downloadableFrom("the internet")
                .asAFile()
        then:
        gitExe.exists()
    }

    def MY_PATH = File.separator + "my" + File.separator + "path"

    def "should be able to configure OS-specific drivers"() {
        given:
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
        String os = CurrentOS.type
        environmentVariables.setProperty("drivers.${os}.my.exe.path",MY_PATH)

        when:
            Path driverPath = DriverServiceExecutable.called("myexe")
                    .usingEnvironmentVariables(environmentVariables)
                    .withSystemProperty("my.exe.path")
                    .documentedAt("the website")
                    .downloadableFrom("the internet")
                    .asAPath()
        then:
            driverPath.toString() == MY_PATH

    }
}

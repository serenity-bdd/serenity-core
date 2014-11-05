package net.thucydides.core.requirements.reportpages

import net.thucydides.core.pages.PageObject
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver

/**
 * Models the requirements progress report page for testing purposes
 */
class ProgressReport extends PageObject {

    static ProgressReport inDirectory(File directory) {
        def driver = new PhantomJSDriver();
        def report = new ProgressReport(driver)
        report.openAt("file:///" +  directory.getAbsolutePath() + "/progress-report.html");
        return report
    }

    ProgressReport(WebDriver driver) {
        super(driver)
    }

    def getProgressGraph() {
        return element(By.id("progress-graph"))
    }

    List<String> getYAxes() {
        def axesElements = findAll(By.cssSelector(".dygraph-axis-label-y"));
        axesElements.collect { axis -> axis.text }
    }


    def close() {
        driver.quit()
    }
}

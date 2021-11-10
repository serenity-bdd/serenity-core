package net.thucydides.core.requirements.reportpages

import net.serenitybdd.core.pages.PageObject
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

/**
 * Models the requirements progress report page for testing purposes
 */
class ProgressReport extends PageObject {

    static ProgressReport inDirectory(File directory) {
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        def driver = new ChromeDriver(options);
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

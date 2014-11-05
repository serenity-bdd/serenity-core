package net.thucydides.core.requirements.reportpages

import net.thucydides.core.pages.PageObject
import net.thucydides.core.pages.WebElementFacade
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.phantomjs.PhantomJSDriver

/**
 * Models the capabilities report page for testing purposes
 */
class RequirementsReport extends PageObject {

    static RequirementsReport inDirectory(File directory) {
        def driver = new PhantomJSDriver();
        def report = new RequirementsReport(driver)
        report.openAt("file:///" +  directory.getAbsolutePath() + "/capabilities.html");
        return report
    }

    RequirementsReport(WebDriver driver) {
        super(driver)
    }

    List<String> getNames() {
        driver.findElements(By.cssSelector(".requirementName")).collect { name ->
            name.text
        }
    }

    String getTableTitle() {
        List<WebElementFacade> titleTab = findAll(By.cssSelector("a[href='#tabs-1']"));
        return (!titleTab.isEmpty()) ? titleTab.get(0).getText() : ""
    }

    List<RequirementRow> getRequirements() {
        List<WebElement> rows = driver.findElements(By.cssSelector("#req-results-table .requirementRow"));
        rows?.collect {
            List<WebElement> cells = it.findElements(By.cssSelector(".requirementRowCell"))
            def iconImage = it.findElement(By.cssSelector(".summary-icon")).getAttribute("src")
            new RequirementRow(id: cells[1]?.text,
                               description : cells[2]?.text,
                               children: cells[3] ? Integer.parseInt(cells[3].text) : 0,
                               tests: cells[4]? Integer.parseInt(cells[4].text) : 0,
                               icon: iconImage)
        }
    }

    def close() {
        driver.quit()
    }

    class RequirementRow {
        String id
        String description
        String icon
        int children
        int tests
    }
}

package net.serenitybdd.core.webdriver.integration;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.pages.*;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.shadow.ByShadow;
import net.thucydides.core.webelements.RadioButtonGroup;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WhenInteractingWithShadowDomElements {

    public class IndexPage extends PageObject {
        public IndexPage(WebDriver driver, int timeout) {
            super(driver, timeout);
        }
    }

    public class IndexPageWithShortTimeout extends PageObject {

        public WebElement checkbox;

        public IndexPageWithShortTimeout(WebDriver driver, int timeout) {
            super(driver, timeout);
        }
    }

    IndexPage indexPage;

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    static WebDriver driver;

    @BeforeClass
    public static void openDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200");
        driver = new ChromeDriver(options);
    }

    @Before
    public void openLocalStaticSite() {
        openStaticTestSite("page-with-shadow-dom.html");
        indexPage = new IndexPage(driver, 1);
        indexPage.setWaitForTimeout(100);
    }

    @AfterClass
    public static void closeDriver() {
    	if (driver != null) {
	        driver.close();
	        driver.quit();
    	}
    }

    @Before
    public void initConfiguration() {
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    private void openStaticTestSite(String pageName) {
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir, "src/test/resources/static-site/" + pageName);
        driver.get("file://" + testSite.getAbsolutePath());
    }

    @Test
    public void should_find_page_title() {
        assertThat(indexPage.getTitle(), is("Test: elements with Shadow-DOM"));
    }

    @Test
    public void locate_a_shadow_dom_element() {
        WebElementFacade shadowInput = indexPage.find(ByShadow.cssSelector("#inputInShadow","#shadow-host"));
        assertTrue(shadowInput.isVisible());
    }

    @Test
    public void locate_a_nested_shadow_dom_element() {
        WebElementFacade shadowInput = indexPage.find(ByShadow.cssSelector("#inputInInnerShadow","#shadow-host", "#inner-shadow-host"));
        assertTrue(shadowInput.isVisible());
    }

    @Test
    public void locate_nested_shadow_dom_elements() {
        ListOfWebElementFacades shadowInputs = indexPage.findAll(ByShadow.cssSelector(".test-class","#shadow-host"));
        assertThat(shadowInputs.size(), equalTo(2));
    }

    @Test
    public void locate_nested_shadow_dom_elements_using_a_more_readable_dsl() {
        ListOfWebElementFacades shadowInputs = indexPage.findAll(ByShadow.css(".test-class").inHost("#shadow-host"));
        assertThat(shadowInputs.size(), equalTo(2));
    }

    @Test
    public void interact_with_a_shadow_dom_element() {
        WebElementFacade shadowInput = indexPage.find(ByShadow.cssSelector("#inputInShadow","#shadow-host"));
        shadowInput.sendKeys("Some value");
        assertThat(shadowInput.getValue(), is("Some value"));
    }

    @Test
    public void interact_with_a_nested_shadow_dom_element() {
        WebElementFacade shadowInput = indexPage.find(ByShadow.cssSelector("#inputInInnerShadow","#shadow-host", "#inner-shadow-host"));
        shadowInput.sendKeys("Some value");
        assertThat(shadowInput.getValue(), is("Some value"));
    }

    @Test
    public void interact_with_a_nested_shadow_dom_element_using_the_dsl() {
        WebElementFacade shadowInput = indexPage.find(ByShadow.css("#inputInInnerShadow").inHosts("#shadow-host", "#inner-shadow-host"));
        shadowInput.sendKeys("Some value");
        assertThat(shadowInput.getValue(), is("Some value"));
    }

    @Test
    public void interact_with_a_nested_shadow_dom_element_using_the_dsl_with_nesting() {
        WebElementFacade shadowInput = indexPage.find(ByShadow.css("#inputInInnerShadow").inHost("#shadow-host").thenInHost("#inner-shadow-host"));
        shadowInput.sendKeys("Some value");
        assertThat(shadowInput.getValue(), is("Some value"));
    }

    @Test
    public void interacting_with_a_web_component() {
        driver.get("https://mdn.github.io/web-components-examples/word-count-web-component/");

        WebElement wordCountComponent = driver.findElement(ByShadow.cssSelector("span","p[is='word-count']"));
        assertThat(wordCountComponent.getText(), is("Words: 212"));
    }
}

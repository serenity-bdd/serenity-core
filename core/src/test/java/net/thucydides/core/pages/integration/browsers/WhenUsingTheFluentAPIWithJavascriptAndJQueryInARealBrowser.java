package net.thucydides.core.pages.integration.browsers;


import net.thucydides.core.pages.integration.StaticSitePage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheFluentAPIWithJavascriptAndJQueryInARealBrowser {

    private static StaticSitePage page;
    private static WebDriver driver;

    @BeforeClass
    public static void openDriver() {
        driver = new FirefoxDriver();
        page = new StaticSitePage(driver, 1000);
        page.open();
    }

    @AfterClass
    public static void shutdownDriver() {
        driver.quit();
    }

    @Test
    public void should_check_and_close_javascript_alerts() {
        page.open();

		page.openAlert();
        page.getAlert().accept();

        assertThat(page.getTitle(), is("Thucydides Test Site"));
    }


    public StaticSitePage getPage() {
        return page;
    }
}

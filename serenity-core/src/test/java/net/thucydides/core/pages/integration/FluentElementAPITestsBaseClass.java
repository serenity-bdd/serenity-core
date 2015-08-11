package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.StaticTestSite;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;

public class FluentElementAPITestsBaseClass {

    private static StaticTestSite staticTestSite;
    private static StaticSitePage chromePage;
    private static StaticSitePage phantomjsPage;

    @BeforeClass
    public static void openStaticSite() {
        staticTestSite = new StaticTestSite();
    }

    protected static StaticTestSite getStaticTestSite() {
        return staticTestSite;
    }

    @After
    public void closeChrome() {
        if (chromePage != null) {
            chromePage.getDriver().close();
            chromePage.getDriver().quit();
            chromePage = null;
        }
    }

    protected StaticSitePage getChromePage() {
        if (chromePage == null) {
            WebDriver driver = getStaticTestSite().open("chrome");
            chromePage = new StaticSitePage(driver);
            chromePage.open();
        }
        return chromePage;
    }

    protected StaticSitePage getPhantomJSPage() {
        if (phantomjsPage == null) {
            WebDriver driver = getStaticTestSite().open("phantomjs");
            phantomjsPage = new StaticSitePage(driver);
            phantomjsPage.open();
        }
        return phantomjsPage;
    }

    @AfterClass
    public static void closeBrowsers() {
        getStaticTestSite().close();
    }

    protected void refresh(StaticSitePage page) {
        page.getDriver().navigate().refresh();
    }

    protected boolean runningOnLinux() {
        return System.getProperty("os.name").contains("Linux");
    }

}

package net.thucydides.core.webdriver;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class WhenUsingADisabledWebDriverFacade {

    class DisabledWebDriverFacade extends WebDriverFacade {
        DisabledWebDriverFacade(Class<? extends WebDriver> driverClass, WebDriverFactory webDriverFactory) {
            super(driverClass, webDriverFactory);
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    }

    @Mock
    WebDriverFactory webDriverFactory;

    WebDriverFacade webDriverFacade;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        webDriverFacade = new DisabledWebDriverFacade(FirefoxDriver.class, webDriverFactory);
    }

    @Test
    public void should_ignore_get_url() {
        webDriverFacade.get("http://some.url");
    }

    @Test
    public void the_current_url_should_be_empty() {
        assertThat(webDriverFacade.getCurrentUrl(), is(""));
    }

    @Test
    public void the_title_should_be_empty() {
        assertThat(webDriverFacade.getTitle(), is(""));
    }

    @Test
    public void find_elements_should_return_an_empty_list() {
        assertThat(webDriverFacade.findElements(By.id("someId")).size(), is(0));
    }

    @Test
    public void page_source_should_be_empty() {
        assertThat(webDriverFacade.getPageSource(), is(""));
    }

    @Test
    public void window_handle_should_be_empty() {
        assertThat(webDriverFacade.getWindowHandle(), is(""));
    }

    @Test
    public void window_handles_should_be_empty() {
        assertThat(webDriverFacade.getWindowHandles().size(), is(0));
    }

    @Mock
    WebElement frameElement;

    @Test
    public void switchTo_should_be_ignored() {
        webDriverFacade.switchTo().frame("someFrame");
        webDriverFacade.switchTo().frame(1);
        webDriverFacade.switchTo().frame(frameElement);
        webDriverFacade.switchTo().defaultContent();
        webDriverFacade.switchTo().window("someWindow");
    }

    @Test
    public void alerts_should_be_ignored() {
        webDriverFacade.switchTo().alert().accept();
        webDriverFacade.switchTo().alert().dismiss();
        assertThat(webDriverFacade.switchTo().alert().getText(), is(""));
        webDriverFacade.switchTo().alert().sendKeys("abc");
    }

    @Test(expected = ElementNotInteractableException.class)
    public void switchTo_web_element_should_throw_element_not_visible() {
        webDriverFacade.switchTo().activeElement();
    }

    @Test
    public void navigate_should_be_ignored() {
        webDriverFacade.navigate().to("http://some.url");
    }

    @Test
    public void manage_should_be_ignored() {
        assertThat(webDriverFacade.manage().getCookies().size(), is(0));
    }

    @Test
    public void cookies_should_be_ignored() {
        assertThat(webDriverFacade.manage().getCookieNamed("someCookie").getName(), is("someCookie"));
    }

    @Test
    public void manage_timeouts_should_be_ignored() {
        webDriverFacade.manage().timeouts().implicitlyWait(Duration.ofSeconds(100)).scriptTimeout(Duration.ofSeconds(100));
    }

    @Test
    public void manage_windows_should_be_ignored() {
        assertThat(webDriverFacade.manage().window().getPosition(), is(notNullValue()));
        assertThat(webDriverFacade.manage().window().getSize(), is(notNullValue()));
    }

    @Test
    public void manage_ime_should_be_ignored() {
        webDriverFacade.manage().ime().activateEngine("whatever");
        webDriverFacade.manage().ime().deactivate();
        assertThat(webDriverFacade.manage().ime().getActiveEngine(), is(""));
        assertThat(webDriverFacade.manage().ime().isActivated(), is(false));
        assertThat(webDriverFacade.manage().ime().getAvailableEngines().size(), is(0));
        assertThat(webDriverFacade.manage().ime().getAvailableEngines().size(), is(0));


    }


}

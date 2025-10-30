package net.thucydides.core.webdriver.javascript;

import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenWorkingWithJavascript {

    @Mock
    WebDriverFactory factory;

    @Mock FirefoxDriver mockDriver;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void a_firefox_backed_driver_should_support_javascript() {
        assertThat(JavascriptSupport.javascriptIsSupportedIn(FirefoxDriver.class), is(true));

    }

    @Test
    public void an_iexplored_backed_driver_should_support_javascript() {
        assertThat(JavascriptSupport.javascriptIsSupportedIn(InternetExplorerDriver.class), is(true));
    }


    @Test
    public void a_javascript_executor_should_support_javascript() {
        assertThat(JavascriptSupport.javascriptIsSupportedIn(JavascriptWebdriver.class), is(true));
    }

    @Test
    public void a_driver_not_implementing_javascript_executor_should_not_support_javascript() {
        assertThat(JavascriptSupport.javascriptIsSupportedIn(NonJavascriptWebdriver.class), is(false));
    }

    @Test
    public void a_mock_driver_should_not_support_javascript() {
        assertThat(JavascriptSupport.javascriptIsSupportedIn(mockDriver), is(false));
        assertThat(JavascriptSupport.javascriptIsSupportedIn(mockDriver.getClass()), is(false));
    }

    @Test
    public void javascript_support_can_be_checked_on_a_driver() {
        assertThat(JavascriptSupport.javascriptIsSupportedIn(mockDriver.getClass()), is(false));
    }

    @Test
    public void javascript_support_can_be_checked_on_a_driver_facade() {
        WebDriverFacade webDriverFacade = new WebDriverFacade(FirefoxDriver.class, factory);
        assertThat(JavascriptSupport.javascriptIsSupportedIn(webDriverFacade), is(true));
    }

    static class JavascriptWebdriver implements JavascriptExecutor, WebDriver {

        @Override
        public Object executeScript(String script, Object... args) {
            return null;  
        }

        @Override
        public Object executeAsyncScript(String script, Object... args) {
            return null;  
        }

        @Override
        public void get(String url) {
            
        }

        @Override
        public String getCurrentUrl() {
            return null;  
        }

        @Override
        public String getTitle() {
            return null;  
        }

        @Override
        public List<WebElement> findElements(By by) {
            return null;  
        }

        @Override
        public WebElement findElement(By by) {
            return null;  
        }

        @Override
        public String getPageSource() {
            return null;  
        }

        @Override
        public void close() {
            
        }

        @Override
        public void quit() {
            
        }

        @Override
        public Set<String> getWindowHandles() {
            return null;  
        }

        @Override
        public String getWindowHandle() {
            return null;  
        }

        @Override
        public TargetLocator switchTo() {
            return null;  
        }

        @Override
        public Navigation navigate() {
            return null;  
        }

        @Override
        public Options manage() {
            return null;  
        }
    }


    static class NonJavascriptWebdriver implements WebDriver {

        @Override
        public void get(String url) {
            
        }

        @Override
        public String getCurrentUrl() {
            return null;  
        }

        @Override
        public String getTitle() {
            return null;  
        }

        @Override
        public List<WebElement> findElements(By by) {
            return null;  
        }

        @Override
        public WebElement findElement(By by) {
            return null;  
        }

        @Override
        public String getPageSource() {
            return null;  
        }

        @Override
        public void close() {
            
        }

        @Override
        public void quit() {
            
        }

        @Override
        public Set<String> getWindowHandles() {
            return null;  
        }

        @Override
        public String getWindowHandle() {
            return null;  
        }

        @Override
        public TargetLocator switchTo() {
            return null;  
        }

        @Override
        public Navigation navigate() {
            return null;  
        }

        @Override
        public Options manage() {
            return null;  
        }
    }

}

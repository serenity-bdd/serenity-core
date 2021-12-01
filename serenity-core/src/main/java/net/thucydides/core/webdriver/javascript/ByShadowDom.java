package net.thucydides.core.webdriver.javascript;

import net.serenitybdd.core.annotations.findby.By;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * By Selector that finds Shadow Dom elements.
 *
 * Based on implementation from https://github.com/Georgegriff/query-selector-shadow-dom
 * QuerySelector that can pierce Shadow DOM roots without knowing the path through nested shadow roots.
 * This class is now deprecated: Use the Selenium 4 implementation ByShadow
 */
@Deprecated
public class ByShadowDom extends By {

    private final static Logger LOGGER = LoggerFactory.getLogger(ByShadowDom.class);

    private final String shadowDomSelector;

    private static String  loadedScript;

    static {
        try {
            InputStream resourceAsStream = ByShadowDom.class.getResourceAsStream("/javascript/querySelectorDeep.js");
            loadedScript = IOUtils.toString(resourceAsStream, "UTF-8");
        } catch(IOException ex) {
            LOGGER.error("Cannot load script for selecting shadow dom ",ex);
        }
    }

    private ByShadowDom(final String selector) {
        this.shadowDomSelector = selector;
    }

    public static ByShadowDom of(String selector) {
        return new ByShadowDom(selector);
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        StringBuffer scriptData  = new StringBuffer();
        scriptData.append(loadedScript);
        scriptData.append(" return querySelectorAllDeep('");
        scriptData.append(shadowDomSelector);
        scriptData.append("');");
        return (List<WebElement>) ((JavascriptExecutor) context).executeScript(scriptData.toString());
    }

    @Override
    public WebElement findElement(SearchContext context) {
        StringBuffer scriptData  = new StringBuffer();
        scriptData.append(loadedScript);
        scriptData.append(" return querySelectorDeep('");
        scriptData.append(shadowDomSelector);
        scriptData.append("');");
        WebElement element = (WebElement) ((JavascriptExecutor) context).executeScript(scriptData.toString());
        if (element == null) {
            throw new NoSuchElementException("No element found matching ShadowDom selector " + shadowDomSelector);
        } else {
            return element;
        }
    }

    @Override
    public String toString() {
        return "By.shadowDomSelector: " + shadowDomSelector;
    }
}

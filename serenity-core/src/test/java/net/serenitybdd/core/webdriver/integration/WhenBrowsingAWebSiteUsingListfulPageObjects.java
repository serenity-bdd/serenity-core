package net.serenitybdd.core.webdriver.integration;

import net.serenitybdd.core.annotations.ImplementedBy;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementFacadeImpl;
import net.thucydides.model.configuration.SystemPropertiesConfiguration;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.webdriver.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

public class WhenBrowsingAWebSiteUsingListfulPageObjects {
	
	private static final String DATA_TABLE_ID = "data";
	private static final String DATA_TABLE_ROW_CONTENT = "A,B,C";
	private static final int NUM_TABLES = 14;

	/**
	 * A page object which selects all {@literal <table>} using 3 typified methods.
	 * 
	 * @author Joe Nasca
	 */
    public static class TablesPage extends PageObject {

    	@FindBy(tagName = "table")
    	private List<Table> customFacadeTables;
    	
    	@org.openqa.selenium.support.FindBy(tagName = "table")
    	private List<WebElementFacade> facadeTables;
    	
    	@FindBy(tagName = "table")
    	private List<WebElement> elementTables;
    	
        public TablesPage(WebDriver driver, int timeout) {
            super(driver, timeout);
        }
    }

    @ImplementedBy(TableImpl.class)
    public interface Table extends WebElementFacade {
		List<Row> getBodyRows();
    }
    
    @ImplementedBy(RowImpl.class)
    public interface Row extends WebElementFacade {
		String getCsvContent();
    }
    
    public static class TableImpl extends WebElementFacadeImpl implements Table {
    	
    	private final WebDriver driver;

    	public TableImpl(WebDriver driver, ElementLocator locator, long implicitTimeoutInMilliseconds, long waitForTimeoutInMilliseconds) {
			this(driver, locator, null, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
		}

		public TableImpl(WebDriver driver, ElementLocator locator, WebElement webElement, long implicitTimeoutInMilliseconds, long waitForTimeoutInMilliseconds) {
			super(driver, locator, webElement, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
			this.driver = driver;
		}

		@Override
		public List<Row> getBodyRows() {

			return findElements(By.cssSelector("tbody tr")).stream()
					.map(elt -> new RowImpl(driver, null, elt, getImplicitTimeoutInMilliseconds()))
					.collect(Collectors.toList());
		}
    }
    
    public static class RowImpl extends WebElementFacadeImpl implements Row {
    	
    	public RowImpl(WebDriver driver, ElementLocator locator, long timeoutInMilliseconds) {
			super(driver, locator, timeoutInMilliseconds);
		}

		public RowImpl(WebDriver driver, ElementLocator locator, WebElement webElement, long timeoutInMilliseconds) {
			super(driver, locator, webElement, timeoutInMilliseconds);
		}

		@Override
    	public String getCsvContent() {
    		List<WebElement> cells = findElements(By.xpath("./th | ./td"));
    		StringBuilder buf = new StringBuilder();
    		for (Iterator<WebElement> iter = cells.iterator(); iter.hasNext();) {
    			buf.append(iter.next().getText());
    			if (iter.hasNext()) {
    				buf.append(",");
    			}
    		}
    		return buf.toString();
    	}
    }

    WebDriver driver;

    TablesPage indexPage;

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    @Before
    public void openLocalStaticSite() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		driver = new ChromeDriver(options);

        openStaticTestSite();
        indexPage = new TablesPage(driver, 1000);
        indexPage.setWaitForTimeout(100);
    }

    @Before
    public void initConfiguration() {
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    private void openStaticTestSite() {
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir, "src/test/resources/static-site/index.html");
        driver.get("file://" + testSite.getAbsolutePath());
    }
    
    @SuppressWarnings("unchecked")
	private <T> T findDataTable(List<? extends WebElement> tables) {
    	for (WebElement table : tables) {
			String id = table.getAttribute("id");
			System.out.printf("ID = " + id);
			if (DATA_TABLE_ID.equals(id)) {
    			return (T) table;
    		}
    	}
    	return null;
    }
    
    private Table tableFromElement(TablesPage page, WebElement elm) {
    	return new TableImpl(page.getDriver(), null, elm, page.getImplicitWaitTimeout().toMillis(),
														  page.getWaitForTimeout().toMillis());
    }

    @Test
    public void should_find_data_table_using_custom_facade_type_list() {
    	assertNotNull(findDataTable(indexPage.customFacadeTables));
    }
    
    @Test
    public void should_find_data_table_using_web_element_facade_list() {
    	assertNotNull(findDataTable(indexPage.facadeTables));
    }
    
    @Test
    public void should_find_data_table_using_web_element_list() {
    	assertNotNull(findDataTable(indexPage.elementTables));
    }
    
    @Test
    public void should_find_all_tables_using_custom_facade_type_list() {
    	assertThat(indexPage.customFacadeTables.size(), is(NUM_TABLES));
    }
    
    @Test
    public void should_find_all_tables_using_web_element_facade_list() {
    	assertThat(indexPage.facadeTables.size(), is(NUM_TABLES));
    }
    
    @Test
    public void should_find_all_tables_using_web_element_list() {
    	assertThat(indexPage.elementTables.size(), is(NUM_TABLES));
    }
    
    @Test
    public void should_find_nested_content_using_custom_facade_type_list() {
    	Table table = findDataTable(indexPage.customFacadeTables);
		List<String> csvContent = table.getBodyRows().stream()
				.map(row -> row.getCsvContent())
				.collect(Collectors.toList());

		assertThat(csvContent, everyItem(is(DATA_TABLE_ROW_CONTENT)));
    }
    
    @Test
    public void should_find_nested_content_using_web_element_facade_list() {
    	WebElementFacade elm = findDataTable(indexPage.facadeTables);
    	Table table = tableFromElement(indexPage, elm);
		List<String> csvContent = table.getBodyRows().stream()
				.map(row -> row.getCsvContent())
				.collect(Collectors.toList());

    	assertThat(csvContent, everyItem(is(DATA_TABLE_ROW_CONTENT)));
    }
    
    @Test
    public void should_find_nested_content_using_web_element_list() {
    	WebElement elm = findDataTable(indexPage.elementTables);
    	Table table = tableFromElement(indexPage, elm);
    	List<String> csvContent = table.getBodyRows().stream()
				.map(row -> row.getCsvContent())
				.collect(Collectors.toList());

    	assertThat(csvContent, everyItem(is(DATA_TABLE_ROW_CONTENT)));
    }

}

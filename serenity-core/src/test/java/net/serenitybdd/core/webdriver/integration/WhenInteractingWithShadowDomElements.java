package net.serenitybdd.core.webdriver.integration;

public class WhenInteractingWithShadowDomElements {

//    public class IndexPage extends PageObject {
//        public IndexPage(WebDriver driver, int timeout) {
//            super(driver, timeout);
//        }
//    }
//
//    public class IndexPageWithShortTimeout extends PageObject {
//
//        public WebElement checkbox;
//
//        public IndexPageWithShortTimeout(WebDriver driver, int timeout) {
//            super(driver, timeout);
//        }
//    }
//
//    IndexPage indexPage;
//
//    MockEnvironmentVariables environmentVariables;
//
//    Configuration configuration;
//
//    static WebDriver driver;
//
//    @BeforeClass
//    public static void openDriver() {
//        WebDriverManager.chromedriver().setup();
//        ChromeOptions options = new ChromeOptions();
//    //    options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200");
//        driver = new ChromeDriver(options);
//    }
//
//    @Before
//    public void openLocalStaticSite() {
//        openStaticTestSite("page-with-shadow-dom.html");
//        indexPage = new IndexPage(driver, 1);
//        indexPage.setWaitForTimeout(100);
//    }
//
//    @AfterClass
//    public static void closeDriver() {
//    	if (driver != null) {
//	        driver.close();
//	        driver.quit();
//    	}
//    }
//
//    @Before
//    public void initConfiguration() {
//        environmentVariables = new MockEnvironmentVariables();
//        configuration = new SystemPropertiesConfiguration(environmentVariables);
//    }
//
//    private void openStaticTestSite(String pageName) {
//        File baseDir = new File(System.getProperty("user.dir"));
//        File testSite = new File(baseDir, "src/test/resources/static-site/" + pageName);
//        driver.get("file://" + testSite.getAbsolutePath());
//    }
//
//    @Test
//    public void should_find_page_title() {
//        assertThat(indexPage.getTitle(), is("Test: elements with Shadow-DOM"));
//    }
//
//    @Test
//    public void locate_a_shadow_dom_element() {
//        WebElementFacade shadowInput = indexPage.find(ByShadow.cssSelector("#inputInShadow","#shadow-host"));
//        assertTrue(shadowInput.isVisible());
//    }
//
//    @Test
//    public void locate_a_nested_shadow_dom_element() {
//        WebElementFacade shadowInput = indexPage.find(ByShadow.cssSelector("#inputInInnerShadow","#shadow-host", "#inner-shadow-host"));
//        assertTrue(shadowInput.isVisible());
//    }
//
//    @Test
//    public void locate_nested_shadow_dom_elements() {
//        ListOfWebElementFacades shadowInputs = indexPage.findAll(ByShadow.cssSelector(".test-class","#shadow-host"));
//        assertThat(shadowInputs.size(), equalTo(2));
//    }
//
//    @Test
//    public void interact_with_a_shadow_dom_element() {
//        WebElementFacade shadowInput = indexPage.find(ByShadow.cssSelector("#inputInShadow","#shadow-host"));
//        shadowInput.sendKeys("Some value");
//        assertThat(shadowInput.getValue(), is("Some value"));
//    }
//
//    @Test
//    public void interact_with_a_nested_shadow_dom_element() {
//        WebElementFacade shadowInput = indexPage.find(ByShadow.cssSelector("#inputInInnerShadow","#shadow-host", "#inner-shadow-host"));
//        shadowInput.sendKeys("Some value");
//        assertThat(shadowInput.getValue(), is("Some value"));
//    }

}

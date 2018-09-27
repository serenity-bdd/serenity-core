package integration.pages;

import io.appium.java_client.pagefactory.iOSFindBy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.Assert;

/**
 * Created by khanhdo on 8/30/18.
 */
public class IFixitPage extends MobilePageObject {

    public IFixitPage(WebDriver driver) {
        super(driver);
    }

    @iOSFindBy(xpath="//XCUIElementTypeButton[@label='START A REPAIR']")
    private WebElement StartARepairButton;

    @iOSFindBy(xpath="//*[@label='Car and Truck']")
    private WebElement CarAndTruckText;

    @iOSFindBy(xpath="//*[@label='Acura']")
    private WebElement AcuraText;

    @iOSFindBy(xpath="//XCUIElementTypeNavigationBar")
    private WebElement XCUIElementTypeNavigationBar;

    @iOSFindBy(xpath="//XCUIElementTypeStaticText[@label='Acura Integra']")
    private WebElement AcuraIntegraText;

    @iOSFindBy(xpath="//XCUIElementTypeStaticText[@label='Acura MDX']")
    private WebElement AcuraMDXText;

    @iOSFindBy(xpath="//XCUIElementTypeStaticText[@label='Acura RL']")
    private WebElement AcuraRLText;

    @iOSFindBy(xpath="//XCUIElementTypeStaticText[@label='Acura TL']")
    private WebElement AcuraTLText;

    @iOSFindBy(xpath="//XCUIElementTypeStaticText[@label='Acura TSX']")
    private WebElement AcuraTSXText;

    public void clickOnStartARepairButton(){
        getDriver().getPageSource();
        element(StartARepairButton).click();
    }

    public void clickOnCarAndTruckText() {
        element(CarAndTruckText).click();
    }

    public void clickOnAcuraText() {
        element(AcuraText).click();
    }

    public void waitkOnXCUIElementTypeNavigationBar() {
        (new WebDriverWait(getDriver(), 60))
                .until(ExpectedConditions.elementToBeClickable(XCUIElementTypeNavigationBar));
    }

    public boolean isXCUIElementTypeNavigationBarShown() {
        return element(XCUIElementTypeNavigationBar).isDisplayed();
    }

    public void verifyItems() {
        boolean hasAcuraIntegra = element(AcuraIntegraText).isDisplayed();
        boolean hasAcuraMDX = element(AcuraMDXText).isDisplayed();
        boolean hasAcuraRL = element(AcuraRLText).isDisplayed();
        boolean hasAcuraTL = element(AcuraTLText).isDisplayed();
        boolean hasAcuraTSX = element(AcuraTSXText).isDisplayed();

        Assert.assertEquals(hasAcuraIntegra, true);
        Assert.assertEquals(hasAcuraMDX, true);
        Assert.assertEquals(hasAcuraRL, true);
        Assert.assertEquals(hasAcuraTL, true);
        Assert.assertEquals(hasAcuraTSX, true);
    }
}

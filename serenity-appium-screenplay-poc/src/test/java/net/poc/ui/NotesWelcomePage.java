package net.poc.ui;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSFindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

//public class NotesWelcomePage extends MobilePageObject {
public class NotesWelcomePage {

    //@AndroidFindBy(id="com.example.android.testing.notes:id/fab_add_notes")
    //private WebElementFacade titleField;
    //   @iOSFindBy(xpath="PENDING//XCUIElementTypeButton[@name=\"Next Button\"]")

    public static final Target ADD_NOTE_BUTTON =
            Target.the("Add a note button").located(By.id("com.example.android.testing.notes:id/fab_add_notes"));
    public static final Target NOTES = Target.the("Notes ").located(
            MobileBy.AndroidUIAutomator("new UiSelector().className(\"android.widget.FrameLayout\").clickable(true)"));
//    public static final Target NOTES = Target.the("Notes ").located(
//            MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(new UiSelector().className(\"android.widget.FrameLayout\"))"));
    public static final String TITLE_NOTE_SELECTOR = "new UiSelector().resourceId(\"com.example.android.testing.notes:id/note_detail_title\")";
    public static final String DESCRIPTION_NOTE_SELECTOR = "new UiSelector().resourceId(\"com.example.android.testing.notes:id/note_detail_description\")";

}

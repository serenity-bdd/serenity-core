package net.poc.ui;

import io.appium.java_client.MobileBy;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class CameraViewerPage {

    public static final Target SHUTTER = Target.the("Shutter").located(
            MobileBy.AccessibilityId("click to capture"));

    public static final Target CONFIRM = Target.the("Ok button").located(
            MobileBy.AccessibilityId("OK"));
}

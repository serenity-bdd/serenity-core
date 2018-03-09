package net.poc.ui;

import io.appium.java_client.MobileBy;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class AddNotePage {
    public static final Target NOTE_TITLE_FIELD = Target.the("Note title field").located(
            By.id("com.example.android.testing.notes:id/add_note_title"));

    public static final Target NOTE_DESCRIPTION_FIELD = Target.the("Note description field").located(
            By.id("com.example.android.testing.notes:id/add_note_description"));

    public static final Target CONFIRM_ADD_NOTE_BUTTON = Target.the("AddANote a note button").located(
            By.id("com.example.android.testing.notes:id/fab_add_notes"));

    public static final Target SNACKBAR = Target.the("Snackbar").located(
            By.id("com.example.android.testing.notes:id/snackbar_text"));

    public static final Target MORE_OPTIONS = Target.the("More options").located(
            MobileBy.AccessibilityId("More options"));

    public static final Target ADD_PICTURE_OPTION = Target.the("AddANote picture option").located(
            By.id("com.example.android.testing.notes:id/title"));

    public static final Target CANCEL_ADD_NOTE_BUTTON = Target.the("Cancel button").located(
            MobileBy.AccessibilityId("Navigate up"));;
}

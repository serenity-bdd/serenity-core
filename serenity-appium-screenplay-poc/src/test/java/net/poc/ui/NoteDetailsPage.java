package net.poc.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class NoteDetailsPage {
    public static final Target PICTURE_FIELD = Target.the("Picture field").located(
            By.id("com.example.android.testing.notes:id/note_detail_image"));
}

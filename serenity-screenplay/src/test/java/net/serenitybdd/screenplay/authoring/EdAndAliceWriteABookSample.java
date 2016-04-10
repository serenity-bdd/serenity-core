package net.serenitybdd.screenplay.authoring;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class EdAndAliceWriteABookSample {

    Author alice = new Author("Alice", "alice@example.com", "alice1");
    Editor ed = new Editor("Ed");

    public static RoleAction<Author> submitBook(String title) {
        return new SubmitBookAction();
    }


    @Test
    public void shouldBeAbleToPurchaseSomeItemsWithDelivery() {
    }


}


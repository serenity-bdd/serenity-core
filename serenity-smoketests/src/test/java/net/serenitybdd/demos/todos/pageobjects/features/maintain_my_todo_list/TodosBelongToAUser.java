package net.serenitybdd.demos.todos.pageobjects.features.maintain_my_todo_list;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.annotations.WithTag;
import org.junit.Ignore;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
@WithTag("PageObjects pattern")
@Ignore
public class TodosBelongToAUser {

    // This test requires two browsers running in parallel within a single test.
    // As it's not doable using the Classic Serenity with PageObjects,
    // please use the Screenplay version instead.
}

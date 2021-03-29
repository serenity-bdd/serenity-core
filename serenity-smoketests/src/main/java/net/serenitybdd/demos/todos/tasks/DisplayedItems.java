package net.serenitybdd.demos.todos.tasks;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.demos.todos.pages.components.ToDoList;
import serenityscreenplay.screenplay.Actor;
import serenityscreenplay.screenplay.Question;
import serenityscreenplay.screenplay.abilities.BrowseTheWeb;

import java.util.List;
import java.util.stream.Collectors;

public class DisplayedItems implements Question<List<String>> {

    @Override
    public List<String> answeredBy(Actor actor) {
        return BrowseTheWeb.as(actor).findAll(ToDoList.TODO_ITEMS).stream()
                                     .map(WebElementFacade::getText)
                                     .collect(Collectors.toList());
    }
}

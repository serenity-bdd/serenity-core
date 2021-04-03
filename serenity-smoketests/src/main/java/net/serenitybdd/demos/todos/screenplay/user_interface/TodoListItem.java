package net.serenitybdd.demos.todos.screenplay.user_interface;

import net.serenitybdd.screenplay.targets.Target;

public class TodoListItem {

    public static Target ITEM_LABEL    =  Target.the("the item label")
            .locatedBy("//*[@class='view' and contains(.,'{0}')]//label");

    public static final Target
            COMPLETE_ITEM = Target.the("the complete item tick box")
            .locatedBy("//*[@class='view' and contains(.,'{0}')]//input[@type='checkbox']");

    public static final Target EDIT_ITEM     = Target.the("the edit item field")
            .locatedBy("//li[*[@class='view' and contains(.,'{0}')]]//input[contains(@class, 'edit')]");

    public static final Target DELETE_ITEM   = Target.the("the delete item button")
            .locatedBy("//*[@class='view' and contains(.,'{0}')]//button[@class='destroy']");
}
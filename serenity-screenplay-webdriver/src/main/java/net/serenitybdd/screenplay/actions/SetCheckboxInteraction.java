package net.serenitybdd.screenplay.actions;

public interface SetCheckboxInteraction {
    /**
     * Set a checkbox value to true (selected)
     */
    ClickInteraction toTrue();

    /**
     * Set a checkbox value to false (unselected)
     */
    ClickInteraction toFalse();
}

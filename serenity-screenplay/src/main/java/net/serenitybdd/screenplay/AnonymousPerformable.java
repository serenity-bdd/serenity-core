package net.serenitybdd.screenplay;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.steps.HasCustomFieldValues;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnonymousPerformable implements Performable, HasCustomFieldValues {
    private String title;
    private Map<String, Object> fieldValues = new HashMap();
    private List<Performable> steps;

    public AnonymousPerformable() {}

    public AnonymousPerformable(String title, List<Performable> steps) {
        this.title = title;
        this.steps = steps;
    }

    @Override
    @Step("!#title")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(steps.toArray(new Performable[]{}));
    }

    public void setFieldValue(String fieldName, Object fieldValue) {
        fieldValues.put(fieldName, fieldValue);
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public Map<String, Object> getCustomFieldValues() {
        return new HashMap<>(fieldValues);
    }
}

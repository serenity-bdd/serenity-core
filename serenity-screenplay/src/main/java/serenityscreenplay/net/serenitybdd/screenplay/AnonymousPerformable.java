package serenityscreenplay.net.serenitybdd.screenplay;

import serenitycore.net.serenitybdd.core.steps.HasCustomFieldValues;
import serenitymodel.net.thucydides.core.annotations.Step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnonymousPerformable implements Performable, HasCustomFieldValues {
    private final String title;
    private final Map<String, Object> fieldValues = new HashMap();
    private final List<Performable> steps;

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
    public Map<String, Object> getCustomFieldValues() {
        return new HashMap<>(fieldValues);
    }
}

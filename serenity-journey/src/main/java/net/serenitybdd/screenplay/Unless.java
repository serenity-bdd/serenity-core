package net.serenitybdd.screenplay;

public class Unless {
    public static Performable the(Boolean shouldNotPerform, Performable task) {
        return (shouldNotPerform) ? new SilentPerformable() : task;
    }

    public static Performable the(Question<Boolean> shouldNotPerform, Performable task) {
        return new ConditionalPerformable(shouldNotPerform, task);
    }

    // Synonyms

    public static Performable it(Question<Boolean> shouldNotPerform, Performable task) {
        return new ConditionalPerformable(shouldNotPerform, task);
    }

    public static Performable we(Boolean shouldNotPerform, Performable task) {
        return the(shouldNotPerform, task);
    }

}

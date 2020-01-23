package net.serenitybdd.screenplay;

class EatsImmutableFruit implements Performable {

    private final String fruit;

    EatsImmutableFruit(String fruit) {
        this.fruit = fruit;
    }

    public static EatsImmutableFruit ofType(String type) {
        return Tasks.instrumented(EatsImmutableFruit.class, type);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
    }
}